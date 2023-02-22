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

import de.settla.memory.MemoryChildStorable;
import de.settla.spigot.universe.cluster.Cluster.ClusterRegistry;

import com.google.gson.JsonObject;

public abstract class Region<This extends MemoryChildStorable<This, CleanWorld>> extends WorldElement<This> implements SuperForm {

    private final RegionRegistry registry;

    public Region(CleanWorld world, String name) {
        super(world, name);
        this.registry = new RegionRegistry(world.getRegionCluster().openRegistry(this));
    }

    public Region(CleanWorld world, JsonObject json) {
        super(world, json);
        this.registry = new RegionRegistry(world.getRegionCluster().openRegistry(this));
    }

    public RegionRegistry getRegionRegistry() {
        return registry;
    }

    public class RegionRegistry {
    	
    	private final ClusterRegistry registry;
    	
    	private RegionRegistry(ClusterRegistry registry) {
    		this.registry = registry;
    	}
    	
		public void registerToCollection() {
			getWorld().getRegionCollection().register(Region.this);
		}
    	
    	public void unregisterFromCollection() {
			getWorld().getRegionCollection().unregister(Region.this);
		}

		public void registerToCluster() {
			registry.register();
		}
    	
		public void unregisterFromCluster() {
			registry.unregister();
		}
			
    }   
}
