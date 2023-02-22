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
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang.RandomStringUtils;
import de.settla.memory.MemoryException;
import de.settla.memory.MemoryName;
import de.settla.memory.MemoryStorable;


@MemoryName("Galaxy")
public class Galaxy implements MemoryStorable<Galaxy>, Searchable<CleanWorld> {

    private final List<JsonElement> fails = new ArrayList<>();

    private final Map<String, CleanWorld> worlds = new ConcurrentHashMap<>();
    private final Object lock = new Object();
    private final String name;
    private boolean dirty;

    public Galaxy(String name) {
        checkNotNull(name);
        this.name = name;
    }

    public Galaxy(JsonObject json) {
        checkNotNull(json);
        this.name = json.get("name").getAsString();
        json.get("worlds").getAsJsonArray().forEach(w -> {
            try {
                CleanWorld world = deserialize(this, w.getAsJsonObject(), CleanWorld.class);
                worlds.put(world.getName(), world);
            } catch (Exception e) {
                // add the fail element to the fail list, this will be added again in the end.
                fails.add(w);
                System.err.println("Could not deserialize world in galaxy '" + name + "'!");
            }
        });
    }

    @Override
    public JsonObject serialize() throws MemoryException {
        synchronized (lock) {
            JsonObject json = MemoryStorable.super.serialize();
            json.addProperty("name", name);
            JsonArray array = new JsonArray();
            for (CleanWorld world : worlds.values()) {
                try {
                    array.add(world.serialize());
                } catch (Exception e) {
                    System.err.println("Could not serialize world '" + world.getName() + "'!");
                }
            }

            // we will save the fails ...
            fails.forEach(array::add);

            json.add("worlds", array);
            return json;
        }
    }

    public String getName() {
        return name;
    }

    public CleanWorld getWorld(String name) {
        checkNotNull(name);
//        synchronized (lock) {
        return worlds.get(name);
//        }
    }

    public void forEachWorld(Consumer<CleanWorld> consumer) {
        synchronized (lock) {
            worlds.values().forEach(consumer);
        }
    }

    public CleanWorld createWorld(String name, Function<CleanWorld, WildernessRegion> wilderness) {
        checkNotNull(name);
        checkNotNull(wilderness);
        synchronized (lock) {
            return worlds.computeIfAbsent(name, it -> {
                setDirty(true);
                return new CleanWorld(this, it, wilderness);
            });
        }
    }

    public CleanWorld deleteWorld(String name) {
        checkNotNull(name);
        synchronized (lock) {
            setDirty(true);
            return worlds.remove(name);
        }
    }

    @Override
    public boolean isDirty() {
        synchronized (lock) {
            return dirty || worlds.values().stream().anyMatch(MemoryStorable::isDirty);
        }
    }

    @Override
    public void setDirty(boolean dirty) {
        synchronized (lock) {
            this.dirty = dirty;
            worlds.values().forEach(w -> w.setDirty(dirty));
        }
    }
    
    public <T extends Region<?>> List<T> getRegions(String id, Class<T> clazz) {
		checkNotNull(id);
		List<T> regions = new ArrayList<>();
		for (CleanWorld world : this.worlds.values()) {
			T region = world.getRegionCollection().getRegion(id, clazz);
			if(region != null)
				regions.add(region);
		}
		return regions;
	}
    
	public String generateId(int lenght) {
		String id = null;
		do {
			id = RandomStringUtils.randomAlphabetic(lenght).toLowerCase();
		} while(!getRegions(id, Region.class).isEmpty());
		return id;
	}

	@Override
	public <OUT> OUT search(Function<Collection<CleanWorld>, OUT> filter) {
		synchronized (lock) {
			return filter.apply(worlds.values());
		}
	}
	
}