package de.settla.global.beam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.settla.economy.accounts.BeamAccountHandler;
import de.settla.global.GlobalPlugin;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import de.settla.utilities.global.command.annotations.Perm;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Perm(perm = "default")
public class BeamsCommand extends PlayerCommand {

	public BeamsCommand() {
		super("beams");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		
		int beams = GlobalPlugin.getInstance().getEconomy().getWrapper().backward(GlobalPlugin.getInstance()
				.getEconomy().getBalance(BeamAccountHandler.class, player.getUniqueId(), UUID.class)).intValue();

		StringBuilder sb = new StringBuilder();

		sb.append("§7Beams: §a");
		boolean a = true;
		for (int i = 0; i < 10; i++) {
			if (i >= beams && a) {
				a = false;
				sb.append("§8");
			}
			sb.append("▌");
		}

		String str = sb.toString();
		player.sendMessage(str);
	}

	@Override
	protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {
		return new ArrayList<>();
	}

}
