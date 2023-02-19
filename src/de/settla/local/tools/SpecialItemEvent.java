package de.settla.local.tools;

import org.bukkit.event.Listener;

public class SpecialItemEvent implements Listener {
	
	private final SpecialItem<?> specialItem;

	public SpecialItemEvent(SpecialItem<?> specialItem) {
		this.specialItem = specialItem;
	}

	public SpecialItem<?> getSpecialItem() {
		return specialItem;
	}

}
