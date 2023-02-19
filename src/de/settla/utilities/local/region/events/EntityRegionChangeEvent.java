package de.settla.utilities.local.region.events;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import de.settla.utilities.local.region.Region;

public class EntityRegionChangeEvent extends RegionChangeEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	
	private final Cancellable cancellable;
	private final Entity entity;
	
	public EntityRegionChangeEvent(Cancellable cancellable, Entity entity, List<Region> from, List<Region> to) {
		super(from, to);
		checkNotNull(entity);
		checkNotNull(cancellable);
		this.cancellable = cancellable;
		this.entity = entity;
	}

	public static HandlerList getHandlerList() {
        return handlers;
    }
	
	public Entity getEntity() {
		return entity;
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
