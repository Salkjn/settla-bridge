package de.settla.global.guilds.groups;

import java.util.Map;
import java.util.UUID;

import de.settla.utilities.storage.Serial;

@Serial("PlayerGroup")
public class PlayerGroup extends Group<UUID> {
	
	public PlayerGroup() {
		super();
	}

	public PlayerGroup(Map<String, Object> map) {
		super(map);
	}
	
	@Override
	protected String a(UUID a) {
		return a.toString();
	}

	@Override
	protected UUID b(String str) {
		return UUID.fromString(str);
	}

}
