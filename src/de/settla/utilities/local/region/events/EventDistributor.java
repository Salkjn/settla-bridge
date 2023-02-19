package de.settla.utilities.local.region.events;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import de.settla.utilities.local.region.form.Vector;

public interface EventDistributor {
	
	/**
	 * Sends the given Event to all worlds.
	 * When the position is null only the wilnessregion will fire.
	 * 
	 * @param event Event to fire.
	 * @param position Position of the event or null.
	 * @param world World of the event.
	 */
	void fire(Event event, @Nullable Vector position, String world);
	
}
