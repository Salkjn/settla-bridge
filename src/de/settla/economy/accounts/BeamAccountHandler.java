package de.settla.economy.accounts;

import java.util.Map;
import java.util.UUID;

import de.settla.economy.AccountHandler;
import de.settla.utilities.storage.Serial;

@Serial("BeamAccountHandler")
public class BeamAccountHandler extends AccountHandler<UUID, BeamAccount> {

	public BeamAccountHandler(Map<String, Object> map) {
		super(map);
	}

	public BeamAccountHandler(String name) {
		super(name);
	}
	
}