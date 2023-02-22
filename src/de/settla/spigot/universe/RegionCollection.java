/*
 *
 *     Copyright (C) 2019  Salkin (mc.salkin@gmail.com)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.settla.spigot.universe;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.settla.memory.Memory;
import de.settla.memory.MemoryChildStorable;
import de.settla.memory.MemoryException;
import de.settla.memory.MemoryName;
import de.settla.memory.MemoryStorable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@MemoryName("RegionCollection")
public class RegionCollection implements MemoryChildStorable<RegionCollection, CleanWorld>, Searchable<Region<?>> {

    private final List<JsonElement> fails = new ArrayList<>();

    private final Map<String, Region<?>> regions = new ConcurrentHashMap<>();
    private final CleanWorld world;
    private final Object lock = new Object();
    private boolean dirty;

    RegionCollection(CleanWorld world) {
        checkNotNull(world);
        this.world = world;
    }

    public RegionCollection(CleanWorld world, JsonObject json) {
        checkNotNull(world);
        checkNotNull(json);
        this.world = world;
        json.getAsJsonArray("regions").forEach(r -> {
            try {
				@SuppressWarnings("unchecked")
				Region<?> region = Memory.deserialize(world, r.getAsJsonObject(), Region.class);
                regions.put(region.getName(), region);
                region.getRegionRegistry().registerToCluster();
            } catch (Exception e) {
                // add the fail element to the fail list, this will be added again in the end.
                fails.add(r);
                System.err.println("Could not deserialize region in world '" + world.getName() + "'!");
            }
        });
        System.out.println("LOADED REGIONS IN WORLD " + world.getName() + " OF GALAXY " + world.getGalaxy().getName() + ": " + regions.size());
    }

    @Override
    public JsonObject serialize() throws MemoryException {
        synchronized (lock) {

            JsonObject json = MemoryChildStorable.super.serialize();
            JsonArray array = new JsonArray();
            for (Region<?> region : regions.values()) {
                try {
                    array.add(region.serialize());
                } catch (Exception e) {
                    System.err.println("Could not serialize region '" + region.getName() + "'!");
                }
            }

            // we will save the fails ...
            fails.forEach(array::add);

            json.add("regions", array);
            return json;
        }
    }

    public CleanWorld getWorld() {
        return world;
    }

    @Override
    public boolean isDirty() {
        synchronized (lock) {
            return dirty || regions.values().stream().anyMatch(MemoryStorable::isDirty);
        }
    }

    @Override
    public void setDirty(boolean dirty) {
        synchronized (lock) {
            this.dirty = dirty;
            regions.values().forEach(r -> r.setDirty(dirty));
        }
    }

    void register(Region<?> region) {
        checkNotNull(region);
        synchronized (lock) {
            setDirty(true);
            regions.put(region.getName(), region);
        }
    }

    void unregister(Region<?> region) {
        checkNotNull(region);
        synchronized (lock) {
            setDirty(true);
            regions.remove(region.getName());
        }
    }

    public <T extends Region<?>> T getRegion(String name, Class<T> clazz) {
        checkNotNull(clazz);
        checkNotNull(name);
        synchronized (lock) {
            Region<?> r = regions.get(name);
            if (r != null && clazz.isAssignableFrom(r.getClass())) {
                return clazz.cast(r);
            } else {
                return null;
            }
        }
    }

	@Override
	public <OUT> OUT search(java.util.function.Function<Collection<Region<?>>, OUT> filter) {
		checkNotNull(filter);
        synchronized (lock) {
            return filter.apply(regions.values());
        }
	}

}
