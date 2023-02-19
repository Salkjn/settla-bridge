package de.settla.local.tools.tools;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.settla.economy.accounts.BeamAccountHandler;
import de.settla.economy.accounts.ServerAccountHandler;
import de.settla.local.LocalPlugin;
import de.settla.local.tools.MainSpecialItemEvent;
import de.settla.local.tools.SpecialItem;
import de.settla.local.tools.SpecialItemModule;
import de.settla.local.tools.SpecialItemUser;
import de.settla.local.tools.nbt.NBTItem;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.guis.ItemBuilder;

public class BeamItem extends SpecialItem<SpecialItemUser> {

	public BeamItem(SpecialItemModule toolModule, String itemId) {
		super(toolModule, itemId);
		
		registerEvent(new MainSpecialItemEvent(this) {
			@Override
			public void onInteractEvent(PlayerInteractEvent event, Player player, ItemStack item) {
				event.setCancelled(true);
				LocalPlugin.getInstance().getEconomy().transfer(ServerAccountHandler.class, "beams", String.class, BeamAccountHandler.class, event.getPlayer().getUniqueId(), UUID.class, 1, answer -> {
					if(answer.isSuccess()) {
						Utils.decreaseHand(player);
						event.getPlayer().sendMessage(ChatConvention.title("Beam") + "Du hast ein Beam erhalten.");
					} else {
						event.getPlayer().sendMessage(ChatConvention.title("Beam") + "Du hast bereits die maximale Anzahl von Beams.");
					}
				});
			}
		});	
	}

	@Override
	public ItemStack getItemStack(ItemStack item) {
		ItemBuilder builder = new ItemBuilder(Material.BLAZE_ROD);
		builder.addEnchantEffect();
		builder.setName("§2§l+1 Beam");
		NBTItem nbt = new NBTItem(builder.build());
		nbt.setString(SpecialItemModule.NBT_ID, "beam");
		return nbt.getItem();
	}
	
}
