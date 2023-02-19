package de.settla.local.keys;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.settla.local.LocalPlugin;
import de.settla.local.basic.BasicModule;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.region.Universe;

public class KeyModule extends BasicModule {

	private static KeyModule instance;
	private KeyDatabase keyDatabase;

	public static int UPDATE_SLOT;
	public static int UPDATE_SPEED;
	public static int UPDATE_TIME;
	public static String MESSAGE_NO_KEY;
	public static String MESSAGE_REDEEM_KEY;
	public static String MESSAGE_SUCCESS_KEY;
	public static String INVENTORY_CHANCE;
	public static String MESSAGE_CMD_RELOAD;

	public static String MESSAGE_CMD_CREATE_FAIL;
	public static String MESSAGE_CMD_CREATE_SUCCESS;
	public static String MESSAGE_CMD_DELETE;
	public static String MESSAGE_CMD_PERMISSION;
	public static String MESSAGE_CMD_KNOCKBACK;
	public static String MESSAGE_CMD_LOCATION_ADD_SUCCESS;
	public static String MESSAGE_CMD_LOCATION_REMOVE_NO_BLOCK;
	public static String MESSAGE_CMD_LOCATION_REMOVE_FAIL;
	public static String MESSAGE_CMD_LOCATION_REMOVE_SUCCESS;
	public static String MESSAGE_CMD_ITEM_ADD_NO_ITEM;
	public static String MESSAGE_CMD_ITEM_ADD_SIMILAR;
	public static String MESSAGE_CMD_ITEM_ADD_SUCESS;
	public static String MESSAGE_CMD_ITEM_REMOVE_FAIL;
	public static String MESSAGE_CMD_ITEM_REMOVE_SUCCESS;
	public static String MESSAGE_CMD_GIVE_PLAYER;
	public static String MESSAGE_CMD_OPEN_PLAYER;
	public static String MESSAGE_CMD_TP_FAIL;
	public static String MESSAGE_CMD_TP_SUCCESS;

	public KeyModule(LocalPlugin plugin) {
		super(plugin);
		instance = this;
	}

	public static KeyModule getInstance() {
		return instance;
	}

	@Override
	public void enable() {

		initConfig();
//		initGuis();

		ConfigurationSerialization.registerClass(Key.class, "Key");
		ConfigurationSerialization.registerClass(KeyItem.class, "KeyItem");

		getModuleManager().getModule(Universe.class).addToWaitingActionList(() -> {
			keyDatabase = new KeyDatabase(KeyModule.this, "keys", "keys.yml");
			registerDatabase(keyDatabase);
			registerListener(new KeyListener(KeyModule.this));
			sendMessage("Es wurden " + keyDatabase.size() + " Keys geladen!");
			getModuleManager().registerCommand(new KeyCommand("key"));
		});

	}
	
	@Override
	public void disable() {
		
	}

	private void initConfig() {
		YamlConfiguration config = getConfig();

		if (config.get("update.slot") == null)
			config.set("update.slot", 13);
		UPDATE_SLOT = config.getInt("update.slot");
		if (config.get("update.speed") == null)
			config.set("update.speed", 10);
		UPDATE_SPEED = config.getInt("update.speed");
		if (config.get("update.time") == null)
			config.set("update.time", 3);
		UPDATE_TIME = config.getInt("update.time");

		INVENTORY_CHANCE = configMessage("inventory.chance", "&eChance: %chance%");
		MESSAGE_NO_KEY = configMessage("message.no_key", "§8[§2Key§8] &7Du hast nicht den passenden Key in der Hand.");
		MESSAGE_REDEEM_KEY = configMessage("message.redeem_key",
				"§8[§2Key§8] &7Du hast &c%key% &7eingelöst! (Item anklicken!)");
		MESSAGE_SUCCESS_KEY = configMessage("message.success_key",
				"§8[§2Key§8] &7Du hast den Key §c%key% §7eingelöst und §c%amount% %item%§7 bekommen.");
		MESSAGE_CMD_RELOAD = configMessage("message.reload", "§8[§2Key§8] &7Du hast die Config erfolgreich reloaded.");

		MESSAGE_CMD_CREATE_FAIL = configMessage("message.cmd.create_fail", "§8[§2Key§8] &7Es gibt bereits ein Key mit der ID:%id%");
		MESSAGE_CMD_CREATE_SUCCESS = configMessage("message.cmd.create_success",
				"§8[§2Key§8] &7Du hast erfolgreich den Key §a%key%&7 mit der ID:%id% erstellt.");
		MESSAGE_CMD_DELETE = configMessage("message.cmd.delete", "§8[§2Key§8] &7Du hast %key%&7 gelöscht!");
		MESSAGE_CMD_PERMISSION = configMessage("message.cmd.permission",
				"§8[§2Key§8] &7Der Key %key%&7 hat nun die Permission:%permission%");
		MESSAGE_CMD_KNOCKBACK = configMessage("message.cmd.knockback",
				"§8[§2Key§8] &7Der Key %key%&7 hat nun %knockback% Rückstoß.");
		MESSAGE_CMD_LOCATION_ADD_SUCCESS = configMessage("message.cmd.location.add_success",
				"§8[§2Key§8] &7Du hast dem Key %key%&7 einen neuen Block hinzugefügt.");
		MESSAGE_CMD_LOCATION_REMOVE_NO_BLOCK = configMessage("message.cmd.location.remove_no_block",
				"&7Der Block ist zu weit weg! (Du musst einen Block anschauen)");
		MESSAGE_CMD_LOCATION_REMOVE_FAIL = configMessage("message.cmd.location.remove_fail",
				"§8[§2Key§8] &7Dieser Block gehört zu keinem Key!");
		MESSAGE_CMD_LOCATION_REMOVE_SUCCESS = configMessage("message.cmd.location.remove_success",
				"§8[§2Key§8] &7Der Block wurde von dem Key %key%&7 entfernt.");
		MESSAGE_CMD_ITEM_ADD_NO_ITEM = configMessage("message.cmd.item.add_no_item",
				"§8[§2Key§8] &7Du musst ein Item in der Hand halten!");
		MESSAGE_CMD_ITEM_ADD_SIMILAR = configMessage("message.cmd.item.add_similar",
				"§8[§2Key§8] &7Dieses Item ist bereist ein Element.");
		MESSAGE_CMD_ITEM_ADD_SUCESS = configMessage("message.cmd.item.add_success",
				"§8[§2Key§8] &7Item erfolgreich dem Key %key%&7 hinzugefügt. (Chance: %chance%)");
		MESSAGE_CMD_ITEM_REMOVE_FAIL = configMessage("message.cmd.item.remove_fail",
				"§8[§2Key§8] &7Die Zahl ist zu groß! (%key%&7 hat weniger Elemente)");
		MESSAGE_CMD_ITEM_REMOVE_SUCCESS = configMessage("message.cmd.item.remove_success",
				"§8[§2Key§8] &7Du hast das Item (Slot:%slot%) entfernt.");
		MESSAGE_CMD_GIVE_PLAYER = configMessage("message.cmd.give_player",
				"§8[§2Key§8] &7Du hast %player% %amount% %key%&7 gegeben!");
		MESSAGE_CMD_OPEN_PLAYER = configMessage("message.cmd.open_player",
				"§8[§2Key§8] &7Der Spieler %player% hat einen Key %key%&7 eingelöst.");
		MESSAGE_CMD_TP_FAIL = configMessage("message.cmd.tp_fail", "§8[§2Key§8] &7Der Key %key%&7 hat nicht so viele Blöcke...");
		MESSAGE_CMD_TP_SUCCESS = configMessage("message.cmd.tp_success",
				"§8[§2Key§8] &7Du wurdest zu dem %id%'ten Block des Keys %key%&7 teleportiert.");

		saveConfig();
	}

