package de.settla.global.kits;

import java.util.HashMap;
import java.util.Map;

public class KitMeta {

	private static Map<KitType, KitMeta> kits = new HashMap<>();
	
	static {
		kits.put(KitType.ZOMBIE, new KitMeta(KitType.ZOMBIE, "kits.zombie", "kit_zombie", 1000L * 60L));
		kits.put(KitType.ENDERMAN, new KitMeta(KitType.ENDERMAN, null, "kit_enderman", 1000L * 60L * 60L * 24L * 100L));
		kits.put(KitType.SKELETON, new KitMeta(KitType.SKELETON, null, "kit_skeleton", 1000L * 60L * 60L));
		kits.put(KitType.CREEPER, new KitMeta(KitType.CREEPER, null, "kit_creeper", 1000L * 60L * 60L));
		
		kits.put(KitType.KNIGHT, new KitMeta(KitType.KNIGHT, null, "kit_ritter", 1000L * 60L * 60L * 24L));
		kits.put(KitType.WITCH, new KitMeta(KitType.WITCH, null, "kit_hexe", 1000L * 60L * 60L));
		kits.put(KitType.FARMER, new KitMeta(KitType.FARMER, null, "kit_bauer", 1000L * 60L * 60L * 12L));
		kits.put(KitType.ARCHER, new KitMeta(KitType.ARCHER, null, "kit_bogen", 1000L * 60L * 60L * 24L));
		
	}
	
	public static KitMeta getKitMeta(KitType type) {
		return kits.get(type);
	}
	
	private final KitType type;
	private final String permission;
	private final String npc;
	private final long difTime;
	
	private KitMeta(KitType type, String permission, String npc, long difTime) {
		super();
		this.type = type;
		this.permission = permission;
		this.npc = npc;
		this.difTime = difTime;
	}
	
	public String getNpc() {
		return npc;
	}

	public KitType getType() {
		return type;
	}

	public String getPermission() {
		return permission;
	}

	public long getDifTime() {
		return difTime;
	}
	
}
