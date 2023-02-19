package de.settla.economy.accounts;

import java.util.Map;
import java.util.UUID;

import de.settla.economy.Account;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;

@Serial("GuildAccount")
public class GuildAccount extends Account<UUID> {

	public GuildAccount(UUID id, long minimumBalance, long maximumBalance) {
		super(id, minimumBalance, maximumBalance);
	}

	public GuildAccount(Map<String, Object> map) {
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
		return "GuildAccount";
	}
	
}