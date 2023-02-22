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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import de.settla.spigot.universe.cluster.Cluster;
import de.settla.spigot.universe.cluster.Cluster.ClusterRegistry;
import de.settla.spigot.universe.form.Form;

public class RegionCluster {

	private final Cluster cluster;
	private final CleanWorld world;

	RegionCluster(CleanWorld world) {
		checkNotNull(world);
		this.world = world;
		this.cluster = new Cluster();
	}

	public CleanWorld getWorld() {
		return world;
	}
	
	public <T extends Region<?>> List<T> getRegions(Vector vector, Class<? extends T> clazz) {
		return cluster.getSuperForms(vector, clazz);
	}

	public <T extends Region<?>> List<T> getRegions(Form form, Class<? extends T> clazz) {
		return cluster.getSuperForms(form, clazz);
	}

	ClusterRegistry openRegistry(SuperForm form) {
    	return cluster.openRegistry(form);
    }
   
}

