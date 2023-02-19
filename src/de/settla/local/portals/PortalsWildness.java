package de.settla.local.portals;

import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import de.settla.local.LocalPlugin;
import de.settla.local.cloud.LocalCloudModule;
import de.settla.utilities.local.region.WildnessRegion;
import de.settla.utilities.local.region.events.PlayerRegionChangeEvent;
import de.settla.utilities.storage.Serial;

@Serial("PORTALS_WILDNESS")
public class PortalsWildness extends WildnessRegion {

	public PortalsWildness() {
		super();
	}

	public PortalsWildness(Map<String, Object> map) {
		super(map);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void b(PlayerRegionChangeEvent event) {
		
		Player player = event.getPlayer();
		
		List<PortalRegion> regions = event.getTo(PortalRegion.class);
		
		if (!regions.isEmpty()) {
			PortalRegion region = regions.get(0);
			
			if (!region.isCachedPlayer(player.getUniqueId())) {
				region.setCachedPlayer(player.getUniqueId());
				String warp = region.getWarp();
				
				LocalPlugin.getInstance().getModule(LocalCloudModule.class).dispatchBungeeCommand(LocalCloudModule.CONSOLE, "swarp " + warp + " " + event.getPlayer().getName(), result -> {});
			}
		}
	}

}
