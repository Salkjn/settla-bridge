package de.settla.global.guilds;

import java.util.Map;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("GuildName")
public class GuildName implements Storable {

	private boolean dirty;

	private String longName, shortName;
	
	public GuildName(String longName, String shortName) {
		this.longName = longName;
		this.shortName = shortName;
	}
	
	public GuildName(Map<String, Object> map) {
		this.longName = (String)map.get("l");
		this.shortName = (String)map.get("s");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("l", longName);
		map.put("s", shortName);
		return map;
	}
	
	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return dirty;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		setDirty(true);
		this.longName = longName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		setDirty(true);
		this.shortName = shortName;
	}
	
	public boolean isShortName(String name) {
		return shortName != null ? shortName.equalsIgnoreCase(name) : false;
	}
	
	public boolean isLongName(String name) {
		return longName != null ? longName.equalsIgnoreCase(name) : false;
	}

}
