package de.settla.local.kits;

import java.util.HashMap;
import java.util.Map;

import de.settla.global.kits.KitMeta;
import de.settla.global.kits.KitType;
import de.settla.utilities.local.playerdata.LocalData;
import de.settla.utilities.storage.Serial;

@Serial("LocalKitData")
public class LocalKitData extends LocalData {
	
	private final Map<KitType, Long> usages = new HashMap<>();

	public LocalKitData() {
		super();
	}

	public LocalKitData(Map<String, Object> map) {
		super(map);
	}

	@Override
	public Map<String, Object> serialize() {
		return super.serialize();
	}

	public void setTimeOfKit(KitType kit, long time) {
		usages.put(kit, time);
	}
	
	public long getTimeOfKit(KitType kit) {
		return usages.get(kit);
	}
	
	public boolean hasValidTimeOfKit(KitType kit) {
		return usages.get(kit) != null;
	}
	
	public void useKit(KitMeta kit) {
		setDirty(true);
		usages.put(kit.getType(), System.currentTimeMillis() + kit.getDifTime());
	}

	public boolean canUseKit(KitType kit) {
		Long time = usages.get(kit);
		if (time == null)
			return false;
		return time <= System.currentTimeMillis();
	}
	
	public long getDifTime(KitType kit) {
		Long time = usages.get(kit);
		if (time == null)
			return 0;
		return (long) (time - System.currentTimeMillis());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void setDirty(boolean dirty) {
		
	}

}
