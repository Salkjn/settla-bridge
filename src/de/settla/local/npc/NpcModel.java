package de.settla.local.npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;

public class NpcModel {

	private final String model;
	private final Consumer<Player> interact;
	private final Consumer<Player> attack;
	private final List<Function<Player, String>> lines = new ArrayList<>();
	
	@SafeVarargs
	public NpcModel(String model, Consumer<Player> interact, Consumer<Player> attack, Function<Player, String>... lines) {
		this(model, interact, attack, Arrays.asList(lines));
	}
	
	public NpcModel(String model, Consumer<Player> interact, Consumer<Player> attack, List<Function<Player, String>> lines) {
		super();
		this.model = model;
		this.interact = interact;
		this.attack = attack;
		this.lines.addAll(lines);
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

	public List<Function<Player, String>> getLines() {
		return lines;
	}
	
}
