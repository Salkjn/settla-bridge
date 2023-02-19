package de.settla.utilities.local.region.space.selection;

import de.settla.utilities.local.region.BukkitWorld;
import de.settla.utilities.local.region.space.BlockList;

public interface Selector {

	BlockList select(BukkitWorld world);
	
}
