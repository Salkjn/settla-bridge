package de.settla.utilities.global.command;

import net.md_5.bungee.api.CommandSender;

public class ArgumentCommand extends Command {

    public ArgumentCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    protected void execute(CommandSender sender, ArgumentParser ap) {

    }

    @ArgumentMethod
    public void doSomething(int x) {

    }

    public @interface ArgumentMethod {}

}
