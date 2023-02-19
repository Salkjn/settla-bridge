package de.settla.global.warp.commands;

import de.settla.global.GlobalPlugin;
import de.settla.global.warp.GlobalWarpPointModule;
import de.settla.global.warp.WarpPoint;
import de.settla.global.warp.WarpPoints;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import de.settla.utilities.global.command.annotations.Perm;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Perm(perm = "admin")
public class SetWarpCommand extends PlayerCommand {

	public SetWarpCommand(String name) {
		super(name);
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		
		if (ap.hasExactly(7)) {
			
			String name = ap.get(1);
			String world = ap.get(2);
			String server = player.getServer().getInfo().getName();
			double x = ap.getDouble(3);
			double y = ap.getDouble(4);
			double z = ap.getDouble(5);
			float pitch = ap.getDouble(6).floatValue();
			float yaw = ap.getDouble(7).floatValue();
			
			GlobalWarpPointModule mod = GlobalPlugin.getInstance().getModule(GlobalWarpPointModule.class);
			
			WarpPoints warpPoints = mod.getWarpPoints();
			
			if (warpPoints != null) {
				
				if (warpPoints.getWarp(name) == null) {
					
					WarpPoint point = new WarpPoint(name, x, y, z, pitch, yaw, world, server);
					
					warpPoints.addWarp(point);
					
					player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Es wurde ein neuer Warp mit dem Namen ").color(ChatColor.GOLD).append(name).color(ChatColor.GREEN).append(" hinzugefügt.")
							.color(ChatColor.GOLD).create());
					
					
					mod.updateWarps(server);
					
				} else {
					player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Dieser Name ist vergeben!")
							.color(ChatColor.GOLD).create());
				}
				
			}
			
		} else {
			player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Verwendung: /setwarp <warp> <world> <x> <y> <z> <pitch> <yaw>")
					.color(ChatColor.GOLD).create());
		}
		
	}
	

}
