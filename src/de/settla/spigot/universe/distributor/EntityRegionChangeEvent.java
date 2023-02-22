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

package de.settla.spigot.universe.distributor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import de.settla.spigot.universe.Region;

public class EntityRegionChangeEvent extends RegionChangeEvent implements Cancellable {

    private final Cancellable cancellable;
    private final Entity entity;

    public EntityRegionChangeEvent(List<Region> from, List<Region> to, Cancellable cancellable, Entity entity) {
        super(from, to);
        checkNotNull(entity);
        checkNotNull(cancellable);
        this.cancellable = cancellable;
        this.entity = entity;
    }

    @Override
    public boolean isCancelled() {
        return cancellable.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancellable.setCancelled(cancel);
    }

    public Entity getEntity() {
        return entity;
    }
}