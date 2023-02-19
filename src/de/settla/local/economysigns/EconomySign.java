package de.settla.local.economysigns;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("EconomySign")
public class EconomySign implements ConfigurationSerializable {

	private final Location location;
	private final int rank;

	public EconomySign(int rank, Location location) {
		super();
		this.location = location;
		this.rank = rank;
	}

	public EconomySign(Map<String, Object> map) {
		this.location = (Location) map.get("location");
		this.rank = (Integer) map.get("rank");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("location", location);
		map.put("rank", rank);
		return map;
	}

	public Location getLocation() {
		return location;
	}

	public int getRank() {
		return rank;
	}
	
}
