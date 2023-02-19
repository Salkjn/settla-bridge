package de.settla.local.cloud;

import org.bukkit.command.CommandSender;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.Command;
import de.settla.utilities.local.commands.OverviewCommand;
import de.settla.utilities.local.commands.Perm;

public class LocalCloudCommand extends OverviewCommand {

	public LocalCloudCommand() {
		super("localcloud");
		addSubCommand(new CommandExeConsole("cmd-console"));
	}

	@Perm("localcloud")
	class CommandExeConsole extends Command {

		public CommandExeConsole(String name) {
			super(name);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {
			
			LocalPlugin.getInstance().getModule(LocalCloudModule.class).dispatchBungeeCommand(LocalCloudModule.CONSOLE,
					ap.getFrom(0), result -> {});
			
		}
		
	}
	
}
