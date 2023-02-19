package de.settla.local.tools;

import org.bukkit.entity.Player;

public class SpecialItemUser {
	
	private final Player player;
	private long lastusage;

	public SpecialItemUser(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}
	
	public void updateLastUsage() {
		this.lastusage = System.currentTimeMillis();
	}
	
	public long getLastUsage() {
		return this.lastusage;
	}
	
}
