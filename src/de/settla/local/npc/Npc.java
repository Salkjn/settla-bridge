package de.settla.local.npc;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

public class Npc {

	private final String name;
	private final String model;
	private final Consumer<Player> interact, attack;
	private final NpcEntity entity;
	
	public Npc(String model, String name, Consumer<Player> interact, Consumer<Player> attack, NpcEntity entity) {
		super();
		this.model = model;
		this.name = name;
		this.interact = interact;
		this.attack = attack;
		this.entity = entity;
	}

	public String getName() {
		return name;
	}

	public String getModelName() {
		return model;
	}

	public Consumer<Player> getInteract() {
		return interact;
	}
	
	public Consumer<Player> getAttack() {
		return attack;
	}

	public NpcEntity getNpcEntity() {
		return entity;
	}
	
}
