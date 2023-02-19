package de.settla.utilities.local.region.events;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import de.settla.utilities.local.region.Region;

public class PlayerRegionChangeEvent extends RegionChangeEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	
	private final Cancellable cancellable;
	private final Player player;
	
	public PlayerRegionChangeEvent(Cancellable cancellable, Player player, List<Region> from, List<Region> to) {
		super(from, to);
		checkNotNull(player);
		checkNotNull(cancellable);
		this.cancellable = cancellable;
		this.player = player;
	}

	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public boolean isCancelled() {
		return cancellable.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancellable.setCancelled(cancel);
	}
}
