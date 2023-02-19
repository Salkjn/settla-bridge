package de.settla.utilities.local.region.events;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.settla.utilities.local.Utils;
import de.settla.utilities.local.region.Region;

public class RegionChangeEvent extends Event implements WildnessEvent {

	private static final HandlerList handlers = new HandlerList();
	
	private final List<Region> from, to;
	
	public RegionChangeEvent(List<Region> from, List<Region> to) {
		super();
		checkNotNull(from);
		checkNotNull(to);
		this.from = from;
		this.to = to;
	}
	
    public List<Region> getFrom() {
		return from;
	}

	public List<Region> getTo() {
		return to;
	}
	
	private <T extends Region> List<T> get(List<Region> regions, Class<? extends T> clazz) {
		return Utils.filter(regions, clazz);
	}
	
    public <T extends Region> List<T> getFrom(Class<? extends T> clazz) {
		return get(from, clazz);
	}

	public <T extends Region> List<T> getTo(Class<? extends T> clazz) {
		return get(to, clazz);
	}

	public static HandlerList getHandlerList() {
        return handlers;
    }

	public HandlerList getHandlers() {
        return handlers;
    }
	
}
