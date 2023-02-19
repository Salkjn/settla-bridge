package de.settla.local.tools;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.settla.local.tools.nbt.NBTItem;

public abstract class MainSpecialItemEvent extends SpecialItemEvent {

	public MainSpecialItemEvent(SpecialItem<?> specialItem) {
		super(specialItem);
	}

	private final Set<ItemStack> chachedNonItems = new HashSet<>();
	private final Set<ItemStack> chachedItems = new HashSet<>();
	private final Object lock = new Object();

	@EventHandler
	public void e(PlayerInteractEvent event) {
		
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		
		if(event.hasItem()) {
			synchronized (lock) {
				if(chachedNonItems.contains(item))
					return;
				if(chachedItems.contains(item)) {
					onInteractEvent(event, player, item);
					return;
				}
				String itemId = new NBTItem(item).getString(SpecialItemModule.NBT_ID);
				if(itemId.isEmpty()) {
					chachedNonItems.add(item);
					return;
				} else {
					if(itemId.equalsIgnoreCase(this.getSpecialItem().getItemId())) {
						chachedItems.add(item);
						onInteractEvent(event, player, item);
					} else {
						chachedNonItems.add(item);
					}
				}
			}
		}
	}
	
	public abstract void onInteractEvent(PlayerInteractEvent event, Player player, ItemStack item);
}
