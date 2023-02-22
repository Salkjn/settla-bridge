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

@MemoryName("FormEmpty")
public class EmptyForm extends Form {

	@Override
	public Vector minimum() {
		return Vector.ZERO;
	}

	@Override
	public Vector maximum() {
		return Vector.ZERO;
	}

	@Override
	public boolean overlaps(Vector vector) {
		return false;
	}

	@Override
	public boolean intersect(Form form) {
		return false;
	}

	@Override
	public Form move(Vector vector) {
		return this;
	}

	@Override
	public JsonObject serialize() {
		return super.serialize();
	}
}
