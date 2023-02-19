package de.settla.global.beam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.settla.global.GlobalConfig;
import de.settla.global.GlobalPlugin;
import de.settla.utilities.global.ProxyRunnable;
import de.settla.utilities.module.Module;
import de.settla.utilities.sakko.protocol.SakkoProtocol;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GlobalBeamModule extends Module<GlobalPlugin> {

	public static final ComponentBuilder PREFIX = new ComponentBuilder("Beam ").color(ChatColor.DARK_GREEN).append("▎ ").color(ChatColor.GREEN);
	
	private final long maximal_time_distance = 1000 * 2 * 60;
	private final Map<UUID, Long> currentBeams = new HashMap<>();
	
	private final SakkoProtocol protocol;
	private List<BeamRequest> beamRequests = new ArrayList<>();
	private final Object lock = new Object();
	
	public GlobalBeamModule(GlobalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
		
		initAnswers();
		
		new ProxyRunnable() {
			
			@Override
			public void run() {
				clearNotAcceptableRequests(b -> {
					
					ProxiedPlayer player = ProxyServer.getInstance().getPlayer(b.getPlayer());
					ProxiedPlayer target = ProxyServer.getInstance().getPlayer(b.getTarget());
					
					String a = GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(b.getPlayer()).name();
					String c = GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(b.getTarget()).name();
					
					if(player != null)
						player.sendMessage(new ComponentBuilder(PREFIX).append("Die Beam-Anfrage an ").color(ChatColor.GOLD).append(c).color(ChatColor.GREEN).append(" ist abgelaufen!").color(ChatColor.GOLD).create());
					
					if(target != null)
						target.sendMessage(new ComponentBuilder(PREFIX).append("Die Beam-Anfrage von ").color(ChatColor.GOLD).append(a).color(ChatColor.GREEN).append(" ist abgelaufen!").color(ChatColor.GOLD).create());
					
				});
			}
		}.runAfterEvery(1, 1, TimeUnit.SECONDS);
	}
	
	@Override
	public void onEnable() {
		getModuleManager().registerCommand(new BeamDenyCommand());
		getModuleManager().registerCommand(new BeamAcceptCommand());
		getModuleManager().registerCommand(new BeamCommand());
		getModuleManager().registerCommand(new BeamsCommand());
		getModuleManager().registerCommand(new SuperBeamCommand());
	}

	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}
	
	public boolean hasCurrentBeam(UUID uuid) {
		Long time = currentBeams.get(uuid);
		return time == null ? false : time + maximal_time_distance >= System.currentTimeMillis();
	}
	
	public void removeBeam(UUID uuid) {
		currentBeams.put(uuid, 0L);
	}
	
	public void addBeam(UUID uuid) {
		currentBeams.put(uuid, System.currentTimeMillis());
	}

	private void initAnswers() {
		
		getSakkoProtocol().answer("ask_beam_delay_ready", answer -> {
			UUID player = answer.getQuestion("player", UUID.class);
			UUID target = answer.getQuestion("target", UUID.class);
			
			removeBeam(player);
			
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);
			ProxiedPlayer t = ProxyServer.getInstance().getPlayer(target);
			
			if(p != null && t != null) {
				beam(p, t, true, result -> {
					if (result) {
						p.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du wurdest zum Spieler ").color(ChatColor.GOLD).append(t.getName()).color(ChatColor.GREEN).append(" gebeamt!").color(ChatColor.GOLD).create());
						t.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Der Spieler ").color(ChatColor.GOLD).append(p.getName()).color(ChatColor.GREEN).append(" wurde zu dir gebeamt!").color(ChatColor.GOLD).create());
					} else {
						p.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Beam zum Spieler ").color(ChatColor.GOLD).append(t.getName()).color(ChatColor.GREEN).append(" abgebrochen!").color(ChatColor.GOLD).create());
						t.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Beam des Spielers ").color(ChatColor.GOLD).append(p.getName()).color(ChatColor.GREEN).append(" abgebrochen!").color(ChatColor.GOLD).create());
					}		
				});
			} else {
				
				String a = GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(player).name();
				String c = GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(target).name();
				
				if(p != null)
					p.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Beam zum Spieler ").color(ChatColor.GOLD).append(c).color(ChatColor.GREEN).append(" abgebrochen!").color(ChatColor.GOLD).create());
				
				if(t != null)
					t.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Beam des Spielers ").color(ChatColor.GOLD).append(a).color(ChatColor.GREEN).append(" abgebrochen!").color(ChatColor.GOLD).create());
				
			}
			
			return answer.empty();
		});

		getSakkoProtocol().answer("ask_beam_delay_break", answer -> {
			UUID player = answer.getQuestion("player", UUID.class);
			UUID target = answer.getQuestion("target", UUID.class);
			
			removeBeam(player);
			
			String a = GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(player).name();
			String c = GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(target).name();
			
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);
			ProxiedPlayer t = ProxyServer.getInstance().getPlayer(target);
			
			if(p != null)
				p.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Beam zum Spieler ").color(ChatColor.GOLD).append(c).color(ChatColor.GREEN).append(" abgebrochen!").color(ChatColor.GOLD).create());
			
			if(t != null)
				t.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Beam des Spielers ").color(ChatColor.GOLD).append(a).color(ChatColor.GREEN).append(" abgebrochen!").color(ChatColor.GOLD).create());
			
			return answer.empty();
		});
		
	}
	
	public void clearNotAcceptableRequests(Consumer<BeamRequest> rest) {
		synchronized (lock) {
			this.beamRequests.stream().filter(b -> !b.isAcceptable()).forEach(b -> rest.accept(b));
			this.beamRequests = this.beamRequests.stream().filter(b -> b.isAcceptable()).collect(Collectors.toList());
		}
	}
	
	public void addBeamRequest(UUID player, UUID target) {
		synchronized (lock) {
			List<BeamRequest> list = beamRequests.stream().filter(b -> player.equals(b.getPlayer()) && target.equals(b.getTarget())).collect(Collectors.toList());
			if(list.isEmpty()) {
				this.beamRequests.add(new BeamRequest(player, target));
			} else {
				list.forEach(b -> b.updateTime());
			}
		}
	}
	
	public BeamRequest getBeamRequest(UUID player, UUID target) {
		synchronized (lock) {
			return beamRequests.stream().filter(b -> player.equals(b.getPlayer()) && target.equals(b.getTarget())).findFirst().orElse(null);
		}
	}
	
	public void removeBeamRequest(UUID player, UUID target) {
		synchronized (lock) {
			this.beamRequests = this.beamRequests.stream().filter(b -> !(player.equals(b.getPlayer()) && target.equals(b.getTarget()))).collect(Collectors.toList());
		}
	}
	
	public void beamWithDelay(UUID player, UUID target, int delay) {
		
		addBeam(player);
		
		getSakkoProtocol().ask("ask_beam_delay",
				question -> question.put("player", player, UUID.class)
						.put("target", target, UUID.class).put("delay", delay, Integer.class),
				a -> {});
	}
	
	public void beam(ProxiedPlayer player, ProxiedPlayer target, boolean checkpermission, Consumer<Boolean> result) {
		
		if (player.getServer().getInfo().getName().equals(target.getServer().getInfo().getName())) {
			getSakkoProtocol().ask("ask_beam_onserver",
					question -> question.put("player", player.getUniqueId(), UUID.class)
							.put("target", target.getUniqueId(), UUID.class),
					a -> {
						boolean cancel = a.getAnswer("cancel", Boolean.class);
						result.accept(!cancel);
					});
		} else {
			getSakkoProtocol().ask("ask_beam_from",
					question -> question.put("player", player.getUniqueId(), UUID.class)
							.put("player_server", player.getServer().getInfo().getName(), String.class)
							.put("target", target.getUniqueId(), UUID.class)
							.put("target_server", target.getServer().getInfo().getName(), String.class),
					a -> {

						boolean cancelFrom = a.getAnswer("cancel", Boolean.class);

						if (cancelFrom) {
							result.accept(false);
						} else {
							getSakkoProtocol().ask("ask_beam_to",
									question -> question.put("player", player.getUniqueId(), UUID.class)
											.put("player_server", player.getServer().getInfo().getName(), String.class)
											.put("target", target.getUniqueId(), UUID.class)
											.put("target_server", target.getServer().getInfo().getName(), String.class),
									b -> {

										boolean cancelTo = b.getAnswer("cancel", Boolean.class);

										if (cancelTo) {
											result.accept(false);
										} else {
											if (player.isConnected() && target.isConnected()) {
												GlobalConfig config = GlobalPlugin.getInstance().getGlobalConfig();
												if (checkpermission) {
													if (!(config.isBeamServer(player.getServer().getInfo().getName()) && config.isBeamServer(target.getServer().getInfo().getName()))) {
														result.accept(false);
														return;
													}
												}
												
												player.connect(target.getServer().getInfo(), new Callback<Boolean>() {
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

}
