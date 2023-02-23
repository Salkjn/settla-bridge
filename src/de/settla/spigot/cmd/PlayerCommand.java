package de.settla.spigot.cmd;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerCommand extends Command {

    public PlayerCommand(String... aliases) {
		super(aliases);
	}

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(playerOnlyMessage);
            return true;
        } else {
            return super.onCommand(sender, command, s, args);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.singletonList("");
        } else {
            return super.onTabComplete(sender, command, s, args);
        }
    }

    @Override
    protected void execute(CommandSender sender, ArgumentParser ap) {
        execute((Player) sender, ap);
    }

    protected abstract void execute(Player player, ArgumentParser ap);
        
}
