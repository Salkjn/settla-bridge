package de.settla.utilities.local.guis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.guis.Guis.AItemStack;
import de.settla.utilities.local.guis.Guis.Spine;

public class GuiParser {

	private final Map<String, RawItem> items = new HashMap<>();
	private final Map<String, RawInventory> inventories = new HashMap<>();

	private final Object lock = new Object();

	public RawItem getItem(String id) {
		synchronized (lock) {
			return items.get(id);
		}
	}

	public RawInventory getInventory(String id) {
		synchronized (lock) {
			return inventories.get(id);
		}
	}
	
	public void registerItem(RawItem item) {
		synchronized (lock) {
			items.put(item.id, item);
		}
	}
	
	public void registerInventory(RawInventory inventory) {
		synchronized (lock) {
			inventories.put(inventory.id, inventory);
		}
	}

	public void read(File file) {
		if(!file.exists())
			return;
		if(file.isDirectory())
			return;
		try {
			read(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void read(Reader reader) {
		try {
			JSONObject input = (JSONObject) new JSONParser().parse(reader);
			
			if(input.containsKey("INVENTORIES")) {
				List<RawInventory> invs = RawInventory.readAll(input);					
				if(invs != null)
					for (RawInventory simpleInventory : invs)
						if(simpleInventory != null)
							registerInventory(simpleInventory);
				return;
			} else if(input.containsKey("ITEMS")) {
				List<RawItem> items = RawItem.readAll(input);					
				if(items != null)
					for (RawItem simpleItem : items)
						if(simpleItem != null)
							registerItem(simpleItem);
				
				return;
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static class RawItem {

		private final String id;

		private final String title;
		private final List<String> lore;
		private final boolean enchantEffect;
		private final int type;
		private final short data;
		private final Map<String, Long> enchants;

		private RawItem(String id, String title, List<String> lore, boolean enchantEffect, int type, short data,
				Map<String, Long> enchants) {
			this.id = id;
			this.title = title;
			this.lore = lore;
			this.enchantEffect = enchantEffect;
			this.type = type;
			this.data = data;
			this.enchants = enchants;
		}

		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public List<String> getLore() {
			return lore;
		}

		public boolean hasEnchantEffect() {
			return enchantEffect;
		}

		public int getType() {
			return type;
		}

		public short getData() {
			return data;
		}

		public Map<String, Long> getEnchantments() {
			return enchants;
		}
		
		public AItemStack build() {
			@SuppressWarnings("deprecation")
			ItemStack itemstack = new ItemStack(type);
			itemstack.setDurability(data);
			ItemMeta meta = itemstack.getItemMeta();
			meta.setDisplayName(title);
			meta.setLore(lore);
			if(hasEnchantEffect()) {
				meta.addEnchant(Enchantment.DURABILITY, 1, false);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				itemstack.setItemMeta(meta);
			} else {
				itemstack.setItemMeta(meta);
			}
			NBTItem nbt = new NBTItem(itemstack);
			nbt.setString("id", getId());
			return new AItemStack(nbt.getItem(), id);
		}

		@SuppressWarnings("unchecked")
		public static RawItem deserialize(Map<String, Object> input) throws SimpleReaderException {
			String id = (String) input.get("id");
			if (id == null)
				throw new SimpleReaderException("ID");
			String title = (String) input.get("title");
			Boolean enchantEffect = (Boolean) input.get("enchantEffect");
			if (enchantEffect == null)
				enchantEffect = false;
			Long type = (Long) input.get("type");
			if (type == null)
				type = 1L;
			Long data = (Long) input.get("data");
			if (data == null)
				data = 0L;
			List<String> lore = new ArrayList<>();
			List<String> list = (List<String>) input.get("lore");
			if (list != null)
				list.forEach(line -> lore.add(ChatColor.translateAlternateColorCodes('&', line)));
			Map<String, Long> enchantments = (Map<String, Long>) input.get("enchantments");
			return new RawItem(id, ChatColor.translateAlternateColorCodes('&', title), lore, enchantEffect,
					type.intValue(), data.shortValue(), enchantments);
		}

		@SuppressWarnings("unchecked")
		public static List<RawItem> readAll(JSONObject input) {
			List<RawItem> invs = new ArrayList<>();
			JSONArray array = (JSONArray) input.get("ITEMS");
			if (array == null) {
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.RED + "An Error occurred while reading a file. ITEMS WAS NOT FOUND.");
				return null;
			}
			for (Object object : array) {
				try {
					invs.add(deserialize((Map<String, Object>) object));
				} catch (SimpleReaderException e) {
					Bukkit.getConsoleSender()
							.sendMessage(ChatColor.RED + "An Error occurred while reading a file. Please check: "
									+ e.getMessage() + " in object:" + ((JSONObject) object).toJSONString());
				}
			}
			return invs;
		}

	}

	public static class RawInventory {

		private final String id;

		private final HashMap<Integer, String> items;
		private final Spine spine;
		private final String inherit;

		private HashMap<Integer, String> itemsIncludeInherit;

		public RawInventory(String id, HashMap<Integer, String> items, String title, String inherit, int lines) {
			super();
			this.id = id;
			this.items = items;
			this.inherit = inherit;
			this.spine = new Spine(lines, title);
		}

		public String getId() {
			return id;
		}

		public Map<Integer, String> getItems() {
			if (itemsIncludeInherit != null)
				return itemsIncludeInherit;
			if (inherit == null)
				return itemsIncludeInherit = items;
			RawInventory inheritInventory = LocalPlugin.getInstance().getModule(GuiModule.class).getParser()
					.getInventory(inherit);
			if (inheritInventory == null)
				return itemsIncludeInherit = items;
			HashMap<Integer, String> unit = new HashMap<>();
			unit.putAll(inheritInventory.getItems());
			unit.putAll(items);
			return itemsIncludeInherit = unit;
		}

		public String getInherit() {
			return inherit;
		}

		public Spine getSpine() {
			return spine;
		}

		public Map<Integer, AItemStack> buildItems(GuiParser parser) {
			Map<Integer, AItemStack> items = new HashMap<>();
			getItems().entrySet().forEach(e -> items.put(e.getKey(), parser.getItem(e.getValue()).build()));
			return items;
		}
		
		public static RawInventory deserialize(Map<String, Object> input) throws SimpleReaderException {
			String id = (String) input.get("id");
			if (id == null)
				throw new SimpleReaderException("ID");

			String title = (String) input.get("title");
			if (title == null)
				throw new SimpleReaderException("TITLE");

			int lines = (int) ((long) input.get("lines"));
			if (lines < 1)
				throw new SimpleReaderException("LINES");

			String inherit = (String) input.get("inherit");
			JSONArray array = (JSONArray) input.get("items");

			HashMap<Integer, String> items = new HashMap<>();
			Map<String, Integer> placeholder = new HashMap<>();
			if (array != null)
				for (Object ob : array) {
					JSONObject itemObject = (JSONObject) ob;
					String itemId = (String) itemObject.get("item");
					if (itemId != null) {
						Object slot = itemObject.get("slot");
						if (slot != null) {
							int slotInt = (int) (long) slot;
							items.put(slotInt, itemId);
						} else {
							Object slots = itemObject.get("slots");
							String slotsString = (String) slots;
							String[] p = slotsString.split("-");
							if (p.length == 2) {
								try {
									int from = Integer.parseInt(p[0]);
									int until = Integer.parseInt(p[1]);
									for (int i = from; i <= until; i++) {
										items.put(i, itemId);
									}
								} catch (NumberFormatException e) {
									System.err.println(
											"The expression '" + p[0] + "' or '" + p[1] + "' was not a number.");
								}
							}
						}
					} else {
						Object placeholderObject = itemObject.get("placeholder");
						if (placeholderObject != null) {
							String placeholderString = (String) placeholderObject;
							Object slotObject = itemObject.get("slot");
							if (slotObject != null) {
								int slot = (int) (long) slotObject;
								placeholder.put(placeholderString, slot);
							} else {
								// Util.debug("Found placeholder but no item
								// slot.");
							}
						} else {
							// Util.debug("Could not find item or placeholder");
						}
					}
				}
			return new RawInventory(id, items, ChatColor.translateAlternateColorCodes('&', title), inherit, lines);
		}

		@SuppressWarnings("unchecked")
		public static List<RawInventory> readAll(JSONObject input) {
			List<RawInventory> invs = new ArrayList<>();
			JSONArray array = (JSONArray) input.get("INVENTORIES");
			if (array == null) {
				Bukkit.getConsoleSender().sendMessage(
						ChatColor.RED + "An Error occurred while reading a file. INVENTORIES WAS NOT FOUND.");
				return null;
			}
			for (Object object : array) {
				try {
					invs.add(deserialize((JSONObject) object));
				} catch (SimpleReaderException e) {
					Bukkit.getConsoleSender()
							.sendMessage(ChatColor.RED + "An Error occurred while reading a file. Please check: "
									+ e.getMessage() + " in object:" + ((JSONObject) object).toJSONString());
				}
			}
			return invs;
		}
	}
}
