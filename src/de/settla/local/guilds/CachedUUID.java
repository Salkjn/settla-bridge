package de.settla.local.guilds;

import java.util.UUID;

import de.settla.utilities.CachedElement;

public class CachedUUID extends CachedElement<UUID> {

	public CachedUUID() {
		super(1000L * 10L);
	}

	public UUID getUniqueId() {
		return get();
	}
	
}
