package de.settla.utilities.local.region;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import de.settla.utilities.local.Utils;
import de.settla.utilities.local.region.collect.LongHashTable;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;

/**
 * Maintains a hash table for each chunk containing a list of regions that are
 * contained within that chunk, allowing for fast spatial lookup.
 */
public class ChunkManager {

	private final LongHashTable<ChunkState> states = new LongHashTable<ChunkState>();
	private final Object lock = new Object();
	private final World world;

	/**
	 * Create a new instance.
	 *
	 * @param world
	 *            the world
	 */
	public ChunkManager(World world) {
		this.world = world;
	}

	/**
	 * @return world the world of this chunkmanager.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Get a state object at the given position.
	 * 
	 * @param position
	 *            the position
	 * @param create
	 *            true to create an entry if one does not exist
	 * @return a chunk state object, or {@code null} (only if {@code create} is
	 *         false)
	 */
	@Nullable
	private ChunkState get(int x, int z, boolean create) {
		ChunkState state;
		synchronized (lock) {
			state = states.get(x, z);
			if (state == null && create) {
				state = new ChunkState(this, x, z);
				states.put(x, z, state);
			}
		}
		return state;
	}

	/**
	 * Get a state at the given position or create a new entry if one does not
	 * exist.
	 * 
	 * @param position
	 *            the position
	 * @return a state
	 */
	private ChunkState getOrCreate(int x, int z) {
		return get(x, z, true);
	}

	/**
	 * Register the registery of a region in this chunkmanager.
	 * 
	 * @param registery
	 *            the registery of the region to register.
	 */
	public void register(RegionRegistery registery) {
		checkNotNull(registery);
		synchronized (lock) {

			Region region = registery.getRegion();

			Form form = region.getForm();

			if (form == null)
				return;

			Vector max = form.maximum().ceil();
			Vector min = form.minimum().floor();

			for (int x = ((int) min.getX() >> 4); x <= ((int) max.getX() >> 4); x++) {
				for (int z = ((int) min.getZ() >> 4); z <= ((int) max.getZ() >> 4); z++) {
					ChunkState state = getOrCreate(x, z);
					registery.register(state);
					//Notification.debug(Type.ALL, "REGISTERY: x:"+x+" z:"+z);
				}
			}
		}
	}

	/**
	 * Unregister the registery of a region from this chunkmanager.
	 * 
	 * @param registery
	 *            the registery of the region to unregister.
	 */
	public void unregister(RegionRegistery registery) {
		checkNotNull(registery);
		synchronized (lock) {
			registery.unregister();
		}
	}

	/**
	 * Update a registery of a region. This is similar to unregister and
	 * register the registery.
	 * 
	 * @param registery
	 *            the registery of a region.
	 */
	public void update(RegionRegistery registery) {
		checkNotNull(registery);
		synchronized (lock) {
			registery.unregister();
			register(registery);
		}
	}

	/**
	 * @param vector
	 *            the position.
	 * @return a sorted list of overlapping regions by this vector.
	 */
	public List<Region> getRegions(Vector vector) {
		checkNotNull(vector);
		synchronized (lock) {
			ChunkState state = get(vector.getBlockX() >> 4, vector.getBlockZ() >> 4, false);
			if (state == null)
				return Collections.emptyList();
			return state.getRegions(vector);
		}
	}

	/**
	 * @param vector
	 *            the position.
	 * @param clazz
	 *            the class.
	 * @return a sorted list of overlapping regions by this vector.
	 */
	public <T extends Region> List<T> getRegions(Vector vector, Class<? extends T> clazz) {
		checkNotNull(vector);
		checkNotNull(clazz);
		synchronized (lock) {
			ChunkState state = get(vector.getBlockX() >> 4, vector.getBlockZ() >> 4, false);
			if (state == null)
				return Collections.emptyList();
			return state.getRegions(vector, clazz);
		}
	}

	public <T extends Region> List<T> getRegions(int x, int z, Class<? extends T> clazz) {
		checkNotNull(clazz);
		synchronized (lock) {
			ChunkState state = get(x, z, false);
			if (state == null)
				return Collections.emptyList();
			return Utils.filter(state.regions, clazz);
		}
	}

	/**
	 * Stores a cache of region data for a chunk.
	 */
	public static class ChunkState {

		private final List<Region> regions = new ArrayList<>();
		private final Object lock = new Object();
		private final int x, z;
		private final ChunkManager chunkManager;

