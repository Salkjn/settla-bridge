package de.settla.utilities.global.command;

import de.settla.utilities.global.TextBuilder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class PlayerCommand extends Command {

	public PlayerCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			new TextBuilder().text(getPlayerOnlyMessage()).send(sender);
		} else {
			super.execute(sender, args);
		}
	}

	protected abstract void execute(ProxiedPlayer player, ArgumentParser ap);

	@Override
	protected void execute(CommandSender sender, ArgumentParser ap) {
		execute((ProxiedPlayer) sender, ap);
	}

}
