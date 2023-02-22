package de.settla.spigot.universe.cluster;

import de.settla.spigot.universe.cluster.Cluster.ClusterRegistry;

@FunctionalInterface
public interface SuperRegistry {

	ClusterRegistry getClusterRegistry();
	
}
