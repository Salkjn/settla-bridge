package de.settla.local.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import de.settla.global.warp.WarpPoint;
import de.settla.global.warp.WarpPoints;
import de.settla.local.LocalPlugin;
import de.settla.local.warp.LocalWarpPointModule;
import de.settla.utilities.local.region.Galaxy;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.module.Module;

public class LobbyModule extends Module<LocalPlugin> implements Listener {

	public static final String GALAXY_NAME = "lobby";
	
	public LobbyModule(LocalPlugin moduleManager) {
		super(moduleManager);
	}
	
	private void registerSomeThings() {
		Galaxy galaxy = getModuleManager().getModule(Universe.class).getGalaxy(GALAXY_NAME);
		galaxy.createWorld("world", new LobbyWildness());
	}
	
	@Override
	public void onEnable() {
		getModuleManager().getModule(Universe.class).registerGalaxy(GALAXY_NAME);
		getModuleManager().getModule(Universe.class).addToWaitingActionList(() -> registerSomeThings());
		getModuleManager().registerListener(this);
	}
	
	private Location spawnLocation;
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerSpawnLocationEvent event) {
		
		WarpPoints warpPoints = getModuleManager().getModule(LocalWarpPointModule.class).getWarpPoints();
		
		if(warpPoints == null) {
			event.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation());
			return;
		}
		
		if (spawnLocation == null) {
			WarpPoint point = warpPoints.getWarp("Spawn");
			if (point != null) {
				spawnLocation = new Location(Bukkit.getWorld(point.getWorld()), point.getX(), point.getY(), point.getZ(), point.getYaw(), point.getPitch());
			}
		}
		
		if (spawnLocation != null) {
			event.setSpawnLocation(spawnLocation);
		} else {
			event.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onRespawn(PlayerRespawnEvent event) {
		WarpPoints warpPoints = getModuleManager().getModule(LocalWarpPointModule.class).getWarpPoints();
		
		if(warpPoints == null) {
			event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
			return;
		}
		
		if (spawnLocation == null) {
			WarpPoint point = warpPoints.getWarp("Spawn");
			if (point != null) {
				spawnLocation = new Location(Bukkit.getWorld(point.getWorld()), point.getX(), point.getY(), point.getZ(), point.getYaw(), point.getPitch());
			}
		}
		
		if (spawnLocation != null) {
			event.setRespawnLocation(spawnLocation);
		} else {
			event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
		}
	}
	
}
