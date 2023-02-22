package de.settla.spigot.universe.form;

import de.settla.memory.MemoryName;
import de.settla.spigot.universe.Vector;

import com.google.gson.JsonObject;

/**
 * We will include both, the minimum and the maximum!
 */

@MemoryName("FormChunk")
public class ChunkForm extends Form {

	private final int size;
	private final int x, z;
	
	public ChunkForm(JsonObject json) {
		this.size = json.get("size").getAsInt();
		this.x = json.get("x").getAsInt();
		this.z = json.get("z").getAsInt();
	}

	public ChunkForm(int size, int x, int z) {
		this.size = size;
		this.x = x;
		this.z = z;
	}
	
	public ChunkForm(int size, Vector position) {
		this.size = size;
		this.x = position.getBlockX() >> size;
		this.z = position.getBlockZ() >> size;
	}

	public int size() {
		return size;
	}
	
	public int x() {
		return x;
	}
	
	public int z() {
		return z;
	}
	
	@Override
	public JsonObject serialize() {
		JsonObject json = super.serialize();
		json.addProperty("size", size());
		json.addProperty("x", x());
		json.addProperty("z", z());
		return json;
	}
	
	// @Override
	// public boolean contains(Form form) {
	// 	return overlaps(form.minimum()) && overlaps(form.maximum());
	// }

	@Override
	public Vector minimum() {
		return new Vector(x() << size(), 0, z() << size());
	}

	@Override
	public Vector maximum() {
		return new Vector(((x() + 1) << size()) - 1, 300, ((z() + 1) << size()) - 1);
	}

	@Override
	public boolean overlaps(Vector vector) {
		return vector.getBlockX() >> size() == x() && vector.getBlockZ() >> size() == z();
	}

	@Override
	public ChunkForm move(Vector vector) {
		return new ChunkForm(size(), minimum().add(vector));
	}

	@Override
    public boolean intersect(Form form) {
		Vector min = form.minimum();
		if (min.getBlockX() >> size() > x() || min.getBlockZ() >> size() > z())
			return false;
		Vector max = form.maximum();
		if (max.getBlockX() >> size() < x() || max.getBlockZ() >> size() < z())
			return false;
		return true;
    }
	
}
