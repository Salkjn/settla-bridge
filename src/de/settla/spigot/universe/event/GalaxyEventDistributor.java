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

package de.settla.spigot.universe.event;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.event.Event;
import de.settla.spigot.universe.CleanWorld;
import de.settla.spigot.universe.Galaxy;
import de.settla.spigot.universe.Region;
import de.settla.spigot.universe.WorldElement;

public class GalaxyEventDistributor {

    private final Map<Class<? extends WorldElement>, RegionEventHandler<?>> handlers = new HashMap<>();
    private final Set<Galaxy> galaxies;
    private final Object lock = new Object();

    public GalaxyEventDistributor(Galaxy... galaxy) {
        checkNotNull(galaxy);
        galaxies = new HashSet<>(Arrays.asList(galaxy));
    }

    public void addGalaxy(Galaxy galaxy) {
        checkNotNull(galaxy);
        synchronized (lock) {
            galaxies.add(galaxy);
        }
    }

    public void removeGalaxy(Galaxy galaxy) {
        checkNotNull(galaxy);
        synchronized (lock) {
            galaxies.remove(galaxy);
        }
    }

    public void forEachGalaxy(Consumer<Galaxy> consumer) {
        synchronized (lock) {
            galaxies.forEach(consumer);
        }
    }

    public <T extends WorldElement<? super T>> void registerHandler(RegionEventHandler<T> handler) {
        checkNotNull(handler);
        synchronized (lock) {
            handlers.put(handler.getRegionClass(), handler);
        }
    }

    public void fireEvent(Event event, WorldElement worldElement) {
        checkNotNull(event);
        checkNotNull(worldElement);
        synchronized (lock) {
            RegionEventHandler<?> handler = handlers.get(worldElement.getClass());
            if (handler != null) {
                handler.fire(event, worldElement);
            } else {
                // no handler found...
                System.err.println("No RegionEventHandler for the WorldElement '" + worldElement.getName() + "' found!");
            }
        }
    }

    public void fireEvent(Event event, Position position) {
        checkNotNull(event);
        checkNotNull(position);
        forEachGalaxy(galaxy -> {
            CleanWorld world = galaxy.getWorld(position.getWorldName());
            if (world != null) {
                List<Region> regions = world.getRegionCluster().getRegions(position, Region.class);
                if (regions.isEmpty()) {
                    // if no region found, send the event to the underlying wilderness region.
                    fireEvent(event, world.getWildernessRegion());
                } else {
                    // send the event to all regions corresponding to the position.
                    regions.forEach(region -> fireEvent(event, region));
                }
            }
        });
    }

    public void fireEvent(Event event, Location location) {
        checkNotNull(event);
        checkNotNull(location);
        fireEvent(event, new Position(location));
    }

}
