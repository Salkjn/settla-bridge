package de.settla.local.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("NpcDatas")
public class NpcDatas implements Storable {

	private List<NpcData> npcs = new ArrayList<>();
	private final Object lock = new Object();
	
	private boolean dirty;
	
	public NpcDatas() {
		
	}
	
	public NpcDatas(List<NpcData> points) {
		this.npcs = points;
	}
	
	@SuppressWarnings("unchecked")
	public NpcDatas(Map<String, Object> map) {
		((List<Map<String, Object>>)map.get("npcs")).stream().forEach(s -> npcs.add(deserialize(s, NpcData.class)));
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("npcs", npcs.stream().map(g -> g.serialize()).collect(Collectors.toList()));
			return map;
		}	
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public NpcData getNpcData(String name) {
		synchronized (lock) {
			return npcs.stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		}
	}
	
	public boolean addNpcData(NpcData data) {
		synchronized (lock) {
			setDirty(true);
			if(getNpcData(data.getName()) == null) {
				return npcs.add(data);
			} else {
				return false;
			}
		}
	}
	
	public boolean removeNpcData(NpcData data) {
		synchronized (lock) {
			setDirty(true);
			return npcs.remove(data);
		}
	}

	public void forEach(Consumer<NpcData> consumer) {
		synchronized (lock) {
			npcs.forEach(consumer);
		}
	}
	
}
