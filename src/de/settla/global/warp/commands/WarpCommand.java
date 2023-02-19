package de.settla.global.warp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.settla.global.GlobalPlugin;
import de.settla.global.warp.GlobalWarpPointModule;
import de.settla.global.warp.WarpPoint;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

//@Perm(perm = "default")
public class WarpCommand extends PlayerCommand {

	public WarpCommand(String name) {
		super(name);
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		
		if(ap.hasNoArguments()) {
			player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Verwendung: /warp <warp>")
					.color(ChatColor.GOLD).create());
		} else {
			
			String pointName = ap.get(1);
			GlobalWarpPointModule mod = GlobalPlugin.getInstance().getModule(GlobalWarpPointModule.class);
			WarpPoint point = mod.getWarpPoints().getWarp(pointName);
			
			if(point != null) {
				
				if (mod.hasCurrentWarp(player.getUniqueId())) {
					player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Du befindest dich bereits in einem Warp-Vorgang.")
							.color(ChatColor.GOLD).create());
				} else {
					int delay = 5;
					
					player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Du wirst zum Warp ").color(ChatColor.GOLD).append(point.getName()).color(ChatColor.GREEN).append(" gebeamt!\n Du darfst dich " + delay
									+ (delay == 1 ? " Sekunde" : " Sekunden") + " lang nicht bewegen!")
							.color(ChatColor.GOLD).create());
					
					mod.warpWithDelay(player.getUniqueId(), point, delay);
				}
			} else {
				player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Es ist uns kein Warp mit dem Namen ").color(ChatColor.GOLD).append(pointName).color(ChatColor.GREEN).append(" bekannt.").color(ChatColor.GOLD).create());
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
		} else {
			return new ArrayList<>();
		}
	}
	
}
