package de.settla.local.guilds.gui;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

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

public class GuildGui extends Gui {
	
	public GuildGui(Guis guis, Player player) {
		super(guis, player, null, null);
		GuiParser parser = LocalPlugin.getInstance().getModule(GuiModule.class).getParser();
		RawInventory raw = parser.getInventory("guildmain");
		Spine spine = raw.getSpine();
		setMain(new MainPage(this, spine, raw.buildItems(parser)));
	}

	class MainPage extends Page {

		public MainPage(IGui gui, ISpine spine, Map<Integer, AItemStack> items) {
			super(gui, null, spine, items);
		}

		@Override
		public void open() {
			super.open();
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
