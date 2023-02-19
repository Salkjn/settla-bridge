package de.settla.economy.accounts;

import java.util.Map;

import de.settla.economy.AccountHandler;
import de.settla.utilities.storage.Serial;

@Serial("AdminShopHandler")
public class AdminShopHandler extends AccountHandler<Integer, AdminShop> {

	public AdminShopHandler(Map<String, Object> map) {
		super(map);
		// TODO Auto-generated constructor stub
	}

	public AdminShopHandler(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
}
