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

import java.util.function.Function;

import de.settla.memory.MemoryChildStorable;
import de.settla.memory.MemoryException;
import de.settla.memory.MemoryName;

import com.google.gson.JsonObject;


@MemoryName("CleanWorld")
public class CleanWorld implements MemoryChildStorable<CleanWorld, Galaxy> {

    private final Galaxy galaxy;
    private final RegionCollection collection;
    private final RegionCluster cluster;
    private final Object lock = new Object();
    private final String name;

    private final WildernessRegion wilderness;

    CleanWorld(Galaxy galaxy, String name, Function<CleanWorld, WildernessRegion> wilderness) {
        checkNotNull(galaxy);
        checkNotNull(name);
        checkNotNull(wilderness);
        this.galaxy = galaxy;
        this.collection = new RegionCollection(this);
        this.cluster = new RegionCluster(this);
        this.wilderness = wilderness.apply(this);
        this.name = name;
    }

    public CleanWorld(Galaxy galaxy, JsonObject json) {
        checkNotNull(galaxy);
        checkNotNull(json);
        this.galaxy = galaxy;
        this.cluster = new RegionCluster(this);
        this.name = json.get("name").getAsString();
        this.collection = deserialize(this, json.get("collection").getAsJsonObject(), RegionCollection.class);
        this.wilderness = deserialize(this, json.get("wilderness").getAsJsonObject(), WildernessRegion.class);
    }

    @Override
    public JsonObject serialize() throws MemoryException {
        synchronized (lock) {
            JsonObject json = MemoryChildStorable.super.serialize();
            json.addProperty("name", name);
            json.add("collection", collection.serialize());
            json.add("wilderness", wilderness.serialize());
            return json;
        }
    }

    public String getName() {
        return name;
    }

    public Galaxy getGalaxy() {
        return galaxy;
    }

    public RegionCluster getRegionCluster() {
        return cluster;
    }

    public RegionCollection getRegionCollection() {
        return collection;
    }

    public WildernessRegion getWildernessRegion() {
        return wilderness;
    }

    @Override
    public boolean isDirty() {
        synchronized (lock) {
            return collection.isDirty();
        }
    }

    @Override
    public void setDirty(boolean dirty) {
        synchronized (lock) {
            collection.setDirty(dirty);
        }
    }

}
