package de.settla.utilities.local.region.space.generation;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import de.settla.utilities.local.region.WildnessRegion;
import de.settla.utilities.local.region.events.BlockRegionChangeEvent;
import de.settla.utilities.local.region.events.EntityRegionChangeEvent;
import de.settla.utilities.local.region.events.PlayerRegionChangeEvent;

@SerializableAs("GEN_WILDNESS")
public class GenerationWildness extends WildnessRegion {

	public GenerationWildness() {
		super();
	}

	public GenerationWildness(Map<String, Object> map) {
		super(map);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void b(PlayerRegionChangeEvent event) {
		
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void b(EntityRegionChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void b(BlockRegionChangeEvent event) {
		event.setCancelled(true);
	}
	
}
