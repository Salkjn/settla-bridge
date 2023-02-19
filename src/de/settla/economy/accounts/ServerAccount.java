package de.settla.economy.accounts;

import java.util.Map;

import de.settla.economy.Account;
import de.settla.economy.UnlimitedAccount;
import de.settla.utilities.storage.Serial;

@Serial("ServerAccount")
public class ServerAccount extends Account<String> implements UnlimitedAccount {

	public ServerAccount(String id, long minimumBalance, long maximumBalance) {
		super(id, minimumBalance, maximumBalance);
		// TODO Auto-generated constructor stub
	}

	public ServerAccount(Map<String, Object> map) {
		super((String)map.get("id"), map);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("id", id());
		return map;
	}
	
	@Override
	public String getName() {
		return "ServerAccount";
	}
	
}
