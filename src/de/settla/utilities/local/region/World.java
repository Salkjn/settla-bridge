package de.settla.utilities.local.region;

import java.util.Map;

import de.settla.utilities.ChangeTracked;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("World")
public class World implements ChangeTracked, Storable {

	private ChunkManager chunkManager = new ChunkManager(this);
	private final BukkitWorld bukkitWorld = new BukkitWorld(this);
	
	private final Object lock = new Object();

	private final String name;
	private RegionIndex index;
	private boolean loaded = false;
	
	private final WildnessRegion wildnessRegion;

	/**
	 * Events will be fired for the wildnessregion if no region is found at the event's location. 
	 * 
	 * @param name the name of the world.
	 * @param regions the index of the regions.
	 * @param wildnessRegion the wildnessregion
	 */
	public World(String name, RegionIndex regions, WildnessRegion wildnessRegion) {
		this.name = name;
		this.index = regions;
		this.wildnessRegion = wildnessRegion;
		connectWildnessRegionToWorld();
		registerRegions();
	}

	public World(Map<String, Object> map) {
		this.name = (String) map.get("name");
		this.index = (RegionIndex) deserialize(map.get("regions"), RegionIndex.class);
		this.wildnessRegion = (WildnessRegion) deserialize(map.get("wildness"), WildnessRegion.class);
		connectWildnessRegionToWorld();
		registerRegions();
	}

	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("name", name);
			map.put("regions", index.serialize());
			if(wildnessRegion != null)
				map.put("wildness", wildnessRegion.serialize());
			return map;
		}
	}

	/**
	 * Events will be fired for the wildnessregion if no region is found at the event's location.
	 * 
	 * @return the wildnessregion of this world.
	 */
	public WildnessRegion getWildnessRegion() {
		synchronized (lock) {
			return wildnessRegion;
		}
	}
	
	private void connectWildnessRegionToWorld() {
		if(wildnessRegion != null)
			wildnessRegion.setWorld(this);
	}
	
	private void registerRegions() {
		synchronized (lock) {
			index.registerAll(chunkManager);
			loaded = true;
		}
	}
	
	/**
	 * Register the region to the regionindex and chunkmanager.
	 * 
	 * @param region the region to register to this world.
	 */
	public void register(Region region) {
		synchronized (lock) {
			if (!index.contains(region.id()))
				index.add(region);
			chunkManager.register(region.getRegionRegistery());
		}
	}

	public void deleteAll() {
		synchronized (lock) {
			setDirty(true);
			this.chunkManager = new ChunkManager(this);
			this.index = new RegionIndex();
			registerRegions();
		}
	}
	
	/**
	 * Unregister the region from the chunk manager and register the region to the region-index.
	 * 
	 * @param region the region to unregister from this world.
	 */
	public void unregister(Region region) {
		synchronized (lock) {
			if (!index.contains(region.id()))
				index.add(region);
			chunkManager.unregister(region.getRegionRegistery());
		}
	}

	/**
	 * Deletes the region from the chunk manager and region-index.
	 * 
	 * @param region the region to delete.
	 */
	public void delete(Region region) {
		synchronized (lock) {
			if (index.contains(region.id()))
				index.remove(region.id());
			chunkManager.unregister(region.getRegionRegistery());
		}
	}

	/**
	 * @return true if all regions from the region-index are registered to the chunk manager.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * @return name the name of this world.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return chunkmanager the chunkmanager of this world.
	 */
	public ChunkManager getChunkManager() {
		synchronized (lock) {
			return chunkManager;
		}
	}

	/**
	 * @return regionindex the regionindex of this world.
	 */
	public RegionIndex getRegionIndex() {
		synchronized (lock) {
			return index;
		}
	}

	@Override
	public boolean isDirty() {
		synchronized (lock) {
			return index.isDirty();
		}
	}

	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			index.setDirty(dirty);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lock == null) ? 0 : lock.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		World other = (World) obj;
		if (lock == null) {
			if (other.lock != null)
				return false;
		} else if (!lock.equals(other.lock))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @return bukkitworld the bukkitworld of this world.
	 */
	public BukkitWorld getBukkitWorld() {
		return bukkitWorld;
	}

}
