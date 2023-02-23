package de.settla.spigot.cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import de.settla.utilities.module.Module;
import de.settla.local.LocalPlugin;
import de.settla.spigot.cmd.properties.PP;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;


public class CommandModule extends Module<LocalPlugin> {

    private final Map<String, Predicate<Player>> pps = new HashMap<>();

    public CommandModule(LocalPlugin moduleManager) {
        super(moduleManager);
    }

    public void registerCommand(Command command) {
        String name = command.getName();
        PluginCommand pluginCommand = getModuleManager().getCommand(name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }
    }

    public void addPredicate(String key, Predicate<Player> predicate) {
        pps.put(key, predicate);
    }

    public boolean checkPP(Class<?> clazz, Player player) {
        if (clazz.isAnnotationPresent(PP.class)) {
            String key = this.getClass().getAnnotation(PP.class).value();
            Predicate<Player> predicate = pps.get(key);
            if (predicate != null) {
                return predicate.test(player);
            }
            return false;
        }
        return true;
    }

}