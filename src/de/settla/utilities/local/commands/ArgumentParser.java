package de.settla.utilities.local.commands;

import java.util.function.Consumer;

import org.bukkit.command.CommandSender;

public class ArgumentParser {
	
	private String[] args;
	private org.bukkit.command.Command command;
	private CommandSender sender;
	private boolean valid;

	public ArgumentParser(String[] args) {
		this.args = args.clone();
		valid = true;
	}

	public ArgumentParser(String[] args, org.bukkit.command.Command command, CommandSender sender) {
		this(args);
		this.command = command;
		this.sender = sender;
	}
	
//	public static ArgumentParser parse(String[] args){
//		return new ArgumentParser(args);
//	}

	public int size(){
		return args.length;
	}
	/**
	 * 
	 * @param index starts at 1
	 * @return the string at index index-1
	 */
	public String get(int index){
		return args[index-1];
	}

	public String getFrom(int index) {
		return getFrom(index, " ");
	}

	public String getFrom(int index, String splitter) {
		if (index > size()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int i = index; i < args.length; i++) {
			if (!first) {
				sb.append(splitter);
			} else {
				first = false;
			}
			sb.append(args[i]);
		}
		return sb.toString();
	}
	
	public Double getDouble(int index){
		try {
			Double d = Double.parseDouble(get(index));
			return d;
		} catch(Exception ex){
			return null;
		}
	}

	public Double getDouble(int index, Consumer<CommandSender> onFailure) {
		Double d = getDouble(index);
		if (d == null) {
			valid = false;
			onFailure.accept(this.sender);
		}
		return d;
	}

	public Double getDouble(int index, String errorMessage) {
		return getDouble(index, sender -> sender.sendMessage(errorMessage));
	}

	public Long getLong(int index) {
		try {
			Long l = Long.parseLong(get(index));
			return l;
		} catch (Exception e) {
			return null;
		}
	}

	public Long getLong(int index, Consumer<CommandSender> onFailure) {
		Long l = getLong(index);
		if (l == null) {
			valid = false;
			onFailure.accept(sender);
		}
		return l;
	}

	public Long getLong(int index, String errorMessage) {
		return getLong(index, sender -> sender.sendMessage(errorMessage));
	}
	
	public Integer getInt(int index){
		try {
			Integer i = Integer.parseInt(get(index));
			return i;
		} catch(Exception ex){
			return null;
		}
	}

	public Integer getInt(int index, Consumer<CommandSender> onFailure) {
		Integer i = getInt(index);
		if (i == null) {
			valid = false;
			onFailure.accept(sender);
		}
		return i;
	}

	public Integer getInt(int index, String errorMessage) {
		return getInt(index, sender -> sender.sendMessage(errorMessage));
	}
	
	public Boolean getBoolean(int index){
		try {
			Boolean b = Boolean.parseBoolean(get(index));
			return b;
		} catch(Exception ex){
			return null;
		}
	}

	public Boolean getBoolean(int index, Consumer<CommandSender> onFailure) {
		Boolean b = getBoolean(index);
		if (b == null) {
			valid = false;
			onFailure.accept(sender);
		}
		return b;
	}

	public Boolean getBoolean(int index, String errorMessage) {
		return getBoolean(index, sender -> sender.sendMessage(errorMessage));
	}
	
	public boolean hasNoArguments(){
		return size() == 0;
	}
	
	public boolean hasExactly(int arguments){
		return size() == arguments;
	}
	
	public boolean hasNotExactly(int arguments){
		return !hasExactly(arguments);
	}
	
	public boolean hasAtLeast(int arguments){
		return size() >= arguments;
	}
	
	public boolean hasLessThan(int arguments){
		return size() < arguments;
	}

	public String[] getArgs() {
		return this.args;
	}

	public org.bukkit.command.Command getCommand() {
		return command;
	}

	public CommandSender getSender() {
		return sender;
	}

	public boolean isValid() {
		return valid;
	}

}
