package de.settla.global.kits;

public enum KitType {
	
	ZOMBIE("zombie", "Zombie"),
	SKELETON("skeleton", "Skeleton"),
	CREEPER("creeper", "Creeper"),
	ENDERMAN("enderman", "Enderman"),
	
	KNIGHT("ritter", "Ritter"),
	FARMER("bauer", "Bauer"),
	WITCH("hexe", "Hexe"),
	
	ARCHER("bogen", "Bogenschütze");

	private final String name;
	private final String pretty;

	private KitType(String name, String pretty) {
		this.name = name;
		this.pretty = pretty;
	}	 
	
	public static KitType getType(String name) {
		for (KitType type : KitType.values()) {
			if (type.getName().equalsIgnoreCase(name))
				return type;
		}
		return null;
	}
	
	public String getPretty() {
		return pretty;
	}

	public String getName() {
		return name;
	}
	
}
