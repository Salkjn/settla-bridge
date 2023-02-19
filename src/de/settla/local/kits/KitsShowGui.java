package de.settla.local.kits;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.guis.GuiModule;
import de.settla.utilities.local.guis.GuiParser;
import de.settla.utilities.local.guis.GuiParser.RawInventory;
import de.settla.utilities.local.guis.Guis;
import de.settla.utilities.local.guis.Guis.AItemStack;
import de.settla.utilities.local.guis.Guis.Gui;
import de.settla.utilities.local.guis.Guis.IGui;
import de.settla.utilities.local.guis.Guis.ISpine;
import de.settla.utilities.local.guis.Guis.Page;
import de.settla.utilities.local.guis.Guis.Spine;

public class KitsShowGui extends Gui {

	private final Kit kit;

	public KitsShowGui(Guis guis, Player player, Kit kit) {
		super(guis, player, null, null);
		this.kit = kit;
		GuiParser parser = LocalPlugin.getInstance().getModule(GuiModule.class).getParser();
		RawInventory raw = parser.getInventory("kitshow");
		Spine spine = raw.getSpine();
		// (int) Math.ceil(((double) kit.getItems().size()) / 9D)
		double v = (double) kit.getItems().size() / 9.0;
		spine = new Spine((int)v + 1, "§e§lKit: §0" + ChatColor.translateAlternateColorCodes('&', kit.getMeta().getType().getPretty()));
		setMain(new KeyPage(this, spine, raw.buildItems(parser)));
	}

	class KeyPage extends Page {

		public KeyPage(IGui gui, ISpine spine, Map<Integer, AItemStack> items) {
			super(gui, null, spine, items);
		}

		@Override
		public void open() {
			super.open();
			for (ItemStack item : kit.getItems()) {
				getInventory().addItem(Guis.safe(item.clone()));
			}
		}
		
		@Override
		public void onPageDragEvent(InventoryDragEvent event) {
			event.setCancelled(true);
		}
		
		@Override
		public void onPageClickEvent(InventoryClickEvent event) {
			event.setCancelled(true);
		}
		
	}
}
