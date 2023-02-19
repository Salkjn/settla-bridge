package de.settla.local.warp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import de.settla.global.warp.WarpPoint;
import de.settla.global.warp.WarpPoints;
import de.settla.local.LocalPlugin;
import de.settla.local.beam.PlayerFutureBeamEvent;
import de.settla.utilities.Tuple;
import de.settla.utilities.module.Module;
import de.settla.utilities.sakko.protocol.SakkoProtocol;

public class LocalWarpPointModule extends Module<LocalPlugin> implements Listener {

	private final SakkoProtocol protocol;
	private final long maximalTimeDiference = 1000 * 10;
	private final Object lock = new Object();
	private WarpPoints warpPoints;
	
	private final Map<UUID, Tuple<String, Long>> possibleWarps = new HashMap<>();
	
	private final BukkitRunnable synchronizer = new BukkitRunnable() {
		@Override
		public void run() {
			getSakkoProtocol().ask("get_warp_points", q -> q.put("server", LocalPlugin.getInstance().getLocalConfig().getServerName(), String.class), answer -> {
				warpPoints = answer.getStorableAnswer("warps", WarpPoints.class);
			});
		}
	};
	
	public LocalWarpPointModule(LocalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
		initAnswers();
	}
	
	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}

	public WarpPoints getWarpPoints() {
		return warpPoints;
	}
	
	@Override
	public void onEnable() {
		synchronizer.runTaskLaterAsynchronously(getModuleManager(), 20 * 3);
		getModuleManager().registerListener(this);
	}
	
	@Override
	public void onDisable() {
		synchronizer.cancel();
	}
	
	public void addFutureWarp(UUID from, String to) {
		synchronized (lock) {
			possibleWarps.put(from, new Tuple<String, Long>(to, System.currentTimeMillis()));
		}
	}
	
	public void removeFutureWarps(UUID from) {
		synchronized (lock) {
			possibleWarps.remove(from);
		}
	}
	
	public boolean hasFutureWarp(UUID from) {
		return getFutureWarp(from) != null;
	}
	
	public String getFutureWarp(UUID from) {
		synchronized (lock) {
			Tuple<String, Long> tuple = possibleWarps.get(from);
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
			
		getSakkoProtocol().answer("warp_update", answer -> {
			String server = answer.getQuestion("server", String.class);
			String thisServer = LocalPlugin.getInstance().getLocalConfig().getServerName();
			if (server.equalsIgnoreCase(thisServer)) {
				synchronizer.run();
				return answer.answer();
			}
			return answer.empty();
		});
		
		getSakkoProtocol().answer("ask_warp_delay", answer -> {
			
			UUID player = answer.getQuestion("player", UUID.class);
			String point = answer.getQuestion("point", String.class);
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
							getSakkoProtocol().ask("ask_warp_delay_ready",
									que -> que.put("player", player, UUID.class).put("point", point, String.class),
									a -> {});
							return;
						} else {
							Player pp = Bukkit.getPlayer(player);
							if (pp == null || pos.distanceSquared(pp.getLocation()) >= 1 || pp.getHealth() < life) {
								cancel();
								getSakkoProtocol().ask("ask_warp_delay_break",
										que -> que.put("player", player, UUID.class).put("point", point, String.class),
										a -> {});
							} else {
								PlayerFutureBeamEvent futureBeamEvent = new PlayerFutureBeamEvent(pp);
								Bukkit.getPluginManager().callEvent(futureBeamEvent);
								if (futureBeamEvent.isCancelled()) {
									cancel();
									getSakkoProtocol().ask("ask_warp_delay_break",
											que -> que.put("player", player, UUID.class).put("point", point, String.class),
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
		
		getSakkoProtocol().answer("ask_warp_onserver", answer -> {
			Player player = Bukkit.getPlayer(answer.getQuestion("player", UUID.class));
			String pointName = answer.getQuestion("point", String.class);
			if(player != null) {
				
				if (getWarpPoints() == null) {
					return answer.answer().put("cancel", true, Boolean.class);
				} else {
					WarpPoint point = getWarpPoints().getWarp(pointName);
					
					if (point == null) {
						return answer.answer().put("cancel", true, Boolean.class);
					} else {
						PlayerWarpEvent event = new PlayerWarpEvent(player);
						Bukkit.getPluginManager().callEvent(event);
						
						World w = Bukkit.getWorld(point.getWorld());
						
						if (event.isCancelled() || w == null) {
							return answer.answer().put("cancel", true, Boolean.class);
						} else {
							new BukkitRunnable() {
								@Override
								public void run() {
									player.teleport(new Location(w, point.getX(), point.getY(), point.getZ(), point.getPitch(), point.getYaw()));
								}
							}.runTask(LocalPlugin.getInstance());
							return answer.answer().put("cancel", false, Boolean.class);
						}
					}
				}
			}
			return answer.empty();
		});
		
		getSakkoProtocol().answer("ask_warp_from", answer -> {
			Player player = Bukkit.getPlayer(answer.getQuestion("player", UUID.class));
			if(player != null) {
				PlayerWarpEvent event = new PlayerWarpEvent(player);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					return answer.answer().put("cancel", true, Boolean.class);
				} else {
					return answer.answer().put("cancel", false, Boolean.class);
				}
			}
			return answer.empty();
		});
		
		getSakkoProtocol().answer("ask_warp_to", answer -> {
			UUID player = answer.getQuestion("player", UUID.class);
			String pointName = answer.getQuestion("point", String.class);
			
			if (getWarpPoints() == null)
				return answer.empty();
			
			WarpPoint point = getWarpPoints().getWarp(pointName);
			
			if(point != null) {
				addFutureWarp(player, pointName);
				return answer.answer().put("cancel", false, Boolean.class);
			}
			return answer.empty();
		});
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerJoinEvent(PlayerSpawnLocationEvent event) {
		String pointName = getFutureWarp(event.getPlayer().getUniqueId());
		removeFutureWarps(event.getPlayer().getUniqueId());
		if (pointName != null) {
			WarpPoint point = getWarpPoints().getWarp(pointName);
			
			if (point != null) {
				
				World world = Bukkit.getWorld(point.getWorld());
				
				if (world != null) {
					event.setSpawnLocation(new Location(world, point.getX(), point.getY(), point.getZ(), point.getPitch(), point.getYaw()));	
				}
			}
		}
	}

}
