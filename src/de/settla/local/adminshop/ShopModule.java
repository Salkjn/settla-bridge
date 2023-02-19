package de.settla.local.adminshop;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.scheduler.BukkitRunnable;

import de.settla.local.LocalPlugin;
import de.settla.utilities.ChangeTracked;
import de.settla.utilities.module.Module;

public class ShopModule extends Module<LocalPlugin> implements ChangeTracked {

	private final List<ShopSign> signs = new ArrayList<ShopSign>();
	private final Object lock = new Object();
	private boolean dirty;

	public ShopModule(LocalPlugin moduleManager) {
		super(moduleManager);
	}

	private final BukkitRunnable signUpdater = new BukkitRunnable() {
		@Override
		public void run() {
			consumeSigns(list -> {
				list.forEach(sign -> {
					sign.clearWatchers(Bukkit.getOnlinePlayers());
				});
			});
		}
	};

	@Override
	public void onPreEnable() {
		ConfigurationSerialization.registerClass(ShopSign.class);
	}

	@Override
	public void onEnable() {
		new BukkitRunnable() {

			@Override
			public void run() {
				getModuleManager().registerListener(new ShopListener(ShopModule.this));
				getModuleManager().registerCommand(new ShopCommand("adminshop"));
				initConfig();
				signUpdater.runTaskTimerAsynchronously(LocalPlugin.getInstance(), 20L, 3L * 20L);
			}
		}.runTaskAsynchronously(getModuleManager());

	}

	@Override
	public void onDisable() {
		if (isDirty()) {
			YamlConfiguration conf = getModuleManager().loadConfig("shops.yml");
			conf.set("shops", signs);
			getModuleManager().saveConfig(conf, "shops.yml");
		}
	}

	public void initConfig() {

		YamlConfiguration conf = getModuleManager().loadConfig("shops.yml");
		if (conf.isSet("shops")) {
			conf.getList("shops").stream().filter(a -> a instanceof ShopSign)
					.forEach(sign -> signs.add((ShopSign) sign));
		}
		setDirty(false);
	}

	public void consumeSigns(Consumer<List<ShopSign>> consumer) {
		synchronized (lock) {
			consumer.accept(this.signs);
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
