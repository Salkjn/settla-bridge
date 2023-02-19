package de.settla.utilities.global.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.settla.utilities.global.TextBuilder;
import de.settla.utilities.global.command.annotations.Perm;
import de.settla.utilities.global.command.annotations.Sender;
import de.settla.utilities.global.command.annotations.SenderType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

public abstract class Command extends net.md_5.bungee.api.plugin.Command implements TabExecutor {

	public static final String BLACK = ChatColor.BLACK.toString(), DARK_BLUE = ChatColor.DARK_BLUE.toString(),
			DARK_GREEN = ChatColor.DARK_GREEN.toString(), DARK_AQUA = ChatColor.DARK_AQUA.toString(),
			DARK_RED = ChatColor.DARK_RED.toString(), DARK_PURPLE = ChatColor.DARK_PURPLE.toString(),
			GOLD = ChatColor.GOLD.toString(), GRAY = ChatColor.GRAY.toString(),
			DARK_GRAY = ChatColor.DARK_GRAY.toString(), BLUE = ChatColor.BLUE.toString(),
			GREEN = ChatColor.GREEN.toString(), AQUA = ChatColor.AQUA.toString(), RED = ChatColor.RED.toString(),
			LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString(), YELLOW = ChatColor.YELLOW.toString(),
			WHITE = ChatColor.WHITE.toString(), MAGIC = ChatColor.MAGIC.toString(), BOLD = ChatColor.BOLD.toString(),
			STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString(), UNDERLINE = ChatColor.UNDERLINE.toString(),
			ITALIC = ChatColor.ITALIC.toString(), RESET = ChatColor.RESET.toString();
	private static final String PERMISSION_SPLITTER = ".";
	private SenderType target;
	private Command parent;
	private String name;
	private List<String> aliases;
	private List<Command> subCommands;
	private String permission;

	public Command(String name, String... aliases) {
		super(name, null, aliases);
		this.name = name.toLowerCase();
		this.aliases = new ArrayList<>(Arrays.stream(aliases).map(String::toLowerCase).collect(Collectors.toList()));
		subCommands = new ArrayList<>();
		if (this.getClass().isAnnotationPresent(Perm.class)) {
			Perm perm = this.getClass().getAnnotation(Perm.class);
			this.permission = perm.perm();
		}
		if (this.getClass().isAnnotationPresent(Sender.class)) {
			target = this.getClass().getAnnotation(Sender.class).sender();
		} else {
			target = SenderType.ANY;
		}
	}

