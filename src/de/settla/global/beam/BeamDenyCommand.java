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

@Perm(perm = "default")
public class BeamDenyCommand extends PlayerCommand {

	public BeamDenyCommand() {
		super("beamdeny");
	}

	@Override
	protected void execute(ProxiedPlayer target, ArgumentParser ap) {
		
		if(ap.hasNoArguments()) {
			target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Verwendung: /beamdeny <player>").color(ChatColor.GOLD).create());
		} else {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(ap.get(1));
			if(player != null) {
				GlobalBeamModule module = GlobalPlugin.getInstance().getModule(GlobalBeamModule.class);
				BeamRequest request = module.getBeamRequest(player.getUniqueId(), target.getUniqueId());
				if (request != null && request.isAcceptable()) {
					module.removeBeamRequest(player.getUniqueId(), target.getUniqueId());
					player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append(target.getName()).color(ChatColor.GREEN).append(" hat deine Beam-Anfrage abgeleht!").color(ChatColor.GOLD).create());
					target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du hast die Beam-Anfrage von ").color(ChatColor.GOLD).append(player.getName()).color(ChatColor.GREEN).append(" abgeleht!").color(ChatColor.GOLD).create());
				} else {
					target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Es sind keine offenen Beam-Anfragen vom Spieler ").color(ChatColor.GOLD).append(player.getName()).color(ChatColor.GREEN).append(" verfügbar!").color(ChatColor.GOLD).create());
				}
			} else {
				target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Der Spieler ").color(ChatColor.GOLD).append(ap.get(1)).color(ChatColor.GREEN).append(" ist offline!").color(ChatColor.GOLD).create());
			}	
		}
	}
	
	@Override
	protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {
		if (ap.hasLessThan(2)) {
			String val = ap.get(ap.size()).toLowerCase();
			GlobalBeamModule module = GlobalPlugin.getInstance().getModule(GlobalBeamModule.class);
			if(sender instanceof ProxiedPlayer) {
				ProxiedPlayer t = (ProxiedPlayer) sender;
				List<String> sub = ProxyServer.getInstance().getPlayers().stream().filter(p -> {
					BeamRequest request = module.getBeamRequest(p.getUniqueId(), t.getUniqueId());
					return request != null && request.isAcceptable();
				}).map(ProxiedPlayer::getName).filter(name -> name.toLowerCase().startsWith(val)).collect(Collectors.toList());
				return sub;
			} else {
				return new ArrayList<>();
			}
		} else {
			return new ArrayList<>();
		}
	}


}
