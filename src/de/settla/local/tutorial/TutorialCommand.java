package de.settla.local.tutorial;

import org.bukkit.entity.Player;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.PlayerCommand;

public class TutorialCommand extends PlayerCommand {

	public TutorialCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	protected void execute(Player player, ArgumentParser ap) {
		if (ap.hasExactly(1)) {
			String bookName = ap.get(1);
			LocalPlugin.getInstance().getModule(TutorialModule.class).consumeTutorials(c -> {
				TutorialBook book = c.stream().filter(tutorial -> tutorial.getName().equalsIgnoreCase(bookName))
						.findFirst().orElse(null);
				if (book == null) {
					player.sendMessage(
							ChatConvention.title("Tutorial") + "Es wurde kein Tutorial unter diesem Namen gefunden.");
				} else {
					LocalPlugin.getInstance().getModule(TutorialModule.class).openBook(player, book.getTitle(),
							book.getAuthor(), book.getPages());
				}
			});
		} else {
			player.sendMessage(ChatConvention.title("Tutorial") + "Nutze: /tutorial <buch>");
		}
	}

}
