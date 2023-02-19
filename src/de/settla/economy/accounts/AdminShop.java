package de.settla.economy.accounts;

import java.util.Map;

import de.settla.economy.Account;
import de.settla.economy.UnlimitedAccount;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;

@Serial("AdminShop")
public class AdminShop extends Account<Integer> implements UnlimitedAccount {

	public AdminShop(Integer id, long minimumBalance, long maximumBalance) {
		super(id, minimumBalance, maximumBalance);
		// TODO Auto-generated constructor stub
	}

	public AdminShop(Map<String, Object> map) {
		super(StaticParser.parse((String)map.get("id"), Integer.class), map);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("id", StaticParser.unparse(id(), Integer.class));
		return map;
	}

	@Override
	public String getName() {
		return "AdminShop";
	}
	
}
