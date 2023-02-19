package de.settla.utilities.local.region.form;

import java.util.Map;

import de.settla.utilities.storage.Storable.Memory;

public class Cuboid extends Form {

	private final Vector minimum, maximum;
	
	public Cuboid(Map<String, Object> map) {
		minimum = Memory.deserialize(map.get("min"), Vector.class);
		maximum =  Memory.deserialize(map.get("max"), Vector.class);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("min", minimum.serialize());
		map.put("max", maximum.serialize());
		return map;
	}
	
	public Cuboid(Vector v1, Vector v2) {
		minimum = Vector.getMinimum(v1, v2);
		maximum = Vector.getMaximum(v1, v2);
	}
	
	@Override
	public String type() {
		return "CUBOID";
	}

	@Override
	public Vector minimum() {
		return minimum;
	}

	@Override
	public Vector maximum() {
		return maximum;
	}

	@Override
	public boolean overlaps(Vector vector) {
		return minimum.getX() <= vector.getX() && vector.getX() <= maximum.getX()
				&& minimum.getY() <= vector.getY() && vector.getY() <= maximum.getY()
				&& minimum.getZ() <= vector.getZ() && vector.getZ() <= maximum.getZ();
	}

	@Override
	public boolean intersect(Form form) {
		return true;
	}

	@Override
	public Form move(Vector vector) {
		return new Cuboid(maximum().add(vector), minimum().add(vector));
	}
	
	@Override
	public BlockCuboid rotate90(Vector origin) {
		return new BlockCuboid(minimum().subtract(origin).rotate90().add(origin), maximum().subtract(origin).rotate90().add(origin));
	}

}
