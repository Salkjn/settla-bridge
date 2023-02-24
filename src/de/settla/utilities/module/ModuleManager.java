package de.settla.utilities.module;

import java.io.File;
import java.util.function.Consumer;

public interface ModuleManager {
	
	File getDataFolder();
	
	<M extends Module<?>> M getModule(Class<M> clazz);
	
	void modules(Consumer<Module<?>> consumer);
	
	void disable();
	
	void enable();
	
}