		private ChunkState(ChunkManager chunkManager, int x, int z) {
			this.chunkManager = chunkManager;
			this.x = x;
			this.z = z;
		}

		public int x() {
			return x;
		}

		public int z() {
			return z;
		}

		public World getWorld() {
			return chunkManager.getWorld();
		}

		public boolean register(Region region) {
			checkNotNull(region);
			synchronized (lock) {
				if (!regions.contains(region)) {
					regions.add(region);
					return true;
				}
			}
			return false;
		}

		public boolean unregister(Region region) {
			checkNotNull(region);
			synchronized (lock) {
				if (regions.contains(region)) {
					regions.remove(region);
					return true;
				}
				return false;
			}
		}

		public List<Region> getRegions(Vector vector) {
			checkNotNull(vector);
			List<Region> list = new ArrayList<>();
			synchronized (lock) {
				for (Region region : regions) {
					if (region.getForm().overlaps(vector))
						list.add(region);
				}
			}
			Collections.sort(list);
			return list;
		}

		public <T extends Region> List<T> getRegions(Vector vector, Class<? extends T> clazz) {
			checkNotNull(vector);
			checkNotNull(clazz);
			List<T> list = new ArrayList<>();
			synchronized (lock) {
				for (Region region : regions) {
					if (region.getForm().overlaps(vector) && clazz.isAssignableFrom(region.getClass()))
						list.add(clazz.cast(region));
				}
			}
			Collections.sort(list);
			return list;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((lock == null) ? 0 : lock.hashCode());
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
			ChunkState other = (ChunkState) obj;
			if (lock == null) {
				if (other.lock != null)
					return false;
			} else if (!lock.equals(other.lock))
				return false;
			return true;
		}

	}

	public static class RegionRegistery {

		private final List<ChunkState> chunks = new ArrayList<>();
		private final Region region;
		private World world;
		private final Object lock = new Object();

		protected RegionRegistery(Region region) {
			this.region = region;
		}

		/**
		 * @return the region of this registery.
		 */
		public Region getRegion() {
			return region;
		}

		private boolean register(ChunkState state) {
			checkNotNull(state);
			synchronized (lock) {
				if (!chunks.contains(state)) {

					if (world == null) {
						world = state.getWorld();
					} else {
						if (world != state.getWorld())
							return false;
					}
					
					state.register(region);
					chunks.add(state);
					return true;
				}
				return false;
			}
		}

		private boolean unregister(ChunkState state) {
			checkNotNull(state);
			synchronized (lock) {
				if (chunks.contains(state)) {
					chunks.remove(state);
					state.unregister(region);
					if (chunks.isEmpty())
						world = null;
					return true;
				}
				return false;
			}
		}

		/**
		 * Register this registery at the world.
		 * 
		 * @param world
		 *            the world.
		 */
		public void register(World world) {
			synchronized (lock) {
				if (isRegistered())
					unregister();
				world.register(getRegion());
			}
		}

		/**
		 * Unregister this registery from all worlds.
		 */
		public void unregister() {
			synchronized (lock) {
				while (!chunks.isEmpty()) {
					this.unregister(chunks.get(0));
				}
				world = null;
			}
		}

		/**
		 * @return true if a chunk exists where this registery is registered.
		 */
		public boolean isRegistered() {
			synchronized (lock) {
				return !(chunks.isEmpty() && world == null);
			}
		}

		/**
		 * @return a sorted list of all regions intersecting with this
		 *         registery.
		 */
		public List<Region> getIntersections() {
			synchronized (lock) {
				boolean bool = true;
				List<Region> regions = new ArrayList<>();
				for (ChunkState state : chunks) {
					for (Region region : state.regions) {
						if (getRegion() != region && getRegion().intersect(region)) {
							for (Region r : regions) {
								if (r.id().equalsIgnoreCase(region.id())) {
									bool = false;
								}
							}
							if (bool)
								regions.add(region);
							bool = true;
						}
					}
				}
				Collections.sort(regions);
				return regions;
			}
		}

		public World getWorld() {
			synchronized (lock) {
				return world;
			}
		}

		// /**
		// * @return a set of worlds where this region is registered.
		// */
		// public Set<World> getWorlds() {
		// synchronized (lock) {
		// Set<World> worlds = new HashSet<>();
		// for (ChunkState state : chunks) {
		// worlds.add(state.getWorld());
		// }
		// return worlds;
		// }
		// }
	}
}
