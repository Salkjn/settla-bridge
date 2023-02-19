package de.settla.utilities.local.region;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.AsyncCatcher;

import de.settla.local.LocalPlugin;
import de.settla.utilities.functions.Action;
import de.settla.utilities.local.region.events.ExternalEventListener;
import de.settla.utilities.local.region.events.InternalEventListener;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.module.Module;
import de.settla.utilities.storage.Database;
import de.settla.utilities.storage.Storable.Memory;
import de.settla.utilities.storage.Storage;

public class Universe extends Module<LocalPlugin> {
	
	private final ConcurrentMap<String, Storage<Galaxy>> universe = new ConcurrentHashMap<>();
	private final List<Action> waitingActionList = new ArrayList<>();
	private final Object lock = new Object();
	
	/**
	 * Register basic serialization and forms.
	 */
	public Universe(LocalPlugin plugin) {
		super(plugin);
		register();
	}
	
	private void register() {
		Form.registerForms();
		startWaitingList();
		
		Memory.register(Vector.class, map -> new Vector(map));
		Memory.register(Galaxy.class, map -> new Galaxy(map));
		Memory.register(World.class, map -> new World(map));
		Memory.register(Region.class, map -> new Region(map));
		Memory.register(WildnessRegion.class, map -> new WildnessRegion(map));
		Memory.register(RegionIndex.class, map -> new RegionIndex(map));
		
	}
	
	@Override
	public void onPreEnable() {
		new ExternalEventListener(this);
		new InternalEventListener(this);
	}
	
	@Override
	public void onDisable() {
		consume(galaxy -> galaxy.run());
	}
	
	private void startWaitingList() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000*2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (!isLoaded()) {
					try {
						Thread.sleep(1000*2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				waitingActionList.forEach(Action::action);
				waitingActionList.clear();
			}
		}.runTaskAsynchronously(this.getModuleManager());	
	}
	
	public void addToWaitingActionList(Action action) {
		waitingActionList.add(action);
	}
	
	/**
	 * Creates and registers a new galaxy with given name. 
	 * 
	 * @param name the galaxy name.
	 */
	public void registerGalaxy(String name) {
		synchronized (lock) {
			Database<Galaxy> database = new Database<>(name, new File(LocalPlugin.getInstance().getDataFolder(), "/regions/" + name + ".data"), a -> new Galaxy(a), Galaxy.class);
			Storage<Galaxy> store = new Storage<>(database);
			Bukkit.getScheduler().runTaskTimerAsynchronously(this.getModuleManager(), store, 0, 3 * 60 * 20);
			universe.putIfAbsent(name, store);
		}
	}
	
	public void consume(Consumer<Storage<Galaxy>> consumer) {
		synchronized (lock) {
			universe.values().forEach(consumer);
		}
	}
	
	public void consumeGalaxys(Consumer<Galaxy> consumer) {
		synchronized (lock) {
			universe.values().forEach(s -> {
				if(s.object() != null)
					consumer.accept(s.object());
			});
		}
	}

	/**
	 * Sends the given Event to all galaxies.
	 * 
	 * @param event the event to fire.
	 * @param location the location where the Event is called.
	 */
	public void fire(Event event, Location location) {
		if(!isLoaded()) {
			if(event instanceof Cancellable && !(event instanceof PlayerMoveEvent))
				((Cancellable)event).setCancelled(true);
			return;
		}
		consume(galaxy -> {
			if(galaxy.object() != null)
				galaxy.object().fire(event, location);
		});
	}
	
	/**
	 * An useful function to disable the bukkit async-catcher.
	 */
	public void disableAsyncCatcher() {
		AsyncCatcher.enabled = false;
	}
	
	/**
	 * @return true if all regions in all galaxies are loaded.
	 */
	public boolean isLoaded() {
		synchronized (lock) {
			for (Storage<Galaxy> galaxy : universe.values()) {
				if(galaxy.object() == null)
					return false;
			}
			return true;
		}
	}
	
	/**
	 * Gets the galaxy by name. Return null if no galaxy with this name exists.
	 * 
	 * @param name the name of the galaxy.
	 * @return galaxy by the given name.
	 */
	@Nullable
	public Galaxy getGalaxy(String name) {
		synchronized (lock) {
			String normal = Normal.normalize(name);
			for (Storage<Galaxy> galaxy : universe.values()) {
				if(galaxy.object() != null && galaxy.object().getName().equalsIgnoreCase(normal))
					return galaxy.object();
			}
			return null;
		}
	}
	
	/**
	 * @param name the name of the galaxy.
	 * @return true if the galaxy exists.
	 */
	public boolean containsGalaxy(String name) {
		synchronized (lock) {
			String normal = Normal.normalize(name);
			for (Storage<Galaxy> galaxy : universe.values()) {
				if(galaxy.object() != null && galaxy.object().getName().equalsIgnoreCase(normal))
					return true;
			}
			return false;
		}
	}

}