package de.settla.utilities.storage;

import java.util.Map;
import java.util.UUID;

@Serial("unique")
public class UniqueIdBlock implements Storable {
	
	private final UUID uniqueId;
	
	public UniqueIdBlock(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	public UniqueIdBlock(Map<String, Object> map) {
		Object o = map.get("id");
		if(o != null)
			this.uniqueId = UUID.fromString((String) o);
		else
			this.uniqueId = null;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("id", uniqueId == null ? null : uniqueId.toString());
		return map;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}
	
}
