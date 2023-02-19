package de.settla.utilities.global.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.settla.utilities.global.Utils;
import de.settla.utilities.global.command.annotations.Description;
import de.settla.utilities.global.command.annotations.Usage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class OverviewCommand extends Command {
	
	public static List<String> getParents(Command command, List<String> parents) {
		if (command.getParent() != null) {
			parents.add(command.getParent().getName());
			return getParents(command.getParent(), parents);
		} else {
			return parents;
		}
	}

	public static List<String> getAllCommands(Command command, CommandSender sender) {
		List<String> parents = getParents(command, new ArrayList<>());
		Collections.reverse(parents);
		String p = Utils.toString(parents, " ", str -> str);

		List<String> commands = new ArrayList<>();
		
		command.getSubCommands().forEach(cmd -> {
			if(cmd.isPermittedToUseCommand(sender)) {
				String usage = "";
				if (cmd.getClass().isAnnotationPresent(Usage.class)) {
					Usage use = cmd.getClass().getAnnotation(Usage.class);
					usage = use.usage();
				}
				String des = "";
				if (cmd.getClass().isAnnotationPresent(Description.class)) {
					Description d = cmd.getClass().getAnnotation(Description.class);
					des = d.description();
				}
				commands.add(cmd.getName() + " " + ChatColor.DARK_PURPLE + usage + ChatColor.GRAY +  " - " + ChatColor.ITALIC + des);
			}
		});

		String name = command.getName();
		List<String> list = new ArrayList<>();
		commands.forEach(n -> list.add("/" + (p.isEmpty() ? "" : p+" ") + name + " " + n));
		return list;
	}

	public OverviewCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(CommandSender sender, ArgumentParser ap) {
		List<String> cmds = getAllCommands(this, sender);
		
		List<String> parents = getParents(this, new ArrayList<>());
		Collections.reverse(parents);
		String p = Utils.toString(parents, " ", str -> str);
		
		sender.sendMessage("");
		sender.sendMessage("");
		sender.sendMessage("     " + ChatColor.DARK_PURPLE + ChatColor.BOLD + ((p.isEmpty() ? "" : p+" ") + getName()).toUpperCase() + " COMMANDS");
		sender.sendMessage("");
		for (String n : cmds) {
			sender.sendMessage(" " + ChatColor.DARK_PURPLE + n);
		}
		sender.sendMessage("");
	}

}
