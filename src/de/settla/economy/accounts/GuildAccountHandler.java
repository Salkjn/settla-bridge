package de.settla.economy.accounts;

import java.util.Map;
import java.util.UUID;

import de.settla.economy.AccountHandler;
import de.settla.utilities.storage.Serial;

@Serial("GuildAccountHandler")
public class GuildAccountHandler extends AccountHandler<UUID, GuildAccount> {

	public GuildAccountHandler(Map<String, Object> map) {
		super(map);
	}

	public GuildAccountHandler(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
}