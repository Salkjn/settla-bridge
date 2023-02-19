package de.settla.local.keys;

import java.util.Set;

import de.settla.local.basic.BasicModule;
import de.settla.local.basic.YamlDatabase;

public class KeyDatabase extends YamlDatabase {
	
	public KeyDatabase(BasicModule module, String name, String path) {
		super(module, name, path);
	}

	public void addKey(Key key) {
		getConfig().set(key.getId(), key);
		saveToFile(true);
	}
	
	public void removeKey(String id) {
		getConfig().set(id, null);
		saveToFile(true);
	}
	
	public Key getKey(String id) {
		return (Key) getConfig().get(id);
	}
	
	public boolean hasKey(String id) {
		return getConfig().contains(id);
	}
	
	public int size() {
		return getKeys().size();
	}
	
	public Set<String> getKeys() {
		return getConfig().getKeys(false);
	}
	
}
