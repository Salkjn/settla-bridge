package de.settla.local.tools.tools;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.settla.local.tools.MainSpecialItemEvent;
import de.settla.local.tools.SpecialItem;
import de.settla.local.tools.SpecialItemModule;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.region.form.Vector;

public class SelectionTool extends SpecialItem<SelectionUser> {
	
	public SelectionTool(SpecialItemModule toolModule, String itemId) {
		super(toolModule, itemId);
		
		registerEvent(new MainSpecialItemEvent(this) {
			@Override
			public void onInteractEvent(PlayerInteractEvent event, Player player, ItemStack item) {
				
				
				if (!player.hasPermission("tools.selection")) {
					return;
				}
				
				
				event.setCancelled(true);
				
				SelectionUser user = getSpecialItemUser(player.getUniqueId());
				
				if(user == null) {
					user = new SelectionUser(player);
					addSpecialItemUser(player.getUniqueId(), user);
				}
				
				final SelectionUser finalUser = user;
				
				if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
					finalUser.setPos1(new Vector(event.getClickedBlock()));
					player.sendMessage(ChatConvention.title("Selection")+"pos1 gesetzt.");
				} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					finalUser.setPos2(new Vector(event.getClickedBlock()));
					player.sendMessage(ChatConvention.title("Selection")+"pos2 gesetzt.");
				}
			}
		});
	}
}
