package de.settla.global.warp.commands;

import java.util.List;

import de.settla.global.GlobalPlugin;
import de.settla.global.warp.GlobalWarpPointModule;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import de.settla.utilities.global.command.annotations.Perm;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Perm(perm = "admin")
public class ListWarpCommand extends PlayerCommand {

	public ListWarpCommand(String name) {
		super(name);
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		
		GlobalWarpPointModule mod = GlobalPlugin.getInstance().getModule(GlobalWarpPointModule.class);
		List<String> list = mod.getWarpPoints().getWarps();
		
		player.sendMessage(new ComponentBuilder(GlobalWarpPointModule.PREFIX).append("Warps: ")
				.color(ChatColor.GOLD).append(list.toString()).color(ChatColor.GOLD).create());
		
	}

}
