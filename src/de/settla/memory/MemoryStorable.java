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

package de.settla.memory;

import com.google.gson.JsonObject;

public interface MemoryStorable<This extends MemoryStorable<This>> {

    default <T extends MemoryStorable<?>> T deserialize(JsonObject json, Class<T> clazz) throws MemoryException {
        return Memory.deserialize(json, clazz);
    }

    default <T extends MemoryChildStorable<T, This>> T deserialize(This adult, JsonObject json, Class<T> clazz) throws MemoryException {
        return Memory.deserialize(adult, json, clazz);
    }

    default JsonObject serialize() throws MemoryException {
        JsonObject json = new JsonObject();
        json.addProperty(Memory.MEMORY_KEY, Memory.memoryId(this.getClass()));
        return json;
    }

    default boolean isDirty() {
        return false;
    }

    default void setDirty(boolean dirty) {
    }

}
