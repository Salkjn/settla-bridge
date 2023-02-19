package de.settla.local.portals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.settla.utilities.local.region.Region;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.storage.Serial;

@Serial("PortalRegion")
public class PortalRegion extends Region {

	private String warp;
	
	private final Map<UUID, Long> cachedPlayers = new HashMap<>();
	
	
	public PortalRegion(Form form, String name) {
		super(form, 1, false, name);
	}
	
	public PortalRegion(Map<String, Object> map) {
		super(map);
		this.warp = (String) map.get("warp");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("warp", this.warp);
		return map;
	}
	
	public String getWarp() {
		return warp;
	}

	public void setWarp(String warp) {
		this.warp = warp;
		setDirty(true);
	}
	
	public boolean isCachedPlayer(UUID uuid) {
		Long time = cachedPlayers.get(uuid);
		if (time == null) {
			return false;
		} else {
			return time >= System.currentTimeMillis(); 
		}
	}
	
	public void setCachedPlayer(UUID uuid) {
		cachedPlayers.put(uuid, System.currentTimeMillis() + 1000 * 3);
	}
}
