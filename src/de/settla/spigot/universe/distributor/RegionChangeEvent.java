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

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import de.settla.spigot.universe.Region;

import static com.google.common.base.Preconditions.checkNotNull;

public class RegionChangeEvent extends Event {

    private final List<Region> from, to;

    public RegionChangeEvent(List<Region> from, List<Region> to) {
        super();
        checkNotNull(from);
        checkNotNull(to);
        this.from = from;
        this.to = to;
    }

    public List<Region> getFrom() {
        return from;
    }

    public List<Region> getTo() {
        return to;
    }

    public HandlerList getHandlers() {
        return null;
    }

}