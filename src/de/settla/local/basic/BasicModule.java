package de.settla.local.basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.settla.local.LocalPlugin;
import de.settla.utilities.module.Module;
import de.settla.utilities.storage.StorageException;

public abstract class BasicModule extends Module<LocalPlugin> {

	public BasicModule(LocalPlugin moduleManager) {
		super(moduleManager);
	}

	@Override
	final public void onEnable() {
		enableModule();
	}
	
	@Override
	final public void onDisable() {
		disableModule();
	}
	
	public abstract void enable();
	public abstract void disable();
	
	public static final String CONFIG_PATH = "config.yml";

	private boolean enabled;
	private final Map<String, Database> databases = new HashMap<>();
	private YamlConfiguration config;

	final public YamlConfiguration getConfig() {
		return config != null ? config : (config = loadConfig(CONFIG_PATH));
	}

	final public YamlConfiguration reloadConfig() {
		config = null;
		return getConfig();
	}

	final public void saveConfig() {
		try {
			saveConfig(getConfig(), CONFIG_PATH);
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}

	final public void enableModule() {
		this.enabled = true;
		enable();
	}

	final public void disableModule() {
		this.enabled = false;
		disable();
		saveDatabases();
	}

	final public void sendMessage(String message) {
		getModuleManager().getServer().getConsoleSender()
				.sendMessage(ChatColor.RED + "[" + getName() + "] " + ChatColor.DARK_RED + message);
	}

	final public boolean isEnabled() {
		return enabled;
	}

	final public String getName() {
		return this.getClass().getSimpleName();
	}

	final public File getDataFolder() {
		return new File(getModuleManager().getDataFolder(), File.separator + getName());
	}

	final public YamlConfiguration loadConfig(String path) {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		return YamlConfiguration.loadConfiguration(new File(getDataFolder(), path));
	}

	public void saveConfig(YamlConfiguration config, String path) throws StorageException {

		File file = new File(getDataFolder(), path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");

		if (!tempFile.exists() && tempFile.getParentFile() != null)
			tempFile.getParentFile().mkdirs();

		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		try {
			config.save(tempFile);
			file.delete();
			if (!tempFile.renameTo(file)) {
				throw new StorageException("Failed to rename temporary file to " + file.getAbsolutePath());
			}
		} catch (IOException e) {
		}
	}

	@SuppressWarnings("unchecked")
	final public Map<String, Object> loadJSON(String path) {
		File file = new File(getDataFolder(), path);
		if (!file.exists()) {
			return null;
		}
		Map<String, Object> json = null;

		try {
			Gson gson = new Gson();
			FileReader fileReader = new FileReader(file);
			json = gson.fromJson(fileReader, Map.class);
			fileReader.close();
		} catch (IOException e) {
		}
		return json;
	}

	final public void saveJSON(Map<String, Object> map, String path, boolean pritty) throws StorageException {
		File file = new File(getDataFolder(), path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");

		if (!tempFile.exists() && tempFile.getParentFile() != null)
			tempFile.getParentFile().mkdirs();

		GsonBuilder gsonBuilder = new GsonBuilder();
		if (pritty)
			gsonBuilder.setPrettyPrinting();
		String json = gsonBuilder.create().toJson(map);

		try {
			FileWriter fw = new FileWriter(tempFile);
			fw.write(json);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		file.delete();
		if (!tempFile.renameTo(file)) {
			throw new StorageException("Failed to rename temporary file to " + file.getAbsolutePath());
		}
	}

	public YamlConfiguration loadResourceConfigFile(String resourcePath, String path) {

		YamlConfiguration config = null;

		File dataFolder = getDataFolder();

		File file = new File(dataFolder, path);
		if (file.exists()) {
			config = YamlConfiguration.loadConfiguration(file);
		} else {
			file.getParentFile().mkdir();

			if (getModuleManager().getResource(resourcePath) != null) {

				if (resourcePath == null || resourcePath.equals("")) {
					throw new IllegalArgumentException("ResourcePath cannot be null or empty");
				}
				if (path == null || path.equals("")) {
					throw new IllegalArgumentException("Path cannot be null or empty");
				}

				resourcePath = resourcePath.replace('\\', '/');
				path = path.replace('\\', '/');
				InputStream in = getModuleManager().getResource(resourcePath);
				if (in == null) {
					throw new IllegalArgumentException(
							"The embedded resource '" + resourcePath + "' cannot be found in " + file);
				}

				File outFile = new File(dataFolder, path);
				int lastIndex = path.lastIndexOf('/');
				File outDir = new File(dataFolder, path.substring(0, lastIndex >= 0 ? lastIndex : 0));

				if (!outDir.exists()) {
					outDir.mkdirs();
				}

				try {
					OutputStream out = new FileOutputStream(outFile);
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.close();
					in.close();
				} catch (IOException ex) {
					getModuleManager().getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile,
							ex);
				}

				config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), path));

			} else {
				getModuleManager().getLogger().severe("Could not find any locale file!");
			}
		}
		return config;
	}