	public List<String> getNameAndAliases() {
		List<String> copy = new ArrayList<>(aliases);
		copy.add(getName());
		return copy;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public boolean isCommand(String name) {
		return this.name.equalsIgnoreCase(name) || aliases.stream().anyMatch(str -> str.equalsIgnoreCase(name));
	}

	public String getName() {
		return this.name;
	}

	public List<String> getAliasesList() {
		return aliases;
	}

	public void addSubCommand(Command command) {
		command.parent = this;
		subCommands.add(command);
	}

	public String getEffectivePermission() {
		return permission != null
				? (parent != null
						? ((parent.getEffectivePermission() != null
								? parent.getEffectivePermission() + PERMISSION_SPLITTER : "") + permission)
						: (permission))
				: (parent != null ? (parent.getEffectivePermission()) : (null));
	}

	/**
	 * Shall be overriden. This will be executed if the command runs
	 *
	 * @param sender
	 * @param ap
	 */
	protected abstract void execute(CommandSender sender, ArgumentParser ap);

	@Override
	public void execute(CommandSender sender, String[] args) {
		ArgumentParser ap = new ArgumentParser(args);
		if (isCommand(this.getName())) {
			if (ap.hasAtLeast(1)) {
				String next = ap.get(1);
				for (Command subCommand : subCommands) {
					if (subCommand.isCommand(next)) {
						String[] newArgs = new String[args.length - 1];
						for (int i = 1; i < args.length; i++) {
							newArgs[i - 1] = args[i];
						}
						subCommand.execute(sender, newArgs);
						return;
					}
				}
				if (mayUseCommand(sender)) {
					execute(sender, ap);
					return;
				}
			} else {
				if (mayUseCommand(sender)) {
					execute(sender, ap);
					return;
				}
			}
		}
	}

	private boolean mayUseCommand(CommandSender sender) {
		if (canUseCommand(sender)) {
			if (isPermittedToUseCommand(sender)) {
				return true;
			} else {
				new TextBuilder().text(getNoPermissionsMessage()).send(sender);
				return false;
			}
		} else {
			if (target == SenderType.PLAYER_ONLY) {
				new TextBuilder().text(getPlayerOnlyMessage()).send(sender);
			} else {
				new TextBuilder().text(getConsoleOnlyMessage()).send(sender);
			}
			return false;
		}
	}

	/**
	 * Checks permission
	 *
	 * @param sender
	 * @return
	 */
	public boolean isPermittedToUseCommand(CommandSender sender) {
		String perm = getEffectivePermission();
		if (perm != null) {
			return sender.hasPermission(perm);
		}
		return true;
	}

	/**
	 * Checks whether the CommandSender is of correct type (e.g. Player)
	 *
	 * @param sender
	 * @return
	 */
	public boolean canUseCommand(CommandSender sender) {
		return target == SenderType.ANY ? true
				: (target == SenderType.PLAYER_ONLY ? sender instanceof ProxiedPlayer
						: (target == SenderType.CONSOLE_ONLY ? sender instanceof CommandSender : false));
	}

	public List<String> getSubCommandNames() {
		return subCommands.stream().map(Command::getName).collect(Collectors.toList());
	}

	public String getNoPermissionsMessage() {
		return ChatColor.RED + "You are not permitted to use this command.";
	}

	public String getPlayerOnlyMessage() {
		return ChatColor.RED + "This command may only be executed by players.";
	}

	public String getConsoleOnlyMessage() {
		return ChatColor.RED + "This command may only be executed by the console.";
	}

	public Command getParent() {
		return parent;
	}

	public List<Command> getSubCommands() {
		return new ArrayList<>(subCommands);
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		ArgumentParser ap = new ArgumentParser(args);
		if (ap.hasAtLeast(1)) {
			String next = ap.get(1);
			for (Command subCommand : subCommands) {
				if (subCommand.isCommand(next)) {
					// Making new args list
					String[] newArgs = new String[args.length - 1];
					for (int i = 1; i < args.length; i++) {
						newArgs[i - 1] = args[i];
					}
					return subCommand.onTabComplete(sender, newArgs);
				}
			}
		}
		return completeTab(sender, ap);
	}

	protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {
//		if (ap.hasLessThan(2)) {
		if (ap.hasExactly(1)) {
			String val = ap.get(ap.size()).toLowerCase();
			List<String> sub = subCommands.stream().filter(com -> com.isPermittedToUseCommand(sender))
					.filter(com -> com.canUseCommand(sender)).map(Command::getName).filter(name -> name.toLowerCase().startsWith(val)).collect(Collectors.toList());
			return sub;
		} else {
			return new ArrayList<>();
		}
//		List<String> sub = subCommands.stream().filter(com -> com.isPermittedToUseCommand(sender))
//				.filter(com -> com.canUseCommand(sender)).map(Command::getName).filter(name -> name.toLowerCase().startsWith(val)).collect(Collectors.toList()); // getSubCommandNames();
//		
//		if (ap.hasAtLeast(1)) {	
//			if (sub.size() > 0) {
//				String val = ap.get(ap.size()).toLowerCase();
//				if (!val.isEmpty()) {
//					sub = sub.stream().filter(str -> str.startsWith(val)).collect(Collectors.toList());
//				}
//			}
//		}
//		return sub;
	}

}
