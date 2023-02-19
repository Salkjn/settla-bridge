package de.settla.utilities.local.guis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Guis {

	public static interface ISpine {
		
		String title();
		int lines();
		boolean equal(Inventory inventory);
		
	}
	
	public static interface IGui {
		
		Player viewer();
		void close();
		IPage main();
		void setMain(IPage page);
		void open(IPage page);
		
		Inventory getInventory();
		void setInventory(Inventory inventory);
		
	}
	
	public static interface IPage extends PageListener {
		
		ISpine spine();
		IGui gui();
		IPage root();
		Map<String, IPage> pages();
		Map<Integer, AItemStack> items();
		void throughtItems(BiConsumer<Integer, AItemStack> item);
		void open();
		void add(String id, IPage page);
		
	}
	
	public static interface GuiListener {
		
		void onGuiClickEvent(InventoryClickEvent event);
		void onGuiDragEvent(InventoryDragEvent event);
		void onGuiCloseEvent(InventoryCloseEvent event);
		
	}
	
	public static interface PageListener {
		
		void pageClickEvent(InventoryClickEvent event);
		void pageDragEvent(InventoryDragEvent event);
		void pageCloseEvent(InventoryCloseEvent event);
		
		void onPageClickEvent(InventoryClickEvent event);
		void onPageDragEvent(InventoryDragEvent event);
		void onPageCloseEvent(InventoryCloseEvent event);
		
	}
	
	private final List<Gui> guis = new ArrayList<>();
	
	private final Object lock = new Object();
	
	public void register(Gui gui) {
		synchronized (lock) {
			guis.add(gui);
		}
	}
	
	public Gui get(Inventory inventory) {
		synchronized (lock) {
			for (Gui gui : guis) {
				if(gui.isInventory(inventory))
					return gui;
			}
			return null;
		}
	}
	
	public void unregister(Gui gui) {
		
//		System.out.println("UNREGISTER: gui");
		
		synchronized (lock) {
			guis.remove(gui);
		}
	}
	
	public void unregister(Inventory inventory) {
		
//		System.out.println("UNREGISTER: inventory");
		
		synchronized (lock) {
			int i = 0;
			while (i < guis.size()) {
				Gui gui = guis.get(i);
				if(gui.isInventory(inventory)) {
					guis.remove(i);
				} else {
					i++;
				}
			}
		}
	}
	
	public void unregister(Player player) {
		
//		System.out.println("UNREGISTER: player");
		
		synchronized (lock) {
			int i = 0;
			while (i < guis.size()) {
				Gui gui = guis.get(i);
				if(gui.viewer() == player) {
					guis.remove(i);
				} else {
					i++;
				}
			}
		}
	}
	
	public void closeAll() {
		synchronized (lock) {
			guis.forEach(gui -> gui.close());
			guis.clear();
		}
	}
	
	public static ItemStack safe(ItemStack item) {
		if(item == null || item.getType() == Material.AIR) {
			return item;
		}
		NBTItem nbt = new NBTItem(item);
		nbt.setString("safe", "b");
		return nbt.getItem();
	}
	
	public static ItemStack unsafe(ItemStack item) {
		if(item == null || item.getType() == Material.AIR) {
			return item;
		}
		NBTItem nbt = new NBTItem(item);
		nbt.remove("safe");
		return nbt.getItem();
	}
	
	public static boolean isSafe(ItemStack item) {
		return item == null ? false : (item.getType() == Material.AIR ? false : new NBTItem(item).getString("safe").equalsIgnoreCase("b"));
	}
	
	public static class Spine implements ISpine {
		
		private int lines;
		private String title;
		
		public Spine(int lines, String title) {
			super();
			this.lines = lines;
			this.title = title;
		}
		
		public Spine(Spine spine) {
			super();
			this.lines = spine.lines;
			this.title = spine.title;
		}

		@Override
		public String title() {
			return title;
		}

		@Override
		public int lines() {
			return lines;
		}

		@Override
		public boolean equal(Inventory inventory) {
			return inventory == null ? false : inventory.getTitle().equalsIgnoreCase(title) && inventory.getSize() == lines * 9;
		}

	}
	
	public static class Gui implements IGui, GuiListener {
		
		private final Player player;
		private IPage root;
		
		private Inventory inventory;
		private IPage current;
		
		private final Object lock = new Object();
		
		public Gui(Guis guis, Player player, IPage root, Inventory inventory) {
			super();
			guis.register(this);
			this.player = player;
			this.root = root;
			this.inventory = inventory;
		}
		
		@Override
		public Player viewer() {
			return player;
		}
		
		@Override
		public IPage main() {
			synchronized (lock) {
				return root;
			}
		}
		
		@Override
		public Inventory getInventory() {
			synchronized (lock) {
				return inventory;
			}
		}
		
		@Override
		public void setInventory(Inventory inventory) {
			synchronized (lock) {
				this.inventory = inventory;
			}
		}

		@Override
		public void setMain(IPage page) {
			synchronized (lock) {
				this.root = page;
			}
		}

		@Override
		public void close() {
			synchronized (lock) {
				viewer().closeInventory();
				inventory = null;
			}
		}

		@Override
		public void onGuiClickEvent(InventoryClickEvent event) {
			if(current != null) {
				current.pageClickEvent(event);
			}
		}

		@Override
		public void onGuiDragEvent(InventoryDragEvent event) {
			if(current != null) {
				current.pageDragEvent(event);
			}
		}
		
		@Override
		public void onGuiCloseEvent(InventoryCloseEvent event) {
			if(current != null) {
				current.pageCloseEvent(event);
			}	
		}

		@Override
		public void open(IPage page) {
			synchronized (lock) {
				final Inventory inventory = (getInventory() == null || !page.spine().equal(getInventory())) ? Bukkit.createInventory(null, page.spine().lines() * 9, page.spine().title()) : getInventory();
				inventory.clear();
				page.throughtItems((slot, item) -> {
					if(item != null && item.getItemStack() != null)
						inventory.setItem(slot, Guis.safe(item.getItemStack()));
				});
				current = page;
				
				if(page.spine().equal(getInventory())) {
					setInventory(inventory);
					viewer().updateInventory();
				} else {
					setInventory(inventory);
					viewer().openInventory(inventory);
				}
			}
		}
		
		public boolean isInventory(Inventory inventory) {
			synchronized (lock) {
				if(getInventory() == null)
					return false;
				String str1 = getInventory().toString().split("@")[1];
				String str2 = inventory.toString().split("@")[1];
				
				return str1.equals(str2);
			}
		}
	}
	
	public static class AItemStack {
		
		private ItemStack itemStack;
		private String id;

		public AItemStack(ItemStack itemStack, String id) {
			super();
			this.itemStack = itemStack;
			this.id = id;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}

		public void setItemStack(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
	}
	
	public static class Page implements IPage {
		
		private final IGui gui;
		private final IPage root;
		private final ISpine spine;
		private final Map<Integer, AItemStack> items;
		private final Map<String, IPage> pages = new HashMap<>();
		private final Map<String, Consumer<ItemStack>> actions = new HashMap<>(); 
		
		private final Object lock = new Object();
		
		public Page(IGui gui, IPage root, ISpine spine, Map<Integer, AItemStack> items) {
			super();
			this.gui = gui;
			this.spine = spine;
			this.root = root;
			this.items = items;
		}

		@Override
		public IGui gui() {
			return gui;
		}

		@Override
		public IPage root() {
			return root;
		}

		@Override
		public Map<String, IPage> pages() {
			synchronized (lock) {
				return pages;
			}
		}

		@Override
		public void add(String id, IPage page) {
			synchronized (lock) {
				pages.put(id, page);
			}	
		}
		
		@Override
		public Map<Integer, AItemStack> items() {
			synchronized (lock) {
				return items;
			}
		}

		@Override
		public void open() {
			synchronized (lock) {
				gui.open(this);
			}
		}

		@Override
		public void throughtItems(BiConsumer<Integer, AItemStack> item) {
			synchronized (lock) {
				items.entrySet().forEach(e -> item.accept(e.getKey(), e.getValue()));
			}
		}

		@Override
		public ISpine spine() {
			synchronized (lock) {
				return spine;
			}
		}

		public void setItem(int slot, ItemStack item) {
			synchronized (lock) {
				gui.getInventory().setItem(slot, Guis.safe(item));
			}
		}
		
		
		public void addItem(int slot, ItemStack item) {
			synchronized (lock) {
				items.put(slot, new AItemStack(item, "dummy"));
			}
		}
		
		public AItemStack getAItem(String id) {
			synchronized (lock) {
				AItemStack item = items.values().stream().filter(a -> (a.getId() != null && a.getId().equalsIgnoreCase(id))).findFirst().orElse(null);
				return item == null ? null : item;
			}
		}
		
		public ItemStack getItem(String id) {
			synchronized (lock) {
				for (ItemStack i : gui().getInventory().getContents()) {
					if (i != null) {
						NBTItem nbt = new NBTItem(i);
						if (nbt.getString("id").equalsIgnoreCase(id)) {
							return i;
						}
					}
				}
				return null;
			}
		}
		
		public void addAction(String item, Consumer<ItemStack> action) {
			synchronized (lock) {
				actions.put(item, action);
			}
		}
		
		@Override
		final public void pageClickEvent(InventoryClickEvent event) {
			
			ItemStack item = event.getCurrentItem();
			
			if(event.getClick() == ClickType.LEFT && item != null && item.getType() != Material.AIR) {
				NBTItem nbt = new NBTItem(item);
				String id = nbt.getString("id");
				
				if(id != null && !id.isEmpty()) {
					synchronized (lock) {
						Entry<String, Consumer<ItemStack>> action = actions.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(id)).findFirst().orElse(null);
						if(action != null) {
							action.getValue().accept(item);
						} else {
							Entry<String, IPage> page = pages.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(id)).findFirst().orElse(null);
							if(page != null) {
								event.setCancelled(true);
								page.getValue().open();
								return;
							}
						}
					}
				}
			}
			
			onPageClickEvent(event);
			
		}

		@Override
		final public void pageDragEvent(InventoryDragEvent event) {
			onPageDragEvent(event);
		}
		
		@Override
		final public void pageCloseEvent(InventoryCloseEvent event) {
			onPageCloseEvent(event);
		}
		
		@Override
		public void onPageDragEvent(InventoryDragEvent event) {
			event.setCancelled(true);
		}
		
		@Override
		public void onPageClickEvent(InventoryClickEvent event) {
			event.setCancelled(true);
		}
		
		@Override
		public void onPageCloseEvent(InventoryCloseEvent event) {
	
		}
		
	}
	
}
