package de.settla.utilities.global;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class TextBuilder {

	private final ComponentBuilder builder = new ComponentBuilder("");
	
	public TextBuilder title(Object text) {
		builder.append("[").color(ChatColor.DARK_GRAY).append(text.toString()).color(ChatColor.DARK_GREEN).bold(true).append("] ").color(ChatColor.DARK_GRAY).bold(false);
		return this;
	}
	
	public TextBuilder text(Object text) {
		builder.append(text.toString()).color(ChatColor.GRAY);
		return this;
	}
	
	public TextBuilder spezial(Object text) {
		builder.append(text.toString()).color(ChatColor.GREEN);
		return this;
	}
	
	public BaseComponent[] build() {
		return builder.create();
	}
	
	public ComponentBuilder getComponentBuilder() {
		return builder;
	}
	
	public void send(CommandSender sender) {
		sender.sendMessage(build());
	}
	
}
