package de.settla.economy.accounts;

import java.util.Map;

import de.settla.economy.AccountHandler;
import de.settla.utilities.storage.Serial;

@Serial("ServerAccountHandler")
public class ServerAccountHandler extends AccountHandler<String, ServerAccount> {

	public ServerAccountHandler(Map<String, Object> map) {
		super(map);
	}

	public ServerAccountHandler(String name) {
		super(name);
	}
	
}