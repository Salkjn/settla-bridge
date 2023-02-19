package de.settla.utilities.local.region.events;

import org.bukkit.event.Cancellable;

public class Cancel implements Cancellable {

	private boolean cancelled;
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
