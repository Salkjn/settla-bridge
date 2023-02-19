package de.settla.utilities.local.guis;

import java.util.function.Function;

import org.bukkit.entity.Player;

import de.settla.utilities.local.guis.Guis.Gui;
import de.settla.utilities.local.guis.Guis.Page;

public class SimpleGui extends Gui {

	public SimpleGui(Guis guis, Player player, Function<Gui, Page> root) {
		super(guis, player, null, null);
		setMain(root.apply(this));
	}
	
	

}
