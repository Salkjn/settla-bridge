package de.settla.utilities.local.region.events;

import static com.google.common.base.Preconditions.checkNotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private final Player player;
	
	public PlayerEvent(Player player) {
		super();
		checkNotNull(player);
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}

	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
	
}
