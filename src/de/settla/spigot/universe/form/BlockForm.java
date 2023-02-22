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

@MemoryName("FormBlock")
public class BlockForm extends Form {

    private final Vector position;

    public BlockForm(JsonObject json) {
        this.position = deserialize(json.get("position").getAsJsonObject(), Vector.class);
    }

    public BlockForm(Vector position) {
    	this.position = position;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = super.serialize();
        json.add("position", position.serialize());
        return json;
    }

    public Vector block() {
    	return position;
    }
    
    @Override
    public Vector minimum() {
        return position;
    }

    @Override
    public Vector maximum() {
        return position;
    }

    @Override
    public boolean overlaps(Vector vector) {
        return vector.getX() == position.getX() && vector.getY() == position.getY() && vector.getZ() == position.getZ();
    }

    @Override
    public Form move(Vector vector) {
        return new BlockForm(block().add(vector));
    }

    @Override
    public boolean intersect(Form form) {
        return form.overlaps(block());
    }

    // @Override
	// public boolean contains(Form form) {
	// 	return Vector.isSmallerOrEqual(minimum(), form.minimum()) && Vector.isSmallerOrEqual(form.maximum(), maximum());
	// }
    
}
