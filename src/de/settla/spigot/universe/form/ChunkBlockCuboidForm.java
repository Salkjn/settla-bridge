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

package de.settla.spigot.universe.form;

import de.settla.memory.MemoryName;
import de.settla.spigot.universe.Vector;

import com.google.gson.JsonObject;

@MemoryName("FormChunkBlockCuboid")
public class ChunkBlockCuboidForm extends BlockCuboidForm {

    private final int x, z, size;

    public ChunkBlockCuboidForm(JsonObject json) {
        super(json);
        this.size = json.get("size").getAsInt();
        this.x = json.get("x").getAsInt();
        this.z = json.get("z").getAsInt();
    }

    public ChunkBlockCuboidForm(int x, int z, int size) {
        super(new Vector(x << size, 0, z << size), new Vector(((x+1) << size) - 1, 300, ((z+1) << size) - 1));
        this.x = x;
        this.z = z;
        this.size = size;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = super.serialize();
        json.addProperty("size", size());
        json.addProperty("x", x());
        json.addProperty("z", z());
        return json;
    }

    @Override
    public ChunkBlockCuboidForm move(Vector vector) {
        return null;
    }

    public int size() {
        return size;
    }
    
    public int x() {
        return x;
    }
    
    public int z() {
        return z;
    }

}
