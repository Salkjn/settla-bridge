package de.settla.utilities.local.region.form;

import java.util.Map;

import de.settla.utilities.local.region.Rotatable;

public class BlockCuboid extends Cuboid implements Rotatable {
	
	public BlockCuboid(Map<String, Object> map) {
		super(map);
	}
	
	public BlockCuboid(Vector v1, Vector v2) {
		super(v1.floor(), v2.floor());
	}
	
	@Override
	public String type() {
		return "BLOCK_CUBOID";
	}

	@Override
	public boolean overlaps(Vector vector) {
		return super.overlaps(vector.floor());
	}

	@Override
	public boolean intersect(Form form) {
		return true;
	}

	@Override
	public BlockCuboid move(Vector vector) {
		return new BlockCuboid(minimum().add(vector), maximum().add(vector));
	}

	@Override
	public BlockCuboid rotate90(Vector origin) {
		return super.rotate90(origin.floor());
	}
	
}
