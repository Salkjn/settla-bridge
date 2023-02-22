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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import de.settla.spigot.universe.Region;

public class PlayerRegionChangeEvent extends RegionChangeEvent implements Cancellable {

    private final Cancellable cancellable;
    private final Player player;

    public PlayerRegionChangeEvent(List<Region> from, List<Region> to, Cancellable cancellable, Player player) {
        super(from, to);
        checkNotNull(player);
        checkNotNull(cancellable);
        this.cancellable = cancellable;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return cancellable.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancellable.setCancelled(cancel);
    }

    public List<Region> subtract(List<Region> a, List<Region> b) {
        List<Region> list = new ArrayList<>();
        for (Region region : a) {
            if (!b.contains(region)) {
                list.add(region);
            }
        }
        return list;
    }

}