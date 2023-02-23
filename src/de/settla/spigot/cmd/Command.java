package de.settla.spigot.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.settla.local.LocalPlugin;
import de.settla.spigot.cmd.properties.Description;
import de.settla.spigot.cmd.properties.Permission;
import de.settla.spigot.cmd.properties.Sender;
import de.settla.spigot.cmd.properties.Usage;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public abstract class Command implements CommandExecutor, TabCompleter {

    protected static final String undefined = "UNDEFINED";
    protected static final String noPermissionsMessage = "You are not permitted to use this command.";
    protected static final String playerOnlyMessage = "This command may only be executed by players.";
    protected static final String consoleOnlyMessage = "This command may only be executed by the console.";

    protected static final String PERMISSION_SPLITTER = ".";
    protected static final String USAGE_SPLITTER = " ";

    // execute function
    protected abstract void execute(CommandSender sender, ArgumentParser ap);

    private final List<String> aliases;
    private final List<Command> subcmds;
    private Command parent;

    // properties:
    private String permission = null;
    private String usage = null;
    private String description = null;
    private SenderType senderType = SenderType.ANY;

    public Command(String... aliases) {
        this.aliases = Arrays.asList(aliases);
        this.subcmds = new ArrayList<>();

        // fill the properties:
        if (this.getClass().isAnnotationPresent(Permission.class)) {
            this.permission = this.getClass().getAnnotation(Permission.class).value();
        }
        if (this.getClass().isAnnotationPresent(Sender.class)) {
            this.senderType = this.getClass().getAnnotation(Sender.class).value();
        }
        if (this.getClass().isAnnotationPresent(Usage.class)) {
            this.usage = this.getClass().getAnnotation(Usage.class).value();
        }
        if (this.getClass().isAnnotationPresent(Description.class)) {
            this.description = this.getClass().getAnnotation(Description.class).value();
        }
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public String getUsage() {
        return usage;
    }

    public String getName() {
        return aliases.size() != 0 ? aliases.get(0) : undefined;
    }

    public Command getParent() {
        return parent;
    }

    public List<Command> getSubCommands() {
        return subcmds;
    }

    public String getEffectiveUsage() {
        return parent == null ? getUsage() : parent.getEffectiveUsage() + USAGE_SPLITTER + getUsage();
    }

    public String getEffectivePermission() {
        return parent == null ? getPermission()
                : parent.getEffectivePermission() + PERMISSION_SPLITTER + getPermission();
    }

    public boolean isCommand(String name) {
        return aliases.stream().anyMatch(str -> str.equalsIgnoreCase(name));
    }

    public boolean checkPermission(CommandSender sender) {
        String perm = getEffectivePermission();

        // new: add PP property
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CommandModule module = LocalPlugin.getInstance().getModule(CommandModule.class);
            if (!module.checkPP(this.getClass(), player)) {
                return false;
            }
        }

        if (perm != null) {
            return sender.hasPermission(perm);
        }
        return true;
    }

    public boolean checkSenderType(CommandSender sender) {
        return senderType == SenderType.ANY ? true
                : (senderType == SenderType.PLAYER_ONLY ? sender instanceof Player
                        : (senderType == SenderType.CONSOLE_ONLY ? sender instanceof CommandSender : false));
    }

    private boolean mayUseCommand(CommandSender sender) {
        if (checkSenderType(sender)) {
            if (checkPermission(sender)) {
                return true;
            } else {
                sender.sendMessage(noPermissionsMessage);
                return false;
            }
        } else {
            if (senderType == SenderType.PLAYER_ONLY) {
                sender.sendMessage(playerOnlyMessage);
            } else {
                sender.sendMessage(consoleOnlyMessage);
            }
            return false;
        }
    }

    public void addSubCommand(Command command) {
        command.parent = this;
        subcmds.add(command);
    }

    protected List<String> completeTab(CommandSender sender, org.bukkit.command.Command cmd, ArgumentParser ap) {
        if (ap.hasExactly(1)) {
            String val = ap.get(ap.size() - 1).toLowerCase();
            List<String> sub = subcmds.stream()
                    .filter(com -> com.checkPermission(sender) && com.checkSenderType(sender)).map(Command::getName)
                    .filter(name -> name.toLowerCase().startsWith(val)).collect(Collectors.toList());
            return sub;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (command == null || isCommand(command.getName())) {
            ArgumentParser ap = new ArgumentParser(args, sender);
            if (ap.hasAtLeast(1)) {
                String next = ap.get(0);
                for (Command subCommand : subcmds) {
                    if (subCommand.isCommand(next)) {
                        String[] newargs = Arrays.copyOfRange(args, 1, args.length);
                        return subCommand.onCommand(sender, null, s, newargs);
                    }
                }
            }
            if (mayUseCommand(sender)) {
                execute(sender, ap);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String s,
            String[] args) {
        ArgumentParser ap = new ArgumentParser(args);
        if (ap.hasAtLeast(1)) {
            String next = ap.get(0);
            for (Command subCommand : subcmds) {
                if (subCommand.isCommand(next)) {
                    String[] newargs = Arrays.copyOfRange(args, 1, args.length);
                    return subCommand.onTabComplete(sender, null, s, newargs);
                }
            }
        }
        return completeTab(sender, command, ap);
    }
}
