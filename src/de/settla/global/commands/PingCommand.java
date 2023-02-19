package de.settla.global.commands;

import de.settla.utilities.global.TextBuilder;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class PingCommand extends PlayerCommand {

	public PingCommand(String name) {
		super(name);
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		new TextBuilder().title("Ping").text("Dein Ping beträgt: ").spezial(player.getPing()).send(player);
	}
}
