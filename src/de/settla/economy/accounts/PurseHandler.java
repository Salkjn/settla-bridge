package de.settla.economy.accounts;

import java.util.Map;
import java.util.UUID;

import de.settla.economy.AccountHandler;
import de.settla.utilities.storage.Serial;

@Serial("PurseHandler")
public class PurseHandler extends AccountHandler<UUID, Purse> {

	public PurseHandler(Map<String, Object> map) {
		super(map);
	}

	public PurseHandler(String name) {
		super(name);
	}
	
}
