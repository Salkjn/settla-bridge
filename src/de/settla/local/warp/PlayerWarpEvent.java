package de.settla.local.warp;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerWarpEvent extends Event implements Cancellable {

	private static HandlerList HL = new HandlerList();
	private boolean isCancelled;
	private final Player player;

	public PlayerWarpEvent(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		this.isCancelled = b;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return HL;
	}
}
