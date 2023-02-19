package de.settla.local.npc;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import de.settla.utilities.local.Base64;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;


@Serial("npc-data")
public class NpcData implements Storable {

	private final String name;
	private final String model;
	private final EntityType type;
	private final Location location;

	public NpcData(String model, String name, Location location, EntityType type) {
		super();
		this.model = model;
		this.name = name;
		this.location = location;
		this.type = type;
	}

	@SuppressWarnings("deprecation")
	public NpcData(Map<String, Object> map) {
		this.model = (String) map.get("model");
		this.name = (String) map.get("name");
		this.location = Base64.fromBase64((String) map.get("location"), Location.class, () -> Bukkit.getWorld("world").getSpawnLocation());
		this.type = EntityType.fromName((String) map.get("type"));
	}

	@SuppressWarnings("deprecation")
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("model", model);
		map.put("name", name);
		map.put("type", type.getName());
		map.put("location",
				Base64.toBase64(location, Location.class, () -> Bukkit.getWorld("world").getSpawnLocation()));
		return map;
	}
	
	public Npc createNpc(NpcModel model) {
		return new Npc(this.model, this.name, model.getInteract(), model.getAttack(), new NpcEntity(this.location, this.type, model.getLines()));
	}

	public String getName() {
		return name;
	}

	public String getModel() {
		return model;
	}

	public EntityType getType() {
		return type;
	}

	public Location getLocation() {
		return location;
	}
	
}
