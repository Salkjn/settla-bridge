package de.settla.utilities.local.region;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Location;
import org.bukkit.event.Event;

import de.settla.utilities.local.region.events.EventDistributor;
import de.settla.utilities.local.region.events.RegionEvent;
import de.settla.utilities.local.region.events.WildnessEvent;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("Galaxy")
public class Galaxy implements EventDistributor, Storable {
	
	private final ConcurrentMap<String, World> worlds = new ConcurrentHashMap<String, World>();
	private final Object lock = new Object();
	private final String name;
	
	/**
	 * @param name the name of the galaxy.
	 */
	public Galaxy(String name) {
		this.name = Normal.normalize(name);
	}
	
	@SuppressWarnings("unchecked")
	public Galaxy(Map<String, Object> map) {
		this.name = (String) map.get("name");
		for (Entry<String, Object> entry : ((Map<String, Object>)map.get("worlds")).entrySet()) {
			worlds.putIfAbsent(entry.getKey(), (World) deserialize(entry.getValue(), World.class));
		}
	}
	
	/**
	 * @return name the name of this galaxy.
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("name", name);
			
			Map<String, Object> map2 = new HashMap<>();
			for (Entry<String, World> entry : worlds.entrySet()) {
				map2.putIfAbsent(entry.getKey(), entry.getValue().serialize());
			}
			
			map.put("worlds", map2);
			return map;
		}
	}
	
	public void throughWorlds(Consumer<World> consumer) {
		checkNotNull(consumer);
		synchronized (lock) {
			worlds.forEach((name, world) -> consumer.accept(world));
		}
	}
	
	/**
	 * Returns if world exists the world else creates a new one.
	 * 
	 * @param wildnessRegion the wildnessregion.
	 * @param name the name of the world.
	 * @return world.
	 */
	public World getOrCreateWorld(String name, WildnessRegion wildnessRegion) {
		checkNotNull(name);
		synchronized (lock) {
			World world = worlds.get(name);
			if(world == null) {
				world = new World(name, new RegionIndex(), wildnessRegion);
				worlds.putIfAbsent(name, world);
			}
			return world;
		}
	}
	
	/**
	 * Creates a new World with the given wildnessregion.
	 * 
	 * @param name the name of the world.
	 * @param wildnessRegion the wildnessregion.
	 * @return
	 */
	public World createWorld(String name, WildnessRegion wildnessRegion) {
		checkNotNull(name);
		synchronized (lock) {
			World world = worlds.get(name);
			if(world == null) {
				world = new World(name, new RegionIndex(), wildnessRegion);
				worlds.putIfAbsent(name, world);
			}
			return world;
		}
	}
	
	/**
	 * Gets the world by name. Return null if no world with this name exists.
	 * 
	 * @param name the name of the world.
	 * @return world by the given name.
	 */
	@Nullable
	public World getWorld(String name) {
		checkNotNull(name);
		synchronized (lock) {
			return worlds.get(name);
		}
	}
	
	/**
	 * 
	 * @param name the name of the world.
	 * @return true if a world with the given name exists.
	 */
	public boolean existsWorld(String name) {
		checkNotNull(name);
		synchronized (lock) {
			return worlds.containsKey(name);
		}
	}
	
	/**
	 * Gets all regions by id of all worlds. 
	 * 
	 * @param id the id of the region.
	 * @return a list of regions.
	 */
	public List<Region> getRegions(String id) {
		checkNotNull(id);
		synchronized (lock) {
			List<Region> regions = new ArrayList<>();
			for (World world : worlds.values()) {
				Region region = world.getRegionIndex().get(id);
				if(region != null)
					regions.add(region);
			}
			Collections.sort(regions);
			return regions;
		}
	}
	
	/**
	 * Gets all regions by id and by clazz of all worlds.
	 * 
	 * @param id the id of the region.
	 * @param clazz the class 
	 * @return a list of regions.
	 */
	public <T extends Region> List<T> getRegions(String id, Class<? extends T> clazz) {
		checkNotNull(id);
		checkNotNull(clazz);
		synchronized (lock) {
			List<T> regions = new ArrayList<>();
			for (World world : worlds.values()) {
				Region region = world.getRegionIndex().get(id);
				if(region != null && clazz.isAssignableFrom(region.getClass()))
					regions.add(clazz.cast(region));
			}
			Collections.sort(regions);
			return regions;
		}
	}
	
	/**
	 * Returns all registered worlds in this galaxy.
	 *  
	 * @return set of worlds.
	 */
	public Set<String> getWorlds() {
		synchronized (lock) {
			return Collections.unmodifiableSet(worlds.keySet());
		}
	}

	@Override
	public void fire(Event event, @Nullable Vector position, String worldName) {
		checkNotNull(event);
		synchronized (lock) {
			World world = getWorld(worldName);
			if(world == null)
				return;
			if(position == null || event instanceof WildnessEvent) {
				fireWildness(world, event);
			} else {
				List<Region> regions = world.getChunkManager().getRegions(position);
				if(regions.isEmpty()) {
					fireWildness(world, event);
				} else
					regions.forEach(region -> region.fire(event));
				regions.clear(); // At the end, clear all elements
			}
		}	
	}
	
	/**
	 * Sends the event directly to the wildnessregion.
	 * 
	 * @param world the world.
	 * @param event the event.
	 */
	public void fireWildness(World world, Event event) {
		checkNotNull(world);
		checkNotNull(event);
		WildnessRegion wildnessRegion = world.getWildnessRegion();
		if(wildnessRegion != null && !(event instanceof RegionEvent))
			wildnessRegion.fire(event);
	}
	
	/**
	 * Sends the given event to all worlds.
	 * 
	 * @param event the event to fire.
	 * @param location the location where the event is called.
	 */
	public void fire(Event event, Location location) {
		checkNotNull(location);
		fire(event, new Vector(location), location.getWorld().getName());
	}

	@Override
	public boolean isDirty() {
		synchronized (lock) {
			for (World world : worlds.values()) {
				if(world.isDirty())
					return true;
			}
			return false;
		}
	}

	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			for (World world : worlds.values()) {
				world.setDirty(dirty);
			}
		}
	}
	
	/**
	 * @return the number of regions registered in this galaxy.
	 */
	public int size() {
		synchronized (lock) {
			int size = 0;
			for (World world : worlds.values()) {
				size += world.getRegionIndex().size();
			}
			return size;
		}
	}
	
	/**
	 * Generates a new not used id.
	 * 
	 * @param lenght the lenght of the id.
	 * @return a free id.
	 */
	public String generateId(int lenght) {
		String id = null;
		do {
			id = Normal.normalize(RandomStringUtils.randomAlphabetic(lenght));
		} while(!getRegions(id).isEmpty());
		return id;
	}
	
}
