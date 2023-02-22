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

import de.settla.memory.MemoryChildStorable;
import de.settla.memory.MemoryException;

import com.google.gson.JsonObject;

public abstract class WorldElement<This extends MemoryChildStorable<This, CleanWorld>> implements MemoryChildStorable<This, CleanWorld> {

    private final CleanWorld world;
    private final String name;
    private boolean dirty;

    public WorldElement(CleanWorld world, String name) {
        checkNotNull(world);
        checkNotNull(name);
        this.world = world;
        this.name = name;
    }

    public WorldElement(CleanWorld world, JsonObject json) {
        checkNotNull(world);
        checkNotNull(json);
        this.world = world;
        this.name = json.get("name").getAsString();
    }

    @Override
    public JsonObject serialize() throws MemoryException {
        JsonObject json = MemoryChildStorable.super.serialize();
        json.addProperty("name", name);
        return json;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public String getName() {
        return name;
    }

    public CleanWorld getWorld() {
        return world;
    }

}
