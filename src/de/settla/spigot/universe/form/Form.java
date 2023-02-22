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

import static com.google.common.base.Preconditions.checkNotNull;

import de.settla.memory.Memory;
import de.settla.memory.MemoryStorable;
import de.settla.spigot.universe.Vector;

import com.google.gson.JsonObject;

public abstract class Form implements MemoryStorable<Form> {

    private static final String UNITE_FORM_KEY = "FormUnite";
    private static final String INTERSECT_FORM_KEY = "FormIntersect";

    public static void initMemory() {
    	
        Memory.register(UNITE_FORM_KEY, json -> {
            Form f1 = Memory.deserialize(json.get("f1").getAsJsonObject(), Form.class);
            Form f2 = Memory.deserialize(json.get("f2").getAsJsonObject(), Form.class);
            return unite(f1, f2);
        });
        Memory.register(INTERSECT_FORM_KEY, json -> {
            Form f1 = Memory.deserialize(json.get("f1").getAsJsonObject(), Form.class);
            Form f2 = Memory.deserialize(json.get("f2").getAsJsonObject(), Form.class);
            return intersect(f1, f2);
        });

        Memory.register(EmptyForm.class, json -> new EmptyForm());
        Memory.register(CuboidForm.class, CuboidForm::new);
        Memory.register(BlockCuboidForm.class, BlockCuboidForm::new);
        Memory.register(BlockForm.class, BlockForm::new);
        Memory.register(ChunkForm.class, ChunkForm::new);
        Memory.register(ChunkBlockCuboidForm.class, ChunkBlockCuboidForm::new);
    }

    public static Form unite(final Form form1, final Form form2) {
        checkNotNull(form1);
        checkNotNull(form2);
        return new Form() {

            @Override
            public boolean overlaps(Vector vector) {
                return form1.overlaps(vector) || form2.overlaps(vector);
            }

            @Override
            public Vector minimum() {
                return Vector.getMinimum(form1.minimum(), form2.minimum());
            }

            @Override
            public Vector maximum() {
                return Vector.getMaximum(form1.maximum(), form2.maximum());
            }

            @Override
            public boolean intersect(Form form) {
                return form1.intersect(form) || form2.intersect(form);
            }
            
            @Override
            public Form move(Vector vector) {
                return unite(form1.move(vector), form2.move(vector));
            }

            @Override
            public JsonObject serialize() {
                JsonObject json = super.serialize();
                json.addProperty(Memory.MEMORY_KEY, UNITE_FORM_KEY);
                json.add("f1", form1.serialize());
                json.add("f2", form2.serialize());
                return json;
            }
        };
    }

    public static Form intersect(Form form1, Form form2) {
        checkNotNull(form1);
        checkNotNull(form2);
        return new Form() {

            @Override
            public boolean overlaps(Vector vector) {
                return form1.overlaps(vector) && form2.overlaps(vector);
            }

            @Override
            public Vector minimum() {
                return Vector.getMinimum(form1.minimum(), form2.minimum());
            }

            @Override
            public Vector maximum() {
                return Vector.getMaximum(form1.maximum(), form2.maximum());
            }

            @Override
            public boolean intersect(Form form) {
                return form1.intersect(form) && form2.intersect(form);
            }

            @Override
            public Form move(Vector vector) {
                return intersect(form1.move(vector), form2.move(vector));
            }

            @Override
            public JsonObject serialize() {
                JsonObject json = super.serialize();
                json.addProperty(Memory.MEMORY_KEY, INTERSECT_FORM_KEY);
                json.add("f1", form1.serialize());
                json.add("f2", form2.serialize());
                return json;
            }

        };
    }

    public abstract Vector minimum();

    public abstract Vector maximum();

    public abstract boolean overlaps(Vector vector);

    public abstract Form move(Vector vector);

    public abstract boolean intersect(Form form);
    
}
