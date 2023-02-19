package de.settla.local.beam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import de.settla.local.LocalPlugin;
import de.settla.utilities.Tuple;
import de.settla.utilities.module.Module;
import de.settla.utilities.sakko.protocol.SakkoProtocol;

public class LocalBeamModule extends Module<LocalPlugin> implements Listener {

	private final SakkoProtocol protocol;

	private final long maximalTimeDiference = 1000 * 10;
	
	private final Map<UUID, Tuple<Location, Long>> possibleBeams = new HashMap<>();
	private final Object lock = new Object();
	
	public LocalBeamModule(LocalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
		initAnswers();
	}
	
	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}

	@Override
	public void onEnable() {
		getModuleManager().registerListener(this);
	}
	
	public void addFutureBeam(UUID from, Location to) {
		synchronized (lock) {
			possibleBeams.put(from, new Tuple<Location, Long>(to, System.currentTimeMillis()));
		}
	}
	
	public void removeFutureBeams(UUID from) {
		synchronized (lock) {
			possibleBeams.remove(from);
		}
	}
	
	public boolean hasFutureBeam(UUID from) {
		return getFutureBeam(from) != null;
	}
	
	public Location getFutureBeam(UUID from) {
		synchronized (lock) {
			Tuple<Location, Long> tuple = possibleBeams.get(from);
			if(tuple == null) {
				return null;
			} else {
				long now = System.currentTimeMillis();
				if (tuple.getY() + maximalTimeDiference > now) {
					return tuple.getX();
				} else {
					return null;
				}
			}
		}
	}
	
	private void initAnswers() {
			
		getSakkoProtocol().answer("ask_beam_delay", answer -> {
			
			UUID player = answer.getQuestion("player", UUID.class);
			UUID target = answer.getQuestion("target", UUID.class);
			int delay = answer.getQuestion("delay", Integer.class);
			
			Player p = Bukkit.getPlayer(player);
			
			if(p != null) {
				
				Location pos = p.getLocation();
				
				double life = p.getHealth();
				
				new BukkitRunnable() {
					
					int i = 0;
					
					@Override
					public void run() {
						
						if(i >= delay) {
							cancel();
							getSakkoProtocol().ask("ask_beam_delay_ready",
									que -> que.put("player", player, UUID.class)
											.put("target", target, UUID.class),
									a -> {});
							return;
						} else {
							Player pp = Bukkit.getPlayer(player);
							if (pp == null || pos.distanceSquared(pp.getLocation()) >= 1 || pp.getHealth() < life) {
								cancel();
								getSakkoProtocol().ask("ask_beam_delay_break",
										que -> que.put("player", player, UUID.class)
												.put("target", target, UUID.class),
										a -> {});
							} else {
								PlayerFutureBeamEvent futureBeamEvent = new PlayerFutureBeamEvent(pp);
								Bukkit.getPluginManager().callEvent(futureBeamEvent);
								if (futureBeamEvent.isCancelled()) {
									cancel();
									getSakkoProtocol().ask("ask_beam_delay_break",
											que -> que.put("player", player, UUID.class)
													.put("target", target, UUID.class),
											a -> {});
								}
							}
						}
						i++;
					}
					
				}.runTaskTimer(LocalPlugin.getInstance(), 10, 20);
				
			}
			return answer.empty();
		});
		
		getSakkoProtocol().answer("ask_beam_onserver", answer -> {
			Player target = Bukkit.getPlayer(answer.getQuestion("target", UUID.class));
			Player player = Bukkit.getPlayer(answer.getQuestion("player", UUID.class));
			if(player != null && target != null) {
//				PlayerBeamOnServerEvent event = new PlayerBeamOnServerEvent(player, target);
				PlayerBeamEvent event = new PlayerBeamEvent(player);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return answer.answer().put("cancel", true, Boolean.class);
				} else {
					new BukkitRunnable() {
						@Override
						public void run() {
							player.teleport(target);
						}
					}.runTask(LocalPlugin.getInstance());
					return answer.answer().put("cancel", false, Boolean.class);
				}
			}
			return answer.empty();
		});
		
		getSakkoProtocol().answer("ask_beam_from", answer -> {
//			String server = answer.getQuestion("target_server", String.class);
//			UUID target = answer.getQuestion("target", UUID.class);
			Player player = Bukkit.getPlayer(answer.getQuestion("player", UUID.class));
			if(player != null) {
//				PlayerBeamFromEvent event = new PlayerBeamFromEvent(player, server, target);
				PlayerBeamEvent event = new PlayerBeamEvent(player);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return answer.answer().put("cancel", true, Boolean.class);
				} else {
					return answer.answer().put("cancel", false, Boolean.class);
				}
			}
			return answer.empty();
		});
		
		getSakkoProtocol().answer("ask_beam_to", answer -> {
//			String server = answer.getQuestion("player_server", String.class);
			UUID player = answer.getQuestion("player", UUID.class);
			Player target = Bukkit.getPlayer(answer.getQuestion("target", UUID.class));
			if(target != null) {
//				PlayerBeamToEvent event = new PlayerBeamToEvent(target, server, player);
				PlayerBeamEvent event = new PlayerBeamEvent(target);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return answer.answer().put("cancel", true, Boolean.class);
				} else {
					addFutureBeam(player, target.getLocation());
					return answer.answer().put("cancel", false, Boolean.class);
				}
			}
			return answer.empty();
		});
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerJoinEvent(PlayerSpawnLocationEvent event) {
		Location pos = getFutureBeam(event.getPlayer().getUniqueId());
		removeFutureBeams(event.getPlayer().getUniqueId());
		if (pos != null) {
			event.setSpawnLocation(pos);
		}
	}
	
}
