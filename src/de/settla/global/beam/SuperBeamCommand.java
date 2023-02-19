package de.settla.global.beam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.settla.global.GlobalPlugin;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import de.settla.utilities.global.command.annotations.Perm;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Perm(perm = "admin")
public class SuperBeamCommand extends PlayerCommand {

	public SuperBeamCommand() {
		super("sbeam");
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		
		if(ap.hasNoArguments()) {
			player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Verwendung: /sbeam <player>").color(ChatColor.GOLD).create());
		} else {
			
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(ap.get(1));
			
			if(target != null) {
				
				if (target.getUniqueId().equals(player.getUniqueId())) {
					player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du kannst dich nicht zu dir beamen!").color(ChatColor.GOLD).create());
					return;
				}
				
				GlobalPlugin.getInstance().getModule(GlobalBeamModule.class).beam(player, target, false, result -> {
					if (result) {
						player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du wurdest zum Spieler ").color(ChatColor.GOLD).append(target.getName()).color(ChatColor.GREEN).append(" gebeamt!").color(ChatColor.GOLD).create());
					} else {
						player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Beam abgebrochen!").color(ChatColor.GOLD).create());
					}		
				});
			} else {
				player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Der Spieler ").color(ChatColor.GOLD).append(ap.get(1)).color(ChatColor.GREEN).append(" ist offline!").color(ChatColor.GOLD).create());
			}	
		}
	}
	
	@Override
	protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {
		if (ap.hasLessThan(2)) {
			String val = ap.get(ap.size()).toLowerCase();
			List<String> sub = ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).filter(name -> name.toLowerCase().startsWith(val)).collect(Collectors.toList());
			return sub;
		} else {
			return new ArrayList<>();
		}
	}
	
}
