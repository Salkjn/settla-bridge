package de.settla.global.beam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.settla.economy.Transfer;
import de.settla.economy.accounts.BeamAccountHandler;
import de.settla.economy.accounts.ServerAccountHandler;
import de.settla.global.GlobalConfig;
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
public class BeamAcceptCommand extends PlayerCommand {

	public BeamAcceptCommand() {
		super("beamaccept");
	}

	@Override
	protected void execute(ProxiedPlayer target, ArgumentParser ap) {

		if (ap.hasNoArguments()) {
			target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Verwendung: /beamaccept <player>")
					.color(ChatColor.GOLD).create());
		} else {

			GlobalConfig config = GlobalPlugin.getInstance().getGlobalConfig();

			if (!config.isNetworkServers(target.getServer().getInfo().getName())) {
				target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Du kannst diese Beam-Anfrage ")
						.color(ChatColor.GOLD).append("hier").color(ChatColor.GREEN).append(" nicht annehmen.")
						.color(ChatColor.GOLD).create());
				return;
			}

			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(ap.get(1));

			if (player != null) {

				GlobalBeamModule module = GlobalPlugin.getInstance().getModule(GlobalBeamModule.class);
				BeamRequest request = module.getBeamRequest(player.getUniqueId(), target.getUniqueId());

				if (request != null && request.isAcceptable()) {
					module.removeBeamRequest(player.getUniqueId(), target.getUniqueId());

					if (!config.isNetworkServers(player.getServer().getInfo().getName())) {
						player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Die Beam-Anfrage an ")
								.color(ChatColor.GOLD).append(target.getName()).color(ChatColor.GREEN).append(" wurde abgebrochen!")
								.color(ChatColor.GOLD).create());
						target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Die Beam-Anfrage von ")
								.color(ChatColor.GOLD).append(player.getName()).color(ChatColor.GREEN).append(" wurde abgebrochen!")
								.color(ChatColor.GOLD).create());
						return;
					}

					Transfer tran = GlobalPlugin.getInstance().getEconomy().transfer(BeamAccountHandler.class,
							player.getUniqueId(), UUID.class, ServerAccountHandler.class, "beams", String.class,
							GlobalPlugin.getInstance().getEconomy().getWrapper().forward(1.0));

					if (tran.isSuccess()) {

						// player.getPermissions().stream().filter(perm ->
						// perm.startsWith("beam.delay.")).map(perm ->
						// (int)Integer.parseInt(perm.substring("beam.delay.".length()))).sorted();

						int delay = 5;
						player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append(target.getName())
								.color(ChatColor.GREEN)
								.append(" hat deine Beam-Anfrage angenommen!\n Du darfst dich " + delay
										+ (delay == 1 ? " Sekunde" : " Sekunden") + " lang nicht bewegen!")
								.color(ChatColor.GOLD).create());
						target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX)
								.append("Du hast die Beam-Anfrage von ").color(ChatColor.GOLD).append(player.getName())
								.color(ChatColor.GREEN).append(" angenommen!").color(ChatColor.GOLD).create());
						module.beamWithDelay(player.getUniqueId(), target.getUniqueId(), delay);
					} else {
						player.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX)
								.append("Du hast keine verfügbaren Beams!").color(ChatColor.GOLD).create());
						target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Der Spieler ")
								.color(ChatColor.GOLD).append(player.getName()).color(ChatColor.GREEN)
								.append(" hat nicht genügend Beams!").color(ChatColor.GOLD).create());
					}

				} else {
					target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX)
							.append("Es sind keine offenen Beam-Anfragen vom Spieler ").color(ChatColor.GOLD)
							.append(player.getName()).color(ChatColor.GREEN).append(" verfügbar!").color(ChatColor.GOLD)
							.create());
				}
			} else {
				target.sendMessage(new ComponentBuilder(GlobalBeamModule.PREFIX).append("Der Spieler ")
						.color(ChatColor.GOLD).append(ap.get(1)).color(ChatColor.GREEN).append(" ist offline!")
						.color(ChatColor.GOLD).create());
			}
		}
	}

	@Override
	protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {
		if (ap.hasLessThan(2)) {
			String val = ap.get(ap.size()).toLowerCase();
			GlobalBeamModule module = GlobalPlugin.getInstance().getModule(GlobalBeamModule.class);
			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer t = (ProxiedPlayer) sender;
				List<String> sub = ProxyServer.getInstance().getPlayers().stream().filter(p -> {
					BeamRequest request = module.getBeamRequest(p.getUniqueId(), t.getUniqueId());
					return request != null && request.isAcceptable();
				}).map(ProxiedPlayer::getName).filter(name -> name.toLowerCase().startsWith(val))
						.collect(Collectors.toList());
				return sub;
			} else {
				return new ArrayList<>();
			}
		} else {
			return new ArrayList<>();
		}
	}

}
