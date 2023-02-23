package de.settla.spigot.cmd;

import org.bukkit.command.CommandSender;

public class ArgumentParser {
	
	private String[] args;
	private CommandSender sender;

	public ArgumentParser(String[] args) {
		this.args = args.clone();
	}

	public ArgumentParser(String[] args, CommandSender sender) {
		this(args);
		this.sender = sender;
	}

	public int size() {
		return args.length;
	}
    
    public CommandSender getSender() {
        return sender;
    }

	// starts by 0 and ends by size-1 
	public String get(int index) {
		return args[index];
	}

	public boolean hasAtLeast(int arguments) {
		return size() >= arguments;
	}

	public boolean hasNoArguments() {
		return size() == 0;
	}
	
	public boolean hasExactly(int arguments) {
		return size() == arguments;
	}

}
