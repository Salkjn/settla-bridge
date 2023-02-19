package de.settla.local.tools.tools;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import de.settla.local.tools.MainSpecialItemEvent;
import de.settla.local.tools.SpecialItem;
import de.settla.local.tools.SpecialItemModule;
import de.settla.local.tools.SpecialItemUser;

public class ChestLookerItem extends SpecialItem<SpecialItemUser> {

	public ChestLookerItem(SpecialItemModule toolModule, String itemId) {
		super(toolModule, itemId);
		
		registerEvent(new MainSpecialItemEvent(this) {
			@Override
			public void onInteractEvent(PlayerInteractEvent event, Player player, ItemStack item) {
				event.setCancelled(true);
				
				if (!player.hasPermission("tools.chestlooker")) {
					return;
				}
				
				if (event.hasBlock()) {
					BlockState state = event.getClickedBlock().getState();	
					if (state instanceof InventoryHolder) {
						InventoryHolder holder = (InventoryHolder) state;
						player.openInventory(holder.getInventory());
					}
				}
			}
		});	
	}
}
