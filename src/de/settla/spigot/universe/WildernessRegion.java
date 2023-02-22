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

import de.settla.memory.MemoryName;

import com.google.gson.JsonObject;

@MemoryName("WildernessRegion")
public class WildernessRegion extends WorldElement<WildernessRegion> {

    public static final String WILDERNESS_REGION_NAME = "wilderness";
    
    public WildernessRegion(CleanWorld world) {
        super(world, WILDERNESS_REGION_NAME);
    }

    public WildernessRegion(CleanWorld world, JsonObject json) {
        super(world, json);
    }

}
