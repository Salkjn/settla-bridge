package de.settla.economy.accounts;

import java.util.Map;
import java.util.UUID;

import de.settla.economy.AccountHandler;
import de.settla.utilities.storage.Serial;

@Serial("HeadHunterAccountHandler")
public class HeadHunterAccountHandler extends AccountHandler<UUID, HeadHunterAccount> {

	public HeadHunterAccountHandler(Map<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
	}

	public HeadHunterAccountHandler(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
}