package de.settla.global;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import de.settla.economy.Currency;
import de.settla.economy.GlobalEconomy;
import de.settla.global.beam.GlobalBeamModule;
import de.settla.global.cloud.GlobalCloudModule;
import de.settla.global.commands.EconomyCommand;
import de.settla.global.commands.MoneyCommand;
import de.settla.global.commands.PayCommand;
import de.settla.global.commands.PingCommand;
import de.settla.global.commands.SpawnCommand;
import de.settla.global.essentials.EssentialsModule;
import de.settla.global.guilds.GuildGlobalModule;
import de.settla.global.kits.GlobalKitData;
import de.settla.global.kits.GlobalKitModule;
import de.settla.global.warp.GlobalWarpPointModule;
import de.settla.utilities.Utility;
import de.settla.utilities.functions.Callback;
import de.settla.utilities.global.fetcher.NameCache;
import de.settla.utilities.global.fetcher.UUIDCache;
import de.settla.utilities.global.playerdata.GlobalPlayer;
import de.settla.utilities.global.playerdata.GlobalPlayers;
import de.settla.utilities.module.Module;
import de.settla.utilities.module.ModuleManager;
import de.settla.utilities.sakko.SakkoAddress;
import de.settla.utilities.sakko.SakkoServer;
import de.settla.utilities.sakko.protocol.SakkoProtocol;
import de.settla.utilities.storage.Database;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable.Memory;
import de.settla.utilities.storage.Storage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class GlobalPlugin extends net.md_5.bungee.api.plugin.Plugin implements ModuleManager {

	private static GlobalPlugin instance;

	private SakkoProtocol protocol;
	private UUIDCache uuidCache;
	private NameCache nameCache;
	private GlobalEconomy economy;
	private Storage<GlobalPlayers> globalPlayers;
	private GlobalConfig config;
	
	private final Map<Class<?>, Module<?>> modules = new HashMap<>();
	
	@Override
	public void onEnable() {
		
		StaticParser.register();
		
		instance = this;
		Utility.init();
		uuidCache = new UUIDCache(this);
		nameCache = new NameCache(this);
		
		config = new GlobalConfig(this);
		
		initGlobalPlayers();
		
		GlobalPlayer.addDefaultData(uuid -> new GlobalKitData(), GlobalKitData.class);
		
		protocol = SakkoProtocol.createSakkoProtocol(SakkoServer.createSakkoServer(new SakkoAddress(Utility.DEFAULT_ADDRESS, Utility.DEFAULT_PORT), msg -> {}));
		economy = new GlobalEconomy(protocol, new Currency("Coins", "$"));
		
		registerCommand(new PayCommand("pay"));
		registerCommand(new EconomyCommand("economy", "eco"));
		registerCommand(new MoneyCommand("balance", "bal"));
		registerCommand(new SpawnCommand());
		registerCommand(new PingCommand("ping"));
		
		modules.put(EssentialsModule.class, new EssentialsModule(this));
		modules.put(GlobalBeamModule.class, new GlobalBeamModule(this, protocol));
		modules.put(GlobalWarpPointModule.class, new GlobalWarpPointModule(this, protocol));
		modules.put(GlobalCloudModule.class, new GlobalCloudModule(this, protocol));
		modules.put(GlobalKitModule.class, new GlobalKitModule(this, protocol));
		modules.put(GuildGlobalModule.class, new GuildGlobalModule(this, protocol));
		
		enable();
	}

	@Override
	public void onDisable() {
		economy.save();
		globalPlayers.run();
		disable();
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
	
	private void initGlobalPlayers() {
		Memory.register(GlobalPlayers.class, map -> new GlobalPlayers(map));
		Memory.register(GlobalPlayer.class, map -> new GlobalPlayer(map));
		
		Database<GlobalPlayers> database = new Database<>("players", new File("plugins/SettlaBridge/players.data"), s -> new GlobalPlayers(), GlobalPlayers.class);
		globalPlayers = new Storage<>(database);
		ProxyServer.getInstance().getScheduler().schedule(GlobalPlugin.getInstance(), globalPlayers, 0, 5,
				TimeUnit.MINUTES);
	}
	
	public GlobalEconomy getEconomy() {
		return economy;
	}
	
	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}
	
	public static GlobalPlugin getInstance() {
		return instance;
	}

	public void getUuid(Callback<UUID> call, String name) {
		getProxy().getScheduler().runAsync(this, new Runnable() {
			@Override
			public void run() {
				call.done(uuidCache.getId(name));
			}
		});
	}
	
	public void getName(Callback<String> call, UUID uuid) {
		getProxy().getScheduler().runAsync(this, new Runnable() {
			@Override
			public void run() {
				call.done(nameCache.getName(uuid));
			}
		});
	}
	
	public void getNames(Callback<List<String>> call, List<UUID> uuids) {
		getProxy().getScheduler().runAsync(this, new Runnable() {
			@Override
			public void run() {
				List<String> list = new ArrayList<>();
				for (UUID uuid : uuids) {
					list.add(getNameCache().getName(uuid));
				}
				call.done(list);
			}
		});
	}
		
	public GlobalPlayers getGlobalPlayers() {
		return globalPlayers.object();
	}

	public UUIDCache getUuidCache() {
		return uuidCache;
	}
	
	public NameCache getNameCache() {
		return nameCache;
	}
	
	public void registerCommand(Command command) {
		getProxy().getPluginManager().registerCommand(this, command);
	}
	
	public GlobalConfig getGlobalConfig() {
		return config;
	}
	
	public Configuration loadConfig(String path) {
		if (!getDataFolder().exists())
			getDataFolder().mkdirs();
		File file = new File(getDataFolder(), path);
		if (!file.exists()) {
			try (InputStream in = this.getResourceAsStream(path)) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(file);
			return configuration;
		} catch (IOException e) {
		}
		return null;
	}

	public boolean saveConfig(Configuration config, String path) {

		File file = new File(getDataFolder(), path);
		File tempFile = new File(getDataFolder(), path + ".tmp");

		if (!tempFile.exists() && tempFile.getParentFile() != null)
			tempFile.getParentFile().mkdirs();

		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, tempFile);
			file.delete();
			if (!tempFile.renameTo(file)) {
				try {
					throw new Exception("Failed to rename temporary file to " + file.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}
	
}
