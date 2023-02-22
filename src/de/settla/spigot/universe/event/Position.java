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

package de.settla.spigot.universe.event;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;

import de.settla.memory.MemoryName;
import org.bukkit.Location;
import de.settla.spigot.universe.CleanWorld;
import de.settla.spigot.universe.Galaxy;
import de.settla.spigot.universe.Region;
import de.settla.spigot.universe.Vector;
import de.settla.spigot.universe.WildernessRegion;

@MemoryName("Position")
public class Position extends Vector {

    private final String world;

    public Position(double x, double y, double z, String world) {
        super(x, y, z);
        checkNotNull(world);
        this.world = world;
    }

    public Position(Vector vector, String world) {
        super(vector);
        checkNotNull(world);
        this.world = world;
    }

    public Position(Vector vector, CleanWorld world) {
        super(vector);
        checkNotNull(world);
        this.world = world.getName();
    }

    public Position(Position position) {
        this(position.x, position.y, position.z, position.world);
    }

    public Position(Location location) {
        super(location.getX(), location.getY(), location.getZ());
        this.world = location.getWorld().getName();
    }

    public Position(JsonObject json) {
        super(json);
        this.world = json.get("world").getAsString();
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = super.serialize();
        json.addProperty("world", world);
        return json;
    }

    public String getWorldName() {
        return world;
    }

    public CleanWorld world(Galaxy galaxy) {
        checkNotNull(galaxy);
        return galaxy.getWorld(getWorldName());
    }

    public List<Region> regions(Galaxy galaxy) {
        CleanWorld cw = world(galaxy);
        return cw == null ? Collections.emptyList() : cw.getRegionCluster().getRegions(this, Region.class);
    }

    public WildernessRegion wilderness(Galaxy galaxy) {
        CleanWorld cw = world(galaxy);
        return cw == null ? null : cw.getWildernessRegion();
    }
}
