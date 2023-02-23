package de.settla.spigot.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.settla.spigot.cmd.properties.Description;
import de.settla.spigot.cmd.properties.Usage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
        String p = parents.stream().reduce("", (a, b) -> a + " " + b);

        List<String> commands = new ArrayList<>();

        command.getSubCommands().forEach(cmd -> {
            if (cmd.checkPermission(sender)) {
                String usage = "";
                if (cmd.getClass().isAnnotationPresent(Usage.class)) {
                    Usage use = cmd.getClass().getAnnotation(Usage.class);
                    usage = use.value();
                }
                String des = "";
                if (cmd.getClass().isAnnotationPresent(Description.class)) {
                    Description d = cmd.getClass().getAnnotation(Description.class);
                    des = d.value();
                }
                commands.add(
                        cmd.getName() + " " + ChatColor.AQUA + usage + ChatColor.GRAY + " - " + ChatColor.ITALIC + des);
            }
        });

        String name = command.getName();
        List<String> list = new ArrayList<>();
        commands.forEach(n -> list.add("/" + (p.isEmpty() ? "" : p.trim() + " ") + name.trim() + " " + n.trim()));
        return list;
    }

    public OverviewCommand(String... aliases) {
        super(aliases);
    }

    @Override
    protected void execute(CommandSender sender, ArgumentParser ap) {
        List<String> cmds = getAllCommands(this, sender);

        List<String> parents = getParents(this, new ArrayList<>());
        Collections.reverse(parents);
        String p = parents.stream().reduce("", (a, b) -> a + " " + b);

        sender.sendMessage("");
        sender.sendMessage("");
        sender.sendMessage("     " + ChatColor.GOLD + ChatColor.BOLD
                + ((p.isEmpty() ? "" : p + " ") + getName()).toUpperCase() + " BEFEHLE");
        sender.sendMessage("");
        for (String n : cmds) {
            sender.sendMessage(" " + ChatColor.YELLOW + n);
        }
        sender.sendMessage("");
    }

}
