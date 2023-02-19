package de.settla.utilities.local.region.space;

import java.util.Map;

import de.settla.utilities.local.region.Rotatable;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("room")
public class Room implements Storable, Rotatable {

	private Vector e1, e2, e3;
	
	private final int max_e1, max_e2, max_e3;
	
	public Room(int max_x, int max_y, int max_z) {
		this.e1 = new Vector(1, 0, 0);
		this.e2 = new Vector(0, 1, 0);
		this.e3 = new Vector(0, 0, 1);
		this.max_e1 = max_x;
		this.max_e2 = max_y;
		this.max_e3 = max_z;
	}
	
	public Room(Room room) {
		this.e1 = room.e1;
		this.e2 = room.e2;
		this.e3 = room.e3;
		this.max_e1 = room.max_e1;
		this.max_e2 = room.max_e2;
		this.max_e3 = room.max_e3;
	}
	
	public Room(Map<String, Object> map) {
		this.e1 = this.deserialize(map.get("e1"), Vector.class);
		this.e2 = this.deserialize(map.get("e2"), Vector.class);
		this.e3 = this.deserialize(map.get("e3"), Vector.class);
		this.max_e1 = ((Double)map.get("me1")).intValue();
		this.max_e2 = ((Double)map.get("me2")).intValue();
		this.max_e3 = ((Double)map.get("me3")).intValue();
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("e1", e1.serialize());
		map.put("e2", e2.serialize());
		map.put("e3", e3.serialize());
		map.put("me1", max_e1);
		map.put("me2", max_e2);
		map.put("me3", max_e3);
		return map;
	}
	
	@Override
	public Room rotate90(Vector origin) {
		this.e1(e1().rotate90());
		this.e3(e3().rotate90());
		return this;
	}
	
	public Vector e1() {
		return e1;
	}
	public void e1(Vector e1) {
		this.e1 = e1;
	}
	public Vector e2() {
		return e2;
	}
	public void e2(Vector e2) {
		this.e2 = e2;
	}
	public Vector e3() {
		return e3;
	}
	public void e3(Vector e3) {
		this.e3 = e3;
	}
	public int getMaxE1() {
		return max_e1;
	}
	public int getMaxE2() {
		return max_e2;
	}
	public int getMaxE3() {
		return max_e3;
	}
	public int volume() {
		return max_e1*max_e2*max_e3;
	}
}
