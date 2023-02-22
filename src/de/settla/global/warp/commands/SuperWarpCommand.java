package de.settla.global.warp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import de.settla.global.GlobalPlugin;
import de.settla.global.warp.GlobalWarpPointModule;
import de.settla.global.warp.WarpPoint;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.Command;
import de.settla.utilities.global.command.annotations.Perm;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Perm(perm = "admin")
public class SuperWarpCommand extends Command {

	public SuperWarpCommand(String name) {
		super(name);
	}

	@Override
	protected void execute(CommandSender sender, ArgumentParser ap) {
		
		if(ap.hasNoArguments()) {
			sender.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Verwendung: /swarp <warp> [player]")
					.color(ChatColor.GOLD).create());
		} else {
			
			ProxiedPlayer player = null;
			
			AtomicBoolean self = new AtomicBoolean(false);
			
			if (sender instanceof ProxiedPlayer) {
				player = ap.hasAtLeast(2) ? ProxyServer.getInstance().getPlayer(ap.get(2)) : (ProxiedPlayer) sender;
				self.set((player == sender));
			} else {
				player = ap.hasAtLeast(2) ? ProxyServer.getInstance().getPlayer(ap.get(2)) : null;
			}

			final ProxiedPlayer finalPlayer = player;
			
			if (finalPlayer == null) {
				sender.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Es wurde kein Spieler gefunden.").color(ChatColor.GOLD).create());
			} else {
				String pointName = ap.get(1);
				GlobalWarpPointModule mod = GlobalPlugin.getInstance().getModule(GlobalWarpPointModule.class);
				WarpPoint point = mod.getWarpPoints().getWarp(pointName);
				
				if(point != null) {

					mod.warp(finalPlayer, point, result -> {
						if (result) {
							if (self.get()) {
								sender.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX)
										.append("Du wurdest zum Warp ").color(ChatColor.GOLD).append(point.getName())
										.color(ChatColor.GREEN).append(" gebeamt!").color(ChatColor.GOLD).create());
							} else {
								sender.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX)
										.append("Der Spieler ").color(ChatColor.GOLD).append(finalPlayer.getName()).color(ChatColor.GREEN).append(" wurde zum Warp ").color(ChatColor.GOLD).append(point.getName())
										.color(ChatColor.GREEN).append(" gebeamt!").color(ChatColor.GOLD).create());
							}
						} else {
							if (self.get()) {
								sender.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Beam zum Warp ")
										.color(ChatColor.GOLD).append(point.getName()).color(ChatColor.GREEN)
										.append(" abgebrochen!").color(ChatColor.GOLD).create());
							} else {
								sender.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX)
										.append("Beam des Spielers ").color(ChatColor.GOLD).append(finalPlayer.getName()).color(ChatColor.GREEN).append(" wurde zum Warp ").color(ChatColor.GOLD).append(point.getName())
										.color(ChatColor.GREEN).append(" abgebrochen!").color(ChatColor.GOLD).create());
							}
						}
					});
				} else {
					sender.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Es ist uns kein Warp mit dem Namen ").color(ChatColor.GOLD).append(pointName).color(ChatColor.GREEN).append(" bekannt.").color(ChatColor.GOLD).create());
				}
			}
	
		}
		
	}
	
	@Override
	protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {
		if (ap.hasLessThan(2)) {
			String val = ap.get(ap.size()).toLowerCase();
			GlobalWarpPointModule mod = GlobalPlugin.getInstance().getModule(GlobalWarpPointModule.class);
			if (mod != null && mod.getWarpPoints() != null && mod.getWarpPoints().getWarps() != null) {
				List<String> sub = mod.getWarpPoints().getWarps().stream().filter(name -> name.toLowerCase().startsWith(val)).collect(Collectors.toList());
				return sub;
			}
			return new ArrayList<>();
		} else if(ap.hasLessThan(3)) {
			String val = ap.get(ap.size()).toLowerCase();
			List<String> sub = ProxyServer.getInstance().getPlayers().stream().filter(p -> p.getServer() != null && p.getServer().getInfo() != null && p.getServer().getInfo().getName() != null).map(ProxiedPlayer::getName)
					.filter(name -> name.toLowerCase().startsWith(val) && !name.equalsIgnoreCase(sender.getName())).collect(Collectors.toList());
			return sub;
		} else {
			return new ArrayList<>();
		}
	}

}
