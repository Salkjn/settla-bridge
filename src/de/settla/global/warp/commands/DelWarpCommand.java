package de.settla.global.warp.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.settla.global.GlobalPlugin;
import de.settla.global.warp.GlobalWarpPointModule;
import de.settla.global.warp.WarpPoint;
import de.settla.global.warp.WarpPoints;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import de.settla.utilities.global.command.annotations.Perm;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Perm(perm = "admin")
public class DelWarpCommand extends PlayerCommand {

	public DelWarpCommand(String name) {
		super(name);
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		
		if (ap.hasExactly(1)) {
			
			String name = ap.get(1);
			GlobalWarpPointModule mod = GlobalPlugin.getInstance().getModule(GlobalWarpPointModule.class);
			
			WarpPoints warpPoints = mod.getWarpPoints();
			
			if (warpPoints != null) {
				
				WarpPoint point = warpPoints.getWarp(name);
				
				if (point != null) {
					
					warpPoints.removeWarp(name);
					
					player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Es wurde der Warp ").color(ChatColor.GOLD).append(name).color(ChatColor.GREEN).append(" gelöscht.")
							.color(ChatColor.GOLD).create());
					
					
					mod.updateWarps(point.getServer());
					
				} else {
					player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Es existiert kein Warp mit diesem Namen.")
							.color(ChatColor.GOLD).create());
				}
				
			}
			
		} else {
			player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Verwendung: /delwarp <warp>")
					.color(ChatColor.GOLD).create());
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
