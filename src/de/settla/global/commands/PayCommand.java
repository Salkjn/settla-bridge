package de.settla.global.commands;

import java.util.UUID;

import de.settla.economy.Transfer;
import de.settla.economy.accounts.PurseHandler;
import de.settla.global.GlobalPlugin;
import de.settla.utilities.global.TextBuilder;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PayCommand extends PlayerCommand {

	public PayCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {

		if (ap.hasExactly(2)) {

			String name = ap.get(1);

			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(name);

			if (target == null) {
				new TextBuilder().title("Überweisung").text("Der Spieler ").spezial(name).text(" ist nicht Online.")
						.send(player);
			} else {
				
				if(target.getUniqueId().equals(player.getUniqueId())) {
					new TextBuilder().title("Überweisung").text("Du kannst nicht an dich selber Geld überweisen.").send(player);
					return;
				}
				
				Integer balance = ap.getInt(2);
				if (balance == null) {
					new TextBuilder().title("Überweisung").text("Du musst eine valide Zahl eingeben.").send(player);
				} else {
					
					if (balance <= 0) {
						new TextBuilder().title("Überweisung").text("Du kannst nur positive Geldbeträge überweisen.")
								.send(player);
					} else {
						Transfer transfer = GlobalPlugin.getInstance().getEconomy().transfer(
								PurseHandler.class, player.getUniqueId(), UUID.class, PurseHandler.class,
								target.getUniqueId(), UUID.class, GlobalPlugin.getInstance().getEconomy().getWrapper().forward((double)balance));

						if (transfer.isSuccess()) {
							new TextBuilder().title("Überweisung").text("Du hast ").spezial(balance + "$").text(" an ")
									.spezial(target.getName()).text(" überwiesen.").send(player);
							new TextBuilder().title("Überweisung").text("Du hast ").spezial(balance + "$").text(" von ")
									.spezial(player.getName()).text(" erhalten.").send(target);
						} else {

							switch (transfer.getFailure()) {
							case FROM_MAXIMAL_REACHED:
								new TextBuilder().title("Überweisung").text("Überweisung abgebrochen (Du hast zu viel Geld.)").send(player);
								break;
							case FROM_MINIMAL_REACHED:
								new TextBuilder().title("Überweisung").text("Überweisung abgebrochen (Du hast zu wenig Geld.)").send(player);
								break;
							case TO_MAXIMAL_REACHED:
								new TextBuilder().title("Überweisung").text("Überweisung abgebrochen ("
										+ target.getName() + " hat zu viel Geld.)").send(player);
								break;
							case TO_MINIMAL_REACHED:
								new TextBuilder().title("Überweisung").text("Überweisung abgebrochen ("
										+ target.getName() + " hat zu wenig Geld.)").send(player);
								break;

							default:
								break;
							}
						}
					}
				}
			}
		} else {
			new TextBuilder().title("Überweisung").text("Du musst einen Spieler Namen angeben: ")
					.spezial("/pay <name> <money>").send(player);
		}

	}

}
