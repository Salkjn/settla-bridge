package de.settla.global.commands;

import java.util.UUID;

import de.settla.economy.accounts.PurseHandler;
import de.settla.global.GlobalPlugin;
import de.settla.utilities.global.TextBuilder;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MoneyCommand extends PlayerCommand {

	public MoneyCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		if(ap.hasExactly(0)) {
			double balance = GlobalPlugin.getInstance().getEconomy().getWrapper().backward(GlobalPlugin.getInstance().getEconomy().getBalance(PurseHandler.class, player.getUniqueId(), UUID.class));
			new TextBuilder().title("Geld").text("Dein Guthaben beträgt: ").spezial(balance+"$").send(player);
		} else {
			GlobalPlugin.getInstance().getUuid(uuid -> {
				if(uuid != null && GlobalPlugin.getInstance().getEconomy().exists(PurseHandler.class, uuid, UUID.class)) {
					double balance = GlobalPlugin.getInstance().getEconomy().getWrapper().backward(GlobalPlugin.getInstance().getEconomy().getBalance(PurseHandler.class, uuid, UUID.class));
					new TextBuilder().title("Geld").text("Das Guthaben von ").spezial(ap.get(1)).text(" beträgt: ").spezial(balance+"$").send(player);				
				} else {
					new TextBuilder().title("Geld").text("Wir kennen keinen Spieler mit dem Namen: " + ap.get(1)).send(player);
				}
			}, ap.get(1));
		}
	}
	
}
