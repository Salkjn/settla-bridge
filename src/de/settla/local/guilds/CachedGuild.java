package de.settla.local.guilds;

import de.settla.global.guilds.Guild;
import de.settla.utilities.CachedElement;

public class CachedGuild extends CachedElement<Guild> {

	public CachedGuild() {
		super(1000L * 10L);
	}
	
	public Guild getGuild() {
		return get();
	}
}
