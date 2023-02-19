package de.settla.utilities.local.region;

import java.util.Map;

import de.settla.utilities.local.region.form.Empty;
import de.settla.utilities.storage.Serial;

@Serial("WildernessRegion")
public class WildnessRegion extends Region {

	private World world; 
	
	public WildnessRegion() {
		super(new Empty(), 0, false, "__global__");
	}

	public WildnessRegion(Map<String, Object> map) {
		super(map);
	}
	
	public World getWorld() {
		return world;
	}
	
	/**
	 * Do not use this method!
	 * This is only for the connection to the world.
	 * 
	 * @param world the world.
	 */
	protected void setWorld(World world) {
		this.world = world;
	}
	
}