	public void copyResource(String pathFrom, String pathTo) {
		File destination = new File(pathTo);
		if (!destination.exists())
			copyResource(pathFrom, destination);
	}

	public void copyResource(String pathFrom, File destination) {
		String location = "resources/" + pathFrom;
		// Util.debug("Location:" + location + ";");
		InputStream input = getModuleManager().getResource(location);
		// Util.debug("Input:" + input);
		if (input != null) {
			FileOutputStream fos = null;
			try {
				File parent = destination.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				fos = new FileOutputStream(destination);
				IOUtils.copy(input, fos);
			} catch (Exception e) {
				try {
					input.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} else {
//			Notification.warn(Type.CONSOLE, "InputStream == null -> true");
		}
	}

	final public Map<String, Object> loadResourceJSONFile(String resourcePath, String path) {

		Map<String, Object> json = null;

		File dataFolder = getDataFolder();

		File file = new File(dataFolder, path);
		if (file.exists()) {
			json = loadJSON(path);
		} else {
			file.getParentFile().mkdir();

			if (getModuleManager().getResource(resourcePath) != null) {

				if (resourcePath == null || resourcePath.equals("")) {
					throw new IllegalArgumentException("ResourcePath cannot be null or empty");
				}
				if (path == null || path.equals("")) {
					throw new IllegalArgumentException("Path cannot be null or empty");
				}

				resourcePath = resourcePath.replace('\\', '/');
				path = path.replace('\\', '/');
				InputStream in = getModuleManager().getResource(resourcePath);
				if (in == null) {
					throw new IllegalArgumentException(
							"The embedded resource '" + resourcePath + "' cannot be found in " + file);
				}

				File outFile = new File(dataFolder, path);
				int lastIndex = path.lastIndexOf('/');
				File outDir = new File(dataFolder, path.substring(0, lastIndex >= 0 ? lastIndex : 0));

				if (!outDir.exists()) {
					outDir.mkdirs();
				}

				try {
					OutputStream out = new FileOutputStream(outFile);
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.close();
					in.close();
				} catch (IOException ex) {
					getModuleManager().getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile,
							ex);
				}

				json = loadJSON(path);

			} else {
				getModuleManager().getLogger().severe("Could not find any locale file!");
			}
		}
		return json;
	}

	final public boolean registerDatabase(Database database) {
		if (databases.containsKey(database.getName()))
			return false;
		databases.put(database.getName(), database);
		return true;
	}

	final public boolean unregisterDatabase(String name, boolean save) {
		if (!databases.containsKey(name))
			return false;
		if (save == true) {
			Database database = databases.get(name);
			if (database != null)
				database.saveToFile(false);
		}
		databases.remove(name);
		return true;
	}

	final public void saveDatabases() {
		for (Database database : databases.values()) {
			if (!database.isDirty())
				database.saveToFile(false);
		}
	}

	final public Database getDatabase(String name) {
		return databases.get(name);
	}

	public final <T extends Database> T getDatabase(String name, Class<T> clazz) {
		Database db = getDatabase(name);
		if (clazz.isInstance(db)) {
			return clazz.cast(db);
		}
		return null;
	}

	final public void registerListener(Listener listener) {
		getModuleManager().registerListener(listener);
	}
	
}