	// private void updateEffects() {
	// for (String id : getKeys()) {
	// Key key = getKey(id);
	// if(key == null)
	// continue;
	// key.update();
	// }
	// }

	private String configMessage(String id, String def) {
		YamlConfiguration config = getConfig();
		String message = config.getString(id);
		if (message == null || message.isEmpty()) {
			config.set(id, def);
			return def;
		} else {
			return message;
		}
	}

	public static String convert(String message, String[][] replacements) {
		return replacements == null ? ChatColor.translateAlternateColorCodes('&', message)
				: ChatColor.translateAlternateColorCodes('&', Utils.replace(message, replacements));
	}

	public void giveKeys(Key key, int amount, Collection<? extends Player> collection) {
		if (key.getKeyItemStack() == null)
			return;
		for (Player player : collection) {
			for (int i = 0; i < amount; i++) {
				player.getInventory().addItem(key.getKeyItemStack());
			}
		}
	}

	public void giveKeys(Key key, int amount, Collection<? extends Player> collection, String... worlds) {
		if (key.getKeyItemStack() == null)
			return;
		for (Player player : collection) {
			for (int i = 0; i < amount; i++) {
				for (String world : worlds) {
					if (player.getWorld().getName().equalsIgnoreCase(world))
						player.getInventory().addItem(key.getKeyItemStack());
				}
			}
		}
	}

	public void giveItemStack(Player player, ItemStack itemstack) {

		if (itemstack == null)
			return;

		// TODO check the inventory size...
		
		if (Utils.hasEnoughPlaceFor(Arrays.asList(player.getInventory().getContents()), itemstack, itemstack.getAmount())) {
			Map<Integer, ItemStack> map = player.getInventory().addItem(itemstack);
			if (map == null || map.isEmpty())
				return;
			for (Integer slot : map.keySet()) {
				ItemStack item = map.get(slot);
				player.getWorld().dropItem(player.getLocation(), item);
			}
		} else {
			player.sendMessage(ChatConvention.title("Key") + "Achtung: Dein Inventar ist voll!");
			new BukkitRunnable() {
				@Override
				public void run() {
					Map<Integer, ItemStack> map = player.getInventory().addItem(itemstack);
					for (Integer slot : map.keySet()) {
						ItemStack item = map.get(slot);
						player.getWorld().dropItem(player.getLocation(), item);
					}
				}
			}.runTask(getModuleManager());
		}
	}

	public Key getKey(Location location) {
		if (!ready())
			return null;
		for (String id : keyDatabase.getKeys()) {
			Key key = keyDatabase.getKey(id);
			if (key != null && key.overlaps(location))
				return key;
		}
		return null;
	}

	private boolean ready() {
		return keyDatabase != null;
	}

	public void addKey(Key key) {
		keyDatabase.addKey(key);
	}

	public void removeKey(String id) {
		keyDatabase.removeKey(id);
	}

	public Key getKey(String id) {
		return keyDatabase.getKey(id);
	}

	public boolean hasKey(String id) {
		return keyDatabase.hasKey(id);
	}

	public int size() {
		return keyDatabase.getKeys().size();
	}

	public Set<String> getKeys() {
		return keyDatabase.getKeys();
	}

}
