package de.settla.utilities;

import java.util.Map;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable;

@Serial("TimeValue")
public class TimeValue implements Storable {

	private boolean dirty;
	private long time;
	
	public TimeValue() {
		time = 0;
	}
	
	public TimeValue(Map<String, Object> map) {
		time = StaticParser.parse((String)map.get("time"), Long.class);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("time", StaticParser.unparse(time, Long.class));
		return map;
	}
	
	public boolean isActive() {
		return time >= System.currentTimeMillis();
	}
	
	public void setTime(long time) {
		setDirty(true);
		this.time = System.currentTimeMillis() + time;
	}
	
	public void reset() {
		setDirty(true);
		this.time = 0;
	}
	
	public long getTime() {
		return time;
	}
	
	public long getLeftTime() {
		return time - System.currentTimeMillis();
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
