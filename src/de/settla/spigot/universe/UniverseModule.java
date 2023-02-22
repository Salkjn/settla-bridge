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

import java.util.function.BiFunction;

import de.settla.local.LocalPlugin;
import de.settla.memory.Memory;
import de.settla.spigot.universe.event.Position;
import de.settla.spigot.universe.form.Form;
import de.settla.utilities.module.Module;
import com.google.gson.JsonObject;

public class UniverseModule extends Module<LocalPlugin> {

    public UniverseModule(LocalPlugin moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onPreEnable() {
        Form.initMemory();
        Memory.register(Vector.class, Vector::new);
        Memory.register(Position.class, Position::new);
        Memory.register(Galaxy.class, Galaxy::new);
        Memory.register(CleanWorld.class, (BiFunction<Galaxy, JsonObject, CleanWorld>) CleanWorld::new);
        Memory.register(RegionCollection.class, (BiFunction<CleanWorld, JsonObject, RegionCollection>) RegionCollection::new);
    }
}
