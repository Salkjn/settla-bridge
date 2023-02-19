package de.settla.economy.accounts;

import java.util.Map;
import java.util.UUID;

import de.settla.economy.Account;
import de.settla.economy.UnlimitedAccount;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;

@Serial("Kills")
public class KillsAccount extends Account<UUID> implements UnlimitedAccount {

	public KillsAccount(UUID id, long minimumBalance, long maximumBalance) {
		super(id, minimumBalance, maximumBalance);
	}

	public KillsAccount(Map<String, Object> map) {
		super(StaticParser.parse((String)map.get("id"), UUID.class), map);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("id", StaticParser.unparse(id(), UUID.class));
		return map;
	}
	
	@Override
	public String getName() {
		return "Purse";
	}
	
}
