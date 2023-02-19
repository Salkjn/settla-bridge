package de.settla.local.npc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class NpcListener implements Listener {
	
	private final NpcModule module;
	
	public NpcListener(NpcModule module) {
		super();
		this.module = module;
	}

	public NpcModule getModule() {
		return module;
	}
	
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (event.getPlayer().isOnline()) {
					NpcPacketReader reader = NpcPacketReader.getOrCreatePacketReader(event.getPlayer());
					reader.inject();
				}
			}
		}.runTaskLater(module.getModuleManager(), 20);
	}

	@EventHandler
	public void playerQuitEvent(PlayerQuitEvent event) {
		NpcPacketReader reader = NpcPacketReader.getOrCreatePacketReader(event.getPlayer());
		reader.uninject();
	}
	
	@EventHandler
	public void playerRespawnEvent(PlayerRespawnEvent event) {
		module.throughNpcs(n -> n.getNpcEntity().removeWatcher(event.getPlayer()));
	}
}
