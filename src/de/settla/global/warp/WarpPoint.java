package de.settla.global.warp;

import java.util.Map;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable;

@Serial("WarpPoint")
public class WarpPoint implements Storable {
	
	private final double x, y, z;
	private final float pitch, yaw;
	private final String world, server, name;
	
	public WarpPoint(String name, double x, double y, double z, float pitch, float yaw, String world, String server) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.world = world;
		this.server = server;
	}

	public WarpPoint(Map<String, Object> map) {
		this.name = (String) map.get("name");
		this.x = StaticParser.parse((String) map.get("x"), Double.class);
		this.y = StaticParser.parse((String) map.get("y"), Double.class);
		this.z = StaticParser.parse((String) map.get("z"), Double.class);
		this.pitch = StaticParser.parse((String) map.get("pitch"), Float.class);
		this.yaw = StaticParser.parse((String) map.get("yaw"), Float.class);
		this.world = (String) map.get("world");
		this.server = (String) map.get("server");
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("name", name);
		map.put("world", world);
		map.put("server", server);
		map.put("x", StaticParser.unparse(x, Double.class));
		map.put("y", StaticParser.unparse(y, Double.class));
		map.put("z", StaticParser.unparse(z, Double.class));
		map.put("pitch", StaticParser.unparse(pitch, Float.class));
		map.put("yaw", StaticParser.unparse(yaw, Float.class));
		return map;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public String getWorld() {
		return world;
	}

	public String getServer() {
		return server;
	}

	public String getName() {
		return name;
	}
	
}
