package de.settla.local.npc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import de.settla.local.LocalPlugin;
import de.settla.utilities.module.Module;
import de.settla.utilities.storage.Database;
import de.settla.utilities.storage.Storage;

public class NpcModule extends Module<LocalPlugin> {

	private Storage<NpcDatas> npcdatas;

	private final Map<String, NpcModel> models = new HashMap<>();
	private final Map<String, Npc> npcs = new HashMap<>();
	private final Object lock = new Object();

	private final BukkitRunnable npcUpdater;

	public NpcModule(LocalPlugin moduleManager) {
		super(moduleManager);
		registerModels();
		this.npcUpdater = new BukkitRunnable() {
			@Override
			public void run() {
				throughNpcs(n -> n.getNpcEntity().clearWatchers(Bukkit.getOnlinePlayers()));
			}
		};
	}

	@Override
	public void onEnable() {

		Bukkit.getOnlinePlayers().forEach(player -> NpcPacketReader.getOrCreatePacketReader(player).inject());
		getModuleManager().registerListener(new NpcListener(NpcModule.this));

		Database<NpcDatas> database = new Database<>("npcs", new File(getDataFolder(), "/npcsdata.data"),
				n -> new NpcDatas(), NpcDatas.class);
		npcdatas = new Storage<>(database);
		new BukkitRunnable() {
			@Override
			public void run() {
				npcdatas.run();
				loadNpcs();
				getModuleManager().registerCommand(new NpcCommand("npc"));
				npcUpdater.runTaskTimerAsynchronously(getModuleManager(), 20 * 1, 20 * 1);
			}
		}.runTaskLater(getModuleManager(), 20 * 2);
	}

	@Override
	public void onDisable() {
		Bukkit.getOnlinePlayers().forEach(player -> NpcPacketReader.getOrCreatePacketReader(player).uninject());
		// npcUpdater.cancel();
		throughNpcs(n -> n.getNpcEntity().destroy());
		npcdatas.run();
	}

	private void registerModels() {

		addModel(new NpcModel("admin_shop", p -> {
			 p.performCommand("warp adminshop");
//			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp adminshop " + p.getName());
		}, p -> {
			 p.performCommand("vote shop");
//			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp adminshop " + p.getName());
		}, p -> "§e✪§f§lAdmin Shop§e✪", null, p -> "§e>> §a§lKLICK §e<<", null, null));

		addModel(new NpcModel("jobs_browse", p -> {
			p.performCommand("jobs browse");
			// Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp adminshop
			// " + p.getName());
		}, p -> {
			p.performCommand("jobs stats " + p.getName());
			// Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp adminshop
			// " + p.getName());
		}, p -> "§e✪§f§lJobs§e✪", null, p -> "§fWähle dir einen Beruf aus. §e(Rechts-Klick)", null, p -> "§fSchaue dir deine Statistiken an. §e(Links-Klick)", null, null));

	}

	private void loadNpcs() {
		getNpcDatas().forEach(data -> registerNpc(data));
	}

	public NpcDatas getNpcDatas() {
		return npcdatas.object();
	}

	public void addModel(NpcModel model) {
		models.put(model.getModelName(), model);
	}

	public NpcModel getModel(String model) {
		return models.get(model);
	}

	public void forEachModel(Consumer<NpcModel> consumer) {
		models.values().forEach(consumer);
	}

	public Npc registerNpc(NpcData data) {
		Npc npc = data.createNpc(getModel(data.getModel()));
		synchronized (lock) {
			npcs.put(data.getName().toLowerCase(), npc);
			return npc;
		}
	}

	public Npc unregisterNpc(String name) {
		synchronized (lock) {
			Npc n = npcs.remove(name.toLowerCase());
			return n;
		}
	}

	public Npc getNpc(String name) {
		synchronized (lock) {
			return npcs.get(name.toLowerCase());
		}
	}

	public void throughNpcs(Consumer<Npc> consumer) {
		synchronized (lock) {
			npcs.values().forEach(consumer);
		}
	}

	public void throughNearNpcs(Location center, Consumer<Npc> consumer) {
		synchronized (lock) {
			npcs.values().stream().filter(n -> n.getNpcEntity().isInsideViewingDistance(center)).forEach(consumer);
		}
	}

	public Npc getNpcByEntityId(int id) {
		synchronized (lock) {
			return npcs.values().stream().filter(n -> n.getNpcEntity().getId() == id).findFirst().orElse(null);
		}
	}

}
