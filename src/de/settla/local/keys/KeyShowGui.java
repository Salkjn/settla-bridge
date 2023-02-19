package de.settla.local.keys;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.Utils;
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
import de.settla.utilities.local.guis.ItemBuilder;

public class KeyShowGui extends Gui {

	private final Key key;

	public KeyShowGui(Guis guis, Player player, Key key) {
		super(guis, player, null, null);
		this.key = key;
		GuiParser parser = LocalPlugin.getInstance().getModule(GuiModule.class).getParser();
		RawInventory raw = parser.getInventory("keyshow");
		Spine spine = raw.getSpine();
		spine = new Spine((int) Math.ceil(((double) key.getItemList().size()) / 9D), ChatColor.translateAlternateColorCodes('&', key.getName() + spine.title()));
		setMain(new KeyPage(this, spine, raw.buildItems(parser)));
	}

	class KeyPage extends Page {

		public KeyPage(IGui gui, ISpine spine, Map<Integer, AItemStack> items) {
			super(gui, null, spine, items);
		}

		@Override
		public void open() {
			super.open();
			List<KeyItemable> items = key.getItemList();
			for (KeyItemable item : items) {
				ItemBuilder ib = new ItemBuilder(item.getItemStack().clone());
				ib.addLore(" ");
				ib.addLore(KeyModule.convert(KeyModule.INVENTORY_CHANCE,
						new String[][] { { "chance", Utils.getPercent(key.getChance(item)) + "%" } }));
				getInventory().addItem(Guis.safe(ib.build()));
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
