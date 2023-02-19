package de.settla.global.kits;

import java.util.HashMap;
import java.util.Map;

import de.settla.utilities.global.playerdata.GlobalData;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;

@Serial("GlobalKitData")
public class GlobalKitData extends GlobalData {
	
	private final Map<KitType, Long> usages = new HashMap<>();
	private boolean dirty;

	public GlobalKitData() {
		super();
	}

	public GlobalKitData(Map<String, Object> map) {
		super(map);
		for (KitType type : KitType.values())
			usages.put(type, StaticParser.parse((String)map.get(type.getName()), Long.class));
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		usages.entrySet().stream().filter(e -> e.getValue() != null && e.getKey() != null).forEach(e -> map.put(e.getKey().getName(), StaticParser.unparse(e.getValue(), Long.class)));
		return map;
	}

	public void setTimeOfKit(KitType kit, long time) {
		setDirty(true);
		usages.put(kit, time);
	}
	
	public long getTimeOfKit(KitType kit) {
		Long time = usages.get(kit);
		return time == null ? 0 : time;
	}
	
	public boolean hasValidTimeOfKit(KitType kit) {
		return usages.get(kit) != null;
	}
	
	public void useKit(KitMeta kit) {
		setTimeOfKit(kit.getType(), System.currentTimeMillis() + kit.getDifTime());
	}

	public boolean canUseKit(KitType kit) {
		Long time = usages.get(kit);
		if (time == null)
			return true;
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
		return dirty;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
