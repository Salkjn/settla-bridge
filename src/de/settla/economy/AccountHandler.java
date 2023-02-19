package de.settla.economy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.settla.utilities.storage.Storable;

public class AccountHandler<T, A extends Account<T>> implements Storable {
	
	private final Map<T, A> accounts = new HashMap<>();
	private final Object lock = new Object();
	
	private final String name;
	
	private boolean dirty;
	
	public AccountHandler(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public AccountHandler(Map<String, Object> map) {
		this.name = (String) map.get("name");
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("accounts");
		list.stream().map(a -> deserialize(a, Account.class)).forEach(a -> accounts.put((T)a.id(), (A)a));
	}

	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("name", name);
			map.put("accounts", accounts.values().stream().map(a -> a.serialize()).collect(Collectors.toList()));
			return map;
		}
	}

	@Override
	public boolean isDirty() {
		synchronized (lock) {
			return dirty || (accounts.values().stream().filter(a -> a.isDirty()).count() > 0);
		}
	}
	
	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			this.dirty = dirty;
			accounts.values().stream().forEach(a -> a.setDirty(dirty));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void put(Account<?> account) {
		accounts.put((T)account.id(), (A)account);
	}
	
	public Map<T, A> accounts() {
		return accounts;
	}

	public Object lock() {
		return lock;
	}
	
}
