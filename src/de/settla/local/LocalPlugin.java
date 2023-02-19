package de.settla.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.settla.economy.Currency;
import de.settla.economy.LocalEconomy;
import de.settla.local.beam.LocalBeamModule;
import de.settla.local.cloud.LocalCloudModule;
import de.settla.local.economysigns.EconomySignTopModule;
import de.settla.local.keys.KeyModule;
import de.settla.local.kits.LocalKitData;
import de.settla.local.kits.LocalKitModule;
import de.settla.local.lobby.LobbyModule;
import de.settla.local.npc.NpcModule;
import de.settla.local.portals.LocalPortalsModule;
import de.settla.local.protection.ProtectionModule;
import de.settla.local.tools.SpecialItemModule;
import de.settla.local.tutorial.TutorialModule;
import de.settla.local.warp.LocalWarpPointModule;
import de.settla.utilities.Utility;
import de.settla.utilities.local.commands.Command;
import de.settla.utilities.local.commands.Commands;
import de.settla.utilities.local.guis.AnvilGuiModule;
import de.settla.utilities.local.guis.GuiModule;
import de.settla.utilities.local.playerdata.LocalPlayer;
import de.settla.utilities.local.playerdata.LocalPlayers;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.local.region.space.TemplateModule;
import de.settla.utilities.local.region.space.generation.GenerationModule;
import de.settla.utilities.module.Module;
import de.settla.utilities.module.ModuleManager;
import de.settla.utilities.sakko.SakkoAddress;
import de.settla.utilities.sakko.SakkoClient;
import de.settla.utilities.sakko.protocol.SakkoProtocol;
import de.settla.utilities.storage.Database;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable.Memory;
import de.settla.utilities.storage.Storage;

public class LocalPlugin extends JavaPlugin implements ModuleManager {

	private static LocalPlugin instance;
	
	private SakkoProtocol protocol;
	private LocalEconomy economy;
	private Commands commands;
	private LocalConfig config;
	private Storage<LocalPlayers> localPlayers;
	
	private final Map<Class<?>, Module<?>> modules = new HashMap<>();
	
	@Override
	public void onEnable() {
		
		StaticParser.register();
		
		instance = this;
		Utility.init();
		config = new LocalConfig(this);
		commands = new Commands(this);
		protocol = SakkoProtocol.createSakkoProtocol(SakkoClient.createSakkoClient(new SakkoAddress(Utility.DEFAULT_ADDRESS, Utility.DEFAULT_PORT), msg -> {}));
		economy = new LocalEconomy(protocol, new Currency("CCoins", "$"));
		
		initLocalPlayers();
		
		VaultHelper.setupPermissions();
		VaultHelper.setupEconomy();
		
		//modules adding
		modules.put(GuiModule.class, new GuiModule(this));
		modules.put(AnvilGuiModule.class, new AnvilGuiModule(this));
		if (config.isModuleNpc())
			modules.put(NpcModule.class, new NpcModule(this));
		
		modules.put(Universe.class, new Universe(this));
		modules.put(TemplateModule.class, new TemplateModule(this));
		modules.put(GenerationModule.class, new GenerationModule(this));
		modules.put(SpecialItemModule.class, new SpecialItemModule(this));
		modules.put(KeyModule.class, new KeyModule(this));
		
		modules.put(ProtectionModule.class, new ProtectionModule(this));
		
		modules.put(LocalBeamModule.class, new LocalBeamModule(this, protocol));
		modules.put(LocalPortalsModule.class, new LocalPortalsModule(this, protocol));
		
		modules.put(LocalWarpPointModule.class, new LocalWarpPointModule(this, protocol));
		modules.put(LocalCloudModule.class, new LocalCloudModule(this, protocol));
		
		if (config.isModuleEconomysigns())
			modules.put(EconomySignTopModule.class, new EconomySignTopModule(this));
		if (config.isModuleTutorial())
			modules.put(TutorialModule.class, new TutorialModule(this));
		if (config.isModuleKit())
			modules.put(LocalKitModule.class, new LocalKitModule(this, protocol));
		if (config.isLobby()) {
			VaultHelper.setupPermissions();
			modules.put(LobbyModule.class, new LobbyModule(this));
		}

		enable();
		
	}
	
	@Override
	public void onDisable() {
		localPlayers.run();
		disable();
	}
	
	public static LocalPlugin getInstance() {
		return instance;
	}

	public LocalConfig getLocalConfig() {
		return config;
	}

	public LocalEconomy getEconomy() {
		return economy;
	}
	
	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}
	
	@Override
	public <M extends Module<?>> M getModule(Class<M> clazz) {
		return clazz.cast(modules.get(clazz));
	}

	@Override
	public void disable() {
		modules.values().forEach(m -> m.onDisable());
	}

	@Override
	public void enable() {
		modules.values().forEach(m -> m.onPreEnable());
		modules.values().forEach(m -> m.onEnable());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <M extends Module<?>> void modules(Consumer<M> consumer) {
		modules.values().forEach((Consumer<? super Module<?>>) consumer);
	}
	
	//LOCAL ADDED FUNCTIONS:
	
	public void registerCommand(Command cmd) {
		commands.registerCommand(cmd);
	}
	
	public void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public LocalPlayers getLocalPlayers() {
		return localPlayers.object();
	}
	
	private void initLocalPlayers() {
		Memory.register(LocalPlayers.class, map -> new LocalPlayers(map));
		Memory.register(LocalPlayer.class, map -> new LocalPlayer(map));
		
		LocalPlayer.addDefaultData(uuid -> new LocalKitData(), LocalKitData.class);
		
		Database<LocalPlayers> database = new Database<>("players", new File(getDataFolder(), "/local_players.data"), s -> new LocalPlayers(), LocalPlayers.class);
		localPlayers = new Storage<>(database);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(LocalPlugin.getInstance(), localPlayers, 0, 20 * 60 * 4);
	}
	
	final public YamlConfiguration loadConfig(String path) {
		if (!getDataFolder().exists())
			getDataFolder().mkdirs();
		File file = new File(getDataFolder(), path);
		if (!file.exists()) {
			try (InputStream in = getResource(path)) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(new File(getDataFolder(), path));
	}
	
	final public void loadResource(String path, String endPath) {
		File file = new File(getDataFolder(), endPath);
		
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		
		if (!file.exists()) {
			try (InputStream in = getResource(path)) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	final public void saveConfig(YamlConfiguration config, String path) {

		File file = new File(getDataFolder(), path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");

		if (!tempFile.exists() && tempFile.getParentFile() != null)
			tempFile.getParentFile().mkdirs();

		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		
		try {
			config.save(tempFile);
			file.delete();
			if (!tempFile.renameTo(file)) {
				try {
					throw new Exception("Failed to rename temporary file to " + file.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
		}
	}
	
}
