package de.settla.utilities.module;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.settla.utilities.storage.StorageException;

public class Module<M extends ModuleManager> {
	
	private final M moduleManager;
	
	public Module(M moduleManager) {
		this.moduleManager = moduleManager;
	}

	public void onPreEnable() {}
	
	public void onEnable() {}
	
	public void onDisable() {}
	
	public String getName() {
		return this.getClass().getSimpleName();
	}	
	
	public File getDataFolder() {
		return new File(moduleManager.getDataFolder(), "/" + getName());
	}
	
	public M getModuleManager() {
		return moduleManager;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> loadJson(String path) {
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

	public void saveJson(Map<String, Object> map, String path, boolean pritty) throws StorageException {
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
	
}
