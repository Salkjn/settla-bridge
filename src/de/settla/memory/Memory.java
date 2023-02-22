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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Memory {

    public static final String MEMORY_KEY = "+";
    private static final Map<String, Function<JsonObject, MemoryStorable>> deserialize = new HashMap<>();
    private static final Map<String, BiFunction<MemoryStorable, JsonObject, MemoryChildStorable<? extends MemoryStorable, MemoryStorable>>> children = new HashMap<>();

    public static <Adult extends MemoryStorable, T extends MemoryChildStorable<? super T, Adult>> T deserialize(Adult adult, JsonObject json, Class<T> clazz) throws MemoryException {
        checkNotNull(json);
        checkNotNull(clazz);
        JsonElement key = json.get(MEMORY_KEY);
        if (key == null)
            throw new MemoryException("class key not found.");
        BiFunction<MemoryStorable, JsonObject, MemoryChildStorable<? extends MemoryStorable, MemoryStorable>> function = children.get(key.getAsString().toLowerCase());
        if (function == null)
            throw new MemoryException("class deserialization function '" + key.getAsString().toLowerCase() + "' not found.");
        return clazz.cast(function.apply(adult, json));
    }

    public static <T extends MemoryStorable> T deserialize(JsonObject json, Class<T> clazz) throws MemoryException {
        checkNotNull(json);
        checkNotNull(clazz);
        JsonElement key = json.get(MEMORY_KEY);
        if (key == null)
            throw new MemoryException("class key not found.");
        Function<JsonObject, MemoryStorable> function = deserialize.get(key.getAsString().toLowerCase());
        if (function == null)
            throw new MemoryException("class deserialization function '" + key.getAsString().toLowerCase() + "' not found.");
        return clazz.cast(function.apply(json));
    }

    public static <Adult extends MemoryStorable, T extends MemoryChildStorable<? super T, Adult>> boolean register(Class<T> clazz, BiFunction<Adult, JsonObject, T> function) {
        String key = memoryId(clazz);
        return register(key, function);
    }

    public static <Adult extends MemoryStorable, T extends MemoryChildStorable<? super T, Adult>> boolean register(String key, BiFunction<Adult, JsonObject, T> function) {
        checkNotNull(key);
        checkNotNull(function);
        if (children.containsKey(key)) {
            throw new MemoryException("class key '" + key + "' already registered.");
        } else {
            children.put(key, (BiFunction<MemoryStorable, JsonObject, MemoryChildStorable<? extends MemoryStorable, MemoryStorable>>) function);
            return true;
        }
    }

    public static <T extends MemoryStorable> boolean register(Class<T> clazz, Function<JsonObject, T> function) {
        String key = memoryId(clazz);
        return register(key, function);
    }

    public static <T extends MemoryStorable> boolean register(String key, Function<JsonObject, T> function) {
        checkNotNull(key);
        checkNotNull(function);
        if (deserialize.containsKey(key)) {
            throw new MemoryException("class key '" + key + "' already registered.");
        } else {
            deserialize.put(key, (Function<JsonObject, MemoryStorable>) function);
            return true;
        }
    }

    public static String memoryId(Class<?> clazz) {
        checkNotNull(clazz);
        String id = clazz.getName();
        if (clazz.isAnnotationPresent(MemoryName.class)) {
            MemoryName ser = clazz.getAnnotation(MemoryName.class);
            if (ser != null) {
                id = ser.value().toLowerCase();
            }
        }
        return id.toLowerCase();
    }
}
