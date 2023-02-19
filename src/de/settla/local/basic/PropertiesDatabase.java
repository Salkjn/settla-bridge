package de.settla.local.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import de.settla.utilities.storage.StorageException;

public class PropertiesDatabase implements Database {

	private final String name;
	private final String path;
	private final BasicModule module;
	private final File file;

	private boolean dirty = true;
	private final Object lock = new Object();
	private Properties props = new Properties();

	public PropertiesDatabase(BasicModule module, String name, String path) {
		this.module = module;
		this.name = name;
		this.path = path;
		this.file = getFile();
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

	@Override
	final public void saveToFile(boolean now) {
		synchronized (lock) {
			if (now) {
				try {
					
					File file = this.file;
					if (!file.exists()) {
						file.getParentFile().mkdirs();
					}
					File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");

					if (!tempFile.exists() && tempFile.getParentFile() != null)
						tempFile.getParentFile().mkdirs();
					
					FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
					props.store(fileOutputStream, null);
					fileOutputStream.close();
					
					file.delete();
					if (!tempFile.renameTo(file)) {
						try {
							throw new StorageException("Failed to rename temporary file to " + file.getAbsolutePath());
						} catch (StorageException e) {
							e.printStackTrace();
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				setDirty(false);
			} else {
				setDirty(true);
			}
		}
	}

	@Override
	final public void loadFromFile() {
		synchronized (lock) {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				props.load(fileInputStream);
				fileInputStream.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setDirty(false);
		}
	}

	public void put(Object key, Object value) {
		synchronized (lock) {
			props.put(key.toString(), value.toString());
			setDirty(true);
		}
	}

	public void remove(Object key) {
		synchronized (lock) {
			props.remove(key.toString());
			setDirty(true);
		}
	}

	public int size() {
		synchronized (lock) {
			return props.size();
		}
	}

	public String get(Object key) {
		synchronized (lock) {
			if (props.containsKey(key.toString())) {
				return props.getProperty(key.toString());
			}
			return null;
		}
	}

	public boolean containsKey(Object key) {
		synchronized (lock) {
			if (props.containsKey(key.toString())) {
				return true;
			}
			return false;
		}
	}

	public boolean containsValue(String value) {
		synchronized (lock) {
			if (props.containsValue(value)) {
				return true;
			}
			return false;
		}
	}

	public Set<Entry<Object, Object>> getEntrySet() {
		synchronized (lock) {
			return props.entrySet();
		}
	}

	public boolean isEmpty() {
		synchronized (lock) {
			if (props.isEmpty()) {
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean isDirty() {
		synchronized (lock) {
			return dirty;
		}
	}

	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			this.dirty = dirty;
		}
	}

}
