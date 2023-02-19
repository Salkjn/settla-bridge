package de.settla.local;

import org.bukkit.configuration.file.YamlConfiguration;

public class LocalConfig {

	private String serverName;

	// private boolean inventory, level, hearts, food, enderchest, effects;

	private boolean adminshop, npc, kit, tutorial, economysigns, blocks;

	private boolean lobby;

	public LocalConfig(LocalPlugin plugin) {
		YamlConfiguration config = plugin.loadConfig("config.yml");

		// inventory = config.getBoolean("synchronized.inventory");
		// enderchest = config.getBoolean("synchronized.enderchest");
		// level = config.getBoolean("synchronized.level");
		// hearts = config.getBoolean("synchronized.hearts");
		// food = config.getBoolean("synchronized.food");
		// effects = config.getBoolean("synchronized.effects");

		serverName = config.getString("sakko.serverName");

		adminshop = config.getBoolean("modules.adminshop");
		npc = config.getBoolean("modules.npc");
		kit = config.getBoolean("modules.kit");
		tutorial = config.getBoolean("modules.tutorial");
		economysigns = config.getBoolean("modules.economysigns");
		blocks = config.getBoolean("modules.blocks");

		lobby = config.getBoolean("modules.lobby");
		
		System.out.println("  ");
		System.out.println("MODULES:  (Server: " + serverName + ")");
		System.out.println("> AdminShop: " + (adminshop ? "activated" : "disabled"));
		System.out.println("> NPC: " + (npc ? "activated" : "disabled"));
		System.out.println("> KIT: " + (kit ? "activated" : "disabled"));
		System.out.println("> Tutorial: " + (tutorial ? "activated" : "disabled"));
		System.out.println("> EconomySigns: " + (economysigns ? "activated" : "disabled"));
		System.out.println("> Blocks: " + (blocks ? "activated" : "disabled"));
		System.out.println("> Lobby: " + (lobby ? "activated" : "disabled"));
		System.out.println("  ");

	}

	public String getServerName() {
		return serverName;
	}

	// public boolean hasSynchronizedInventory() {
	// return inventory;
	// }
	//
	// public boolean hasSynchronizedEnderChest() {
	// return enderchest;
	// }
	//
	// public boolean hasSynchronizedLevel() {
	// return level;
	// }
	//
	// public boolean hasSynchronizedHearts() {
	// return hearts;
	// }
	//
	// public boolean hasSynchronizedFood() {
	// return food;
	// }
	//
	// public boolean hasSynchronizedEffects() {
	// return effects;
	// }

	public boolean isModuleAdminshop() {
		return adminshop;
	}

	public boolean isModuleNpc() {
		return npc;
	}

	public boolean isModuleKit() {
		return kit;
	}

	public boolean isModuleTutorial() {
		return tutorial;
	}

	public boolean isModuleEconomysigns() {
		return economysigns;
	}

	public boolean isModuleBlocks() {
		return blocks;
	}

	public boolean isLobby() {
		return lobby;
	}

}
