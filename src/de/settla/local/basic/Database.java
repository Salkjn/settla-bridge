package de.settla.local.basic;

import java.io.File;

import de.settla.utilities.ChangeTracked;

public interface Database extends ChangeTracked {

	public String getName();

	public BasicModule getModule();

	public String getPath();

	public void saveToFile(boolean now);

	public void loadFromFile();
	
	public default File getFile() {
		return new File(getModule().getDataFolder(), getPath());
	}
	
}
