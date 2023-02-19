package de.settla.local.tutorial;

import org.bukkit.entity.Player;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.Perm;
import de.settla.utilities.local.commands.PlayerCommand;

@Perm("tutorial.reload")
public class TutorialReload extends PlayerCommand {

	public TutorialReload(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	protected void execute(Player player, ArgumentParser ap) {
		LocalPlugin.getInstance().getModule(TutorialModule.class).initConfig();
		player.sendMessage(ChatConvention.title("Tutorial") + "Tutorials wurden neu geladen.");
	}

}
