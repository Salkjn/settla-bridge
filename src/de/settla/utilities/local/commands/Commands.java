package de.settla.utilities.local.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Dennis Heckmann on 13.05.17
 * Copyright (c) 2017 Dennis Heckmann
 */
public class Commands implements Listener {

    private JavaPlugin plugin;
    private List<CustomCommand> customCommands;
    //    private Map<Class<?>, Supplier<?>> dependencyInjectors;
    private List<Injection<?>> injections;

    public Commands(JavaPlugin plugin) {
        this.plugin = plugin;
        customCommands = new ArrayList<>();
        injections = new ArrayList<>();
//        registerCommand(new MegaCommand());
//        ChatColor[] values = ChatColor.values();
//        String val = "";
//        for (ChatColor value : values) {
//            val += value.name() + " = ChatColor." + value.name() + ".toString(), ";
//        }
//        System.out.println(val);
    }

    public <T> Commands addDependencyInjector(Class<T> clazz, Function<String, ? extends T> function, String condition) {
        Objects.requireNonNull(clazz, "clazz may not be null");
        Objects.requireNonNull(function, "function may not be null");
        injections.add(new Injection<>(clazz, function, condition));
//        dependencyInjectors.put(clazz, supplier);
        return this;
    }

    public <T> Commands addStaticDependencyInjector(Class<T> clazz, T o, String condition) {
        return addDependencyInjector(clazz, s -> o, condition);
    }

    public <T> Commands addForcedDependencyInjector(Class<T> clazz, Function<String, ? extends T> function) {
        injections.add(new ForcedInjection<>(clazz, function));
        return this;
    }

    public <T> Commands addStaticForcedDependencyInjector(Class<T> clazz, T o) {
        return addForcedDependencyInjector(clazz, s -> o);
    }

    private void injectDependencies(Object o) {
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        outer: for (Field field : fields) {
            for (Injection<?> injection : injections) {
                if (injection.shallInject(field)) {
//                    System.out.println("Injecting SOME SHIT!");
//                    System.out.println("Injecting SOME SHIT!");
//                    System.out.println("Injecting SOME SHIT!");
//                    System.out.println("Injecting SOME SHIT!");
//                    System.out.println("Injecting SOME SHIT!");
//                    System.out.println("Injecting SOME SHIT!");
//                    System.out.println("Injecting SOME SHIT!");
//                    System.out.println("Injecting SOME SHIT!");
//                    System.out.println("Injecting SOME SHIT!");
//                    System.out.println("Injecting SOME SHIT!");
                    injection.inject(o, field);
                    continue outer;
                }
            }
//            if (field.isAnnotationPresent(Inject.class)) {
//                Supplier<?> supplier = dependencyInjectors.get(field.getType());
//                if (supplier != null) {
//                    Object res = supplier.get();
//                    field.setAccessible(true);
//                    try {
//                        field.set(o, res);
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                    field.setAccessible(false);
//                } else {
//                    System.out.println("Could not inject dependency into ");
//                }
//            }
        }
    }

    public Commands registerCommand(Command command) {
        injectDependencies(command);
        String name = command.getName();
        PluginCommand pluginCommand = plugin.getCommand(name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        } else {
            if (command instanceof CustomCommand) {
                if (customCommands.size() == 0) {
                    // only register if necessary
                    Bukkit.getPluginManager().registerEvents(this, plugin);
                }
                customCommands.add((CustomCommand) command);
            } else {
                throw new RuntimeException("Could not find command named " + command.getName());
            }
        }
        return this;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        for (CustomCommand command : customCommands) {
            if (command.isCustomCommand(message)) {
                command.handleCustomCommand(e);
                e.setCancelled(true);
            }
        }
    }

    private class Injection<T> {
        Class<T> clazz;
        Function<String, ? extends T> function;
        private String condition;

        public Injection(Class<T> clazz, Function<String, ? extends T> function, String condition) {
            this.clazz = clazz;
            this.function = function;
            this.condition = condition;
        }

        boolean shallInject(Field field) {
            if (field.isAnnotationPresent(Inject.class)) {
                String condition = field.getAnnotation(Inject.class).value();
                return field.getType() == clazz && (this.condition == null || condition.equals(this.condition));
            } else {
                return false;
            }
        }

        void inject(Object o, Field field) {
            String argument = field.getAnnotation(Inject.class).value();
            field.setAccessible(true);
            try {
                field.set(o, function.apply(argument));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }

    private class ForcedInjection<T> extends Injection<T> {

        public ForcedInjection(Class<T> clazz, Function<String, ? extends T> function) {
            super(clazz, function, null);
        }

        @Override
        boolean shallInject(Field field) {
            return field.getType() == clazz;
        }

        @Override
        void inject(Object o, Field field) {
            field.setAccessible(true);
            try {
                field.set(o, function.apply(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }

//    @EventHandler
//    public void onTabComplete(TabCompleteEvent e) {
//        CommandSender sender = e.getSender();
//        sender.sendMessage("Buffer:" + e.getBuffer());
//        sender.sendMessage("Completitions:");
//        e.getCompletions().forEach(sender::sendMessage);
//    }

}
