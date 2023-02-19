package de.settla.utilities.local.region;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import de.settla.utilities.ChangeTracked;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("Regions")
public class RegionIndex implements ChangeTracked, Storable {

	private final ConcurrentMap<String, Region> regions = new ConcurrentHashMap<String, Region>();
	private Set<Region> removed = new HashSet<Region>();
	private final Object lock = new Object();

	public RegionIndex() {

	}

	public RegionIndex(Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			regions.putIfAbsent(entry.getKey(), (Region) deserialize(entry.getValue(), Region.class));
		}
	}

	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			for (Entry<String, Region> entry : regions.entrySet()) {
				if(!entry.getValue().isTransient()) {
					map.putIfAbsent(entry.getKey(), entry.getValue().serialize());
				}
			}
			return map;
		}
	}

	public void throughRegions(Consumer<Region> consumer) {
		checkNotNull(consumer);
		synchronized (lock) {
			regions.forEach((name, world) -> consumer.accept(world));
		}
	}
	
	/**
	 * Register all regions to the chunkmanager.
	 * 
	 * @param chunkmanager the chunkmanager.
	 */
	public void registerAll(ChunkManager chunkManager) {
		checkNotNull(chunkManager);
		synchronized (lock) {
			for (Region region : regions.values()) {
				chunkManager.register(region.getRegionRegistery());
			}
		}
	}

	/**
	 * Perform the add operation.
	 *
	 * @param region the region
	 */
	private void performAdd(Region region) {
		checkNotNull(region);

		region.setDirty(true);

		synchronized (lock) {
			String normalId = Normal.normalize(region.id());

			Region existing = regions.get(normalId);

			// Casing / form of ID has changed
			if (existing != null && !existing.id().equals(region.id())) {
				removed.add(existing);
			}

			regions.put(normalId, region);
			removed.remove(region);
		}
	}

	/**
	 * Add the collection to this index.
	 * 
	 * @param regions the collection.
	 */
	public void addAll(Collection<Region> regions) {
		checkNotNull(regions);

		synchronized (lock) {
			for (Region region : regions) {
				performAdd(region);
			}
		}
	}

	/**
	 * Adds a region to this index.
	 * 
	 * @param region the region.
	 */
	public void add(Region region) {
		checkNotNull(region);
		synchronized (lock) {
			performAdd(region);
		}
	}

	/**
	 * Checks to see if the id contains.
	 * 
	 * @param id the id of a region.
	 * @return true if a region exists with this id.
	 */
	public boolean contains(String id) {
		checkNotNull(id);
		synchronized (lock) {
			return regions.containsKey(Normal.normalize(id));
		}
	}

	/**
	 * @param id the id of a region.
	 * @return region by id.
	 */
	@Nullable
	public Region get(String id) {
		checkNotNull(id);
		synchronized (lock) {
			return regions.get(Normal.normalize(id));
		}
	}

	/**
	 * @param id the id of a region.
	 * @return the region which is removed from index.
	 */
	@Nullable
	public Region remove(String id) {
		checkNotNull(id);
		synchronized (lock) {
			Region region = regions.remove(Normal.normalize(id));
			if (region != null)
				removed.add(region);
			return region;
		}
	}

	/**
	 * @return the number of regions in this index.
	 */
	public int size() {
		return regions.size();
	}

	@Override
	public boolean isDirty() {
		synchronized (lock) {
			if (!removed.isEmpty()) {
				return true;
			}

			for (Region region : regions.values()) {
				if (region.isDirty()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			if (!dirty) {
				removed.clear();
			}

			for (Region region : regions.values()) {
				region.setDirty(dirty);
			}
		}
	}
}
