package de.settla.utilities.local.region.events;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import de.settla.utilities.local.region.Region;

public class BlockRegionChangeEvent extends RegionChangeEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	
	private final Cancellable cancellable;
	private final Case type;
	
	public BlockRegionChangeEvent(Cancellable cancellable, List<Region> from, List<Region> to, Case type) {
		super(from, to);
		checkNotNull(cancellable);
		checkNotNull(type);
		this.cancellable = cancellable;
		this.type = type;
	}

	public static HandlerList getHandlerList() {
        return handlers;
    }

	@Override
	public boolean isCancelled() {
		return cancellable.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancellable.setCancelled(cancel);
	}
	
	public Case getCase() {
		return type;
	}

	public static enum Case {
		
		EXLOPSION, FROMTO, GROW, DISPENSER, PISTON, SPREAD;
		
	}
	
}
