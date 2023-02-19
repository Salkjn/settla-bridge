package de.settla.global.beam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.settla.global.GlobalConfig;
import de.settla.global.GlobalPlugin;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import de.settla.utilities.global.command.annotations.Perm;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Perm(perm = "default")
public class BeamCommand extends PlayerCommand {

	public BeamCommand() {
		super("beam");
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		
		if(ap.hasNoArguments()) {
			player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Verwendung: /beam <player>").color(ChatColor.GOLD).create());
		} else {
			
			GlobalConfig config = GlobalPlugin.getInstance().getGlobalConfig();
			
			if (!config.isNetworkServers(player.getServer().getInfo().getName())) {
				player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du kannst diesen Befehl ").color(ChatColor.GOLD).append("hier").color(ChatColor.GREEN).append(" nicht nutzen!").color(ChatColor.GOLD).create());
				return;
			}
			
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(ap.get(1));
			
			if(target != null) {
				
				if (target.getUniqueId().equals(player.getUniqueId())) {
					player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du kannst dich nicht zu dir beamen!").color(ChatColor.GOLD).create());
					return;
				}
				
				if (!config.isNetworkServers(target.getServer().getInfo().getName())) {
					player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du kannst dich zu ").color(ChatColor.GOLD).append(target.getName()).color(ChatColor.GREEN).append(" nicht beamen!").color(ChatColor.GOLD).create());
					return;
				}
				
				GlobalBeamModule module = GlobalPlugin.getInstance().getModule(GlobalBeamModule.class);
				
				if (module.hasCurrentBeam(player.getUniqueId())) {
					player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du befindest dich bereits in einem Beam-Vorgang.")
							.color(ChatColor.GOLD).create());
				} else {
					module.addBeamRequest(player.getUniqueId(), target.getUniqueId());
					
					player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du hast eine Beam-Anfrage an ").color(ChatColor.GOLD).append(target.getName()).color(ChatColor.GREEN).append(" gesendet!").color(ChatColor.GOLD).create());
					
					BaseComponent[] request = new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du hast eine Beam-Anfrage von ").color(ChatColor.GOLD).append(player.getName()).color(ChatColor.GREEN).append(" erhalten:").color(ChatColor.GOLD)
							.append("\n\n      [").color(ChatColor.DARK_GRAY).append("").reset()
							.append(" Annehmen ").event(new ClickEvent(Action.RUN_COMMAND, "/beamaccept " + player.getName()))
							.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Annehmen").color(ChatColor.GREEN).create())).color(ChatColor.GREEN)
							.append("").reset().append("]   [").color(ChatColor.DARK_GRAY)
							.append(" Ablehnen ").event(new ClickEvent(Action.RUN_COMMAND, "/beamdeny " + player.getName()))
							.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Ablehnen").color(ChatColor.RED).create())).color(ChatColor.RED)
							.append("").reset().append("]\n").color(ChatColor.DARK_GRAY).create();
					
					target.sendMessage(request);
				}
			} else {
				player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Der Spieler ").color(ChatColor.GOLD).append(ap.get(1)).color(ChatColor.GREEN).append(" ist offline!").color(ChatColor.GOLD).create());
			}	
		}
	}
	
	@Override
	protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {
		if (ap.hasLessThan(2)) {
			String val = ap.get(ap.size()).toLowerCase();
			GlobalConfig config = GlobalPlugin.getInstance().getGlobalConfig();
			List<String> sub = ProxyServer.getInstance().getPlayers().stream().filter(p -> p.getServer() != null && p.getServer().getInfo() != null && p.getServer().getInfo().getName() != null && config.isNetworkServers(p.getServer().getInfo().getName())).map(ProxiedPlayer::getName)
					.filter(name -> name.toLowerCase().startsWith(val) && !name.equalsIgnoreCase(sender.getName())).collect(Collectors.toList());
			return sub;
		} else {
			return new ArrayList<>();
		}
	}

}
