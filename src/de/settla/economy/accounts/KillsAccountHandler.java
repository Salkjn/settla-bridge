package de.settla.economy.accounts;

import java.util.Map;
import java.util.UUID;

import de.settla.economy.AccountHandler;
import de.settla.utilities.storage.Serial;

@Serial("KillsHandler")
public class KillsAccountHandler extends AccountHandler<UUID, KillsAccount> {

	public KillsAccountHandler(Map<String, Object> map) {
		super(map);
	}

	public KillsAccountHandler(String name) {
		super(name);
	}

}