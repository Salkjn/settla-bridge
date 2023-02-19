package de.settla.local.basic;

import org.bukkit.configuration.file.YamlConfiguration;

import de.settla.utilities.storage.StorageException;

public class YamlDatabase implements Database {

	private final String name;
	private final String path;
	private final BasicModule module;
	private boolean dirty = true;

	private YamlConfiguration config;

	public YamlDatabase(BasicModule module, String name, String path) {
		this.module = module;
		this.name = name;
		this.path = path;
		loadFromFile();
	}

	@Override
	final public String getName() {
		return name;
	}

	@Override
	final public BasicModule getModule() {
		return module;
	}

	@Override
	final public String getPath() {
		return path;
	}

	final public YamlConfiguration getConfig() {
		return config;
	}

	public void reload() {
		loadFromFile();
	}

	@Override
	final public void saveToFile(boolean now) {
		if (now) {
			try {
				getModule().saveConfig(config, getPath());
			} catch (StorageException e) {
				e.printStackTrace();
			}
			setDirty(false);
		} else {
			setDirty(true);
		}
	}

	@Override
	final public void loadFromFile() {
		config = getModule().loadConfig(getPath());
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
