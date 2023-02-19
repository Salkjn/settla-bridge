package de.settla.utilities.local.region.form;

import java.util.Map;

public class ChunkForm extends Form implements Centerable {

	private final int x, z;
	private Vector min, max, center;
	
	public ChunkForm(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public ChunkForm(Map<String, Object> map) {
		this.x = ((Double) map.get("x")).intValue();
		this.z = ((Double) map.get("z")).intValue();
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		map.put("x", x);
		map.put("z", z);
		return map;
	}
	
	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	@Override
	public String type() {
		return "CHUNK";
	}

	@Override
	public Vector minimum() {
		return min != null ? min : (min = new Vector(x << 4, 0, z << 4));
	}

	@Override
	public Vector maximum() {
		return max != null ? max : (max = minimum().add((1 << 4) , 265, (1 << 4) ));
	}

	@Override
	public boolean overlaps(Vector vector) {
		return x == (int)Math.floor(vector.getX()) >> 4 && z == (int)Math.floor(vector.getZ()) >> 4;
	}

	@Override
	public boolean intersect(Form form) {
		if(form instanceof ChunkForm) {
			ChunkForm chunkForm = (ChunkForm) form;
			return chunkForm.x == x && chunkForm.z == z;
		}
		return true;
	}

	@Override
	public ChunkForm move(Vector vector) {
		int x = (int)Math.floor(vector.getX()) >> 4;
		int z = (int)Math.floor(vector.getZ()) >> 4;
		return new ChunkForm(this.x + x, this.z + z);
	}

	@Override
	public Vector center() {
		return center != null ? center : (center = Vector.getMidpoint(minimum(), maximum()));
	}

	@Override
	public ChunkForm rotate90(Vector origin) {
		return new ChunkForm(x, z);
	}
	
}
