package de.settla.spigot.cmd;

import org.bukkit.ChatColor;

public class CC {

	private String str = "	➙	➚	➛	➜	➝	➞	➟	27A0	➠	➡	➢	➣	➤	➥	➦	➧	➨	➩	➪	➫	➬	➭	➮	➯	➰	➱	➲	➳	➴	➵	➶	➷	➸	➹	➺	➻	➼	➽	➾";

	// 10
	public static String[] NUMBER = {"➊","➋","➌","➍","➎","➏","➐","➑","➒","➓"};
	// 8
	public static String[] BLANK = {"▁","▂","▃","▄","▅","▆","▇","█"};

	public static String text(String text) {
		return ChatColor.GRAY + text;
	}

	public static String RIGHT_ARROW = "➲";

	public static String title(Object text) {
		String format = "&7[&e&l"+text+"&7] &7";
		return ChatColor.translateAlternateColorCodes('&', format);
	}

	public static String special(Object text) {
		String format = "&a"+text+"&7";
		return ChatColor.translateAlternateColorCodes('&', format);
	}

}
