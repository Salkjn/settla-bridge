package de.settla.global.warp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import de.settla.global.GlobalPlugin;
import de.settla.global.warp.commands.DelWarpCommand;
import de.settla.global.warp.commands.ListWarpCommand;
import de.settla.global.warp.commands.SetWarpCommand;
import de.settla.global.warp.commands.SuperWarpCommand;
import de.settla.global.warp.commands.WarpCommand;
import de.settla.utilities.module.Module;
import de.settla.utilities.sakko.protocol.SakkoProtocol;
import de.settla.utilities.storage.Database;
import de.settla.utilities.storage.Storage;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GlobalWarpPointModule extends Module<GlobalPlugin> {

	public static final ComponentBuilder PREFIX = new ComponentBuilder("Warp ").color(ChatColor.DARK_GREEN).append("▎ ").color(ChatColor.GREEN);
	
	private final SakkoProtocol protocol;
	private Storage<WarpPoints> warps;

	private final long maximal_time_distance = 1000 * 2 * 60;
	private final Map<UUID, Long> currentWarps = new HashMap<>();
	
	public GlobalWarpPointModule(GlobalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
		initAnswers();
	}

	@Override
	public void onEnable() {
		Database<WarpPoints> database = new Database<>("warps", new File("plugins/SettlaBridge/warppoints.data"),
				n -> new WarpPoints(), WarpPoints.class);
		warps = new Storage<>(database);
		ProxyServer.getInstance().getScheduler().schedule(GlobalPlugin.getInstance(), warps, 0, 5 * 60,
				TimeUnit.SECONDS);
		
		getModuleManager().registerCommand(new WarpCommand("warp"));
		getModuleManager().registerCommand(new SuperWarpCommand("swarp"));
		getModuleManager().registerCommand(new DelWarpCommand("delwarp"));
		getModuleManager().registerCommand(new SetWarpCommand("setwarp"));
		getModuleManager().registerCommand(new ListWarpCommand("warps"));
		
	}

	@Override
	public void onDisable() {
		warps.run();
	}

	public WarpPoints getWarpPoints() {
		return warps.object();
	}

	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}
	
	public boolean hasCurrentWarp(UUID uuid) {
		Long time = currentWarps.get(uuid);
		return time == null ? false : time + maximal_time_distance >= System.currentTimeMillis();
	}
	
	public void removeWarp(UUID uuid) {
		currentWarps.put(uuid, 0L);
	}
	
	public void addWarp(UUID uuid) {
		currentWarps.put(uuid, System.currentTimeMillis());
	}
	
	private void initAnswers() {

		getSakkoProtocol().answer("get_warp_points", answer -> {
			String server = answer.getQuestion("server", String.class);
			return answer.answer().put("warps",
					getWarpPoints() == null ? new WarpPoints() : getWarpPoints().getWarpPoints(server));
		});

		getSakkoProtocol().answer("ask_warp_delay_ready", answer -> {
			UUID player = answer.getQuestion("player", UUID.class);
			String pointName = answer.getQuestion("point", String.class);

			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);
			
			removeWarp(player);
			
			if (p != null) {

				if (getWarpPoints() == null) {

				} else {

					WarpPoint point = getWarpPoints().getWarp(pointName);

					if (point == null) {

					} else {
						warp(p, point, result -> {
							if (result) {
								p.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX)
										.append("Du wurdest zum Warp ").color(ChatColor.GOLD).append(point.getName())
										.color(ChatColor.GREEN).append(" gebeamt!").color(ChatColor.GOLD).create());
							} else {
								p.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Beam zum Warp ")
										.color(ChatColor.GOLD).append(point.getName()).color(ChatColor.GREEN)
										.append(" abgebrochen!").color(ChatColor.GOLD).create());
							}
						});
					}
				}
			}

			return answer.empty();
		});

		getSakkoProtocol().answer("ask_warp_delay_break", answer -> {
			UUID player = answer.getQuestion("player", UUID.class);
			String point = answer.getQuestion("point", String.class);
			
			removeWarp(player);
			
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);
			if (p != null)
				p.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Beam zum Warp ")
						.color(ChatColor.GOLD).append(point).color(ChatColor.GREEN).append(" abgebrochen!")
						.color(ChatColor.GOLD).create());
			return answer.empty();
		});

	}

	public void updateWarps(String server) {
		getSakkoProtocol().ask("warp_update", question -> question.put("server", server, String.class), a -> {
				});
	}
	
	public void warpWithDelay(UUID player, WarpPoint point, int delay) {
		
		addWarp(player);
		
		getSakkoProtocol().ask("ask_warp_delay", question -> question.put("player", player, UUID.class)
				.put("point", point.getName(), String.class).put("delay", delay, Integer.class), a -> {});
	}
	
	public void warp(ProxiedPlayer player, WarpPoint point, Consumer<Boolean> result) {

		if (player.getServer().getInfo().getName().equals(point.getServer())) {
			getSakkoProtocol().ask("ask_warp_onserver", question -> question
					.put("player", player.getUniqueId(), UUID.class).put("point", point.getName(), String.class), a -> {
						boolean cancel = a.getAnswer("cancel", Boolean.class);
						result.accept(!cancel);
					});
		} else {
			
			ProxyServer.getInstance().getServers().get(point.getServer()).ping(new Callback<ServerPing>() {
		         
	            @Override
	            public void done(ServerPing ping, Throwable error) {
	                if(error != null) {
	                	result.accept(false);
	                } else {
	                	getSakkoProtocol().ask("ask_warp_from",
	    						question -> question.put("player", player.getUniqueId(), UUID.class), a -> {

	    							boolean cancelFrom = a.getAnswer("cancel", Boolean.class);

	    							if (cancelFrom) {
	    								result.accept(false);
	    							} else {
	    								
	    								ServerInfo server = ProxyServer.getInstance()
	    										.getServerInfo(point.getServer());
	    								
	    								getSakkoProtocol().ask("ask_warp_to",
    											question -> question.put("player", player.getUniqueId(), UUID.class).put("point",
    													point.getName(), String.class),
    											b -> {

    												boolean cancelTo = b.getAnswer("cancel", Boolean.class);

    												if (cancelTo) {
    													result.accept(false);
    												} else {

    													if (player.isConnected() && server != null) {
    														player.connect(server, new Callback<Boolean>() {
    												            @Override
    												            public void done(Boolean bool, Throwable error) {
    												            	result.accept(bool);
    												            }
    														});
    													} else {
    														result.accept(false);
    													}
    												}
    											});
	    							}

	    						});
	                }
	             
	            }
	        });
		}
	}

}
