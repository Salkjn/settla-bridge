package de.settla.local.kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;

import de.settla.local.npc.NpcModel;

public class KitNpcModel extends NpcModel {

	private final Kit kit;

	@SafeVarargs
	public KitNpcModel(Kit kit, String model, Function<Kit, Consumer<Player>> interact, Function<Kit, Consumer<Player>> attack, Function<Kit, Function<Player, String>>... lines) {
		this(kit, model, interact, attack, Arrays.asList(lines));
	}

	public KitNpcModel(Kit kit, String model, Function<Kit, Consumer<Player>> interact, Function<Kit, Consumer<Player>> attack, List<Function<Kit, Function<Player, String>>> lines) {
		super(model, convert(kit, interact), convert(kit, attack), convert(kit, lines));
		this.kit = kit;
	}

	private static Consumer<Player> convert(Kit kit, Function<Kit, Consumer<Player>> interact) {
		return interact.apply(kit);
	}
	
	private static List<Function<Player, String>> convert(Kit kit, List<Function<Kit, Function<Player, String>>> lines) {
		List<Function<Player, String>> list = new ArrayList<>();
		for (Function<Kit, Function<Player, String>> function : lines) {
			list.add(function.apply(kit));
		}
		return list;
		
	}
	
	public Kit getKit() {
		return kit;
	}

}
