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

@MemoryName("FormBlockCuboid")
public class BlockCuboidForm extends CuboidForm {

    public BlockCuboidForm(JsonObject json) {
        super(json);
    }

    /**
     * We will include both, the minimum and the maximum!
     */
    public BlockCuboidForm(Vector v1, Vector v2) {
        super(Vector.getMinimum(v1, v2).floor(), Vector.getMaximum(v1, v2).floor().add(1,1,1));
    }

    @Override
    public BlockCuboidForm move(Vector vector) {
        return new BlockCuboidForm(maximum().add(vector), minimum().add(vector));
    }

    public int surface() {
        Vector diag = maximum().setY(0).subtract(minimum().setY(0));
        return diag.getBlockX()*diag.getBlockZ();
    }

    public int volume() {
        Vector diag = maximum().subtract(minimum());
        return diag.getBlockX()*diag.getBlockZ()*diag.getBlockY();
    }

}
