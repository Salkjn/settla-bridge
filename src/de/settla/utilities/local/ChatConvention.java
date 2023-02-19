package de.settla.utilities.local;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public enum ChatConvention {

	TITLE, NORMAL, SPEZIAL;
	
	@Override
	public String toString() {
		switch (this) {
		case TITLE:
			return ChatColor.DARK_GREEN.toString();
		case NORMAL:
			return ChatColor.GRAY.toString();
		case SPEZIAL:
			return ChatColor.GREEN.toString();
		}
		return "";
	}
	
	public static final Pattern pattern = Pattern.compile("([^\\%]*)(\\%)(\\w+)(\\%)([^\\%]*)", Pattern.CASE_INSENSITIVE);
	
	public static String title(Object title){
		return ChatColor.DARK_GRAY+"["+ChatColor.DARK_GREEN+ChatColor.BOLD+title.toString()+ChatColor.DARK_GRAY+"] "+ChatColor.GRAY;
	}
	
	public static String spezial(Object spezial){
		return ChatColor.GREEN+spezial.toString()+ChatColor.GRAY;
	}
	
	public static String replace(String line, Function<String, Object> replacer) {
		Matcher match = pattern.matcher(line);
		StringBuilder newLine = new StringBuilder();
		boolean noMatch = true;
		while (match.find()) {
			noMatch = false;
			String id = match.group(3);
			id = replacer.apply(id).toString();
			newLine.append(match.group(1));
			newLine.append(id);
			newLine.append(match.group(5));
		}
		return noMatch ? line : newLine.toString();
	}
	
}
