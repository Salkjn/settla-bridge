package de.settla.utilities.local.guis;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.guis.Guis.Gui;
import de.settla.utilities.module.Module;

public class GuiModule extends Module<LocalPlugin> implements Listener {

	private final Guis guis;
	private final GuiParser parser;
	private final ItemStack AIR = new ItemStack(Material.AIR);

	public GuiModule(LocalPlugin plugin) {
		super(plugin);
		guis = new Guis();
		parser = new GuiParser();
		plugin.registerListener(this);
	}

	@Override
	public void onDisable() {
		guis.closeAll();
	}
	
	public Guis getGuis() {
		return guis;
	}

	public GuiParser getParser() {
		return parser;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void a(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		Gui gui = guis.get(inventory);
		if (gui == null) {
			guis.unregister((Player) event.getWhoClicked());

			if (Guis.isSafe(event.getCurrentItem())) {
				event.setCancelled(true);
				event.setCurrentItem(AIR);
				return;
			}

			if (Guis.isSafe(event.getCursor())) {
				event.setCancelled(true);
				event.setCursor(AIR);
				return;
			}

		} else {
			gui.onGuiClickEvent(event);
		}
	}

	@EventHandler
	public void a(PlayerDropItemEvent event) {
		if (Guis.isSafe(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void a(PlayerInteractEvent event) {
		if(event.hasItem() && Guis.isSafe(event.getItem())) {
			event.getPlayer().getInventory().remove(event.getItem());
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void a(InventoryDragEvent event) {
		Inventory inventory = event.getInventory();
		Gui gui = guis.get(inventory);
		if (gui == null) {
			guis.unregister((Player) event.getWhoClicked());
		} else {
			gui.onGuiDragEvent(event);
		}
	}
	
	@EventHandler
	public void a(InventoryCloseEvent event) {
		Inventory inventory = event.getInventory();
		Gui gui = guis.get(inventory);
		if (gui != null)
			gui.onGuiCloseEvent(event);
	}
	
	public void readFile(File file) {
        if (file.isDirectory()) {
            System.out.print("Did not read 'file' " + file.getAbsolutePath() + " because it is not a file but a directory.");
        } else {
        	getParser().read(file);
        }
    }

}
