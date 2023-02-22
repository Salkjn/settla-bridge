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

/**
 * This region includes the minimum and excludes the maximum...
 */

@MemoryName("FormCuboid")
public class CuboidForm extends Form {

	private final Vector minimum, maximum;

	public CuboidForm(JsonObject json) {
		this.minimum = deserialize(json.get("min").getAsJsonObject(), Vector.class);
		this.maximum = deserialize(json.get("max").getAsJsonObject(), Vector.class);
	}

	public CuboidForm(Vector v1, Vector v2) {
		minimum = Vector.getMinimum(v1, v2);
		maximum = Vector.getMaximum(v1, v2);
	}

	@Override
	public JsonObject serialize() {
		JsonObject json = super.serialize();
		json.add("min", minimum.serialize());
		json.add("max", maximum.serialize());
		return json;
	}

	@Override
	public Vector minimum() {
		return minimum;
	}

	@Override
	public Vector maximum() {
		return maximum;
	}

	@Override
	public boolean overlaps(Vector vector) {
		return Vector.isSmallerOrEqual(minimum, vector) && Vector.isSmaller(vector, maximum);
	}

	@Override
	public Form move(Vector vector) {
		return new CuboidForm(maximum().add(vector), minimum().add(vector));
	}

	@Override
	public boolean intersect(Form form) {
		if (form instanceof CuboidForm) {
			Vector min1 = minimum();
			Vector max1 = maximum();
			Vector min2 = form.minimum();
			Vector max2 = form.maximum();
			boolean fail1 = max2.getX() <= min1.getX() || max2.getY() <= min1.getY() || max2.getZ() <= min1.getZ();
			boolean fail2 = min2.getX() >= max1.getX() || min2.getY() >= max1.getY() || min2.getZ() >= max1.getZ();
			if (fail1 || fail2)
				return false;
			return true;
		} else {
			return false;
		}
	}
	
	// @Override
	// public boolean contains(Form form) {
	// 	return Vector.isSmallerOrEqual(minimum(), form.minimum()) && Vector.isSmallerOrEqual(form.maximum(), maximum());
	// }
}
