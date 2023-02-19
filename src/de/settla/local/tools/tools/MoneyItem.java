package de.settla.local.tools.tools;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.settla.local.VaultHelper;
import de.settla.local.tools.MainSpecialItemEvent;
import de.settla.local.tools.SpecialItem;
import de.settla.local.tools.SpecialItemModule;
import de.settla.local.tools.SpecialItemUser;
import de.settla.local.tools.nbt.NBTItem;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.guis.ItemBuilder;
import net.milkbowl.vault.economy.EconomyResponse;

public class MoneyItem extends SpecialItem<SpecialItemUser> {

	public MoneyItem(SpecialItemModule toolModule, String itemId) {
		super(toolModule, itemId);
		
		registerEvent(new MainSpecialItemEvent(this) {
			@Override
			public void onInteractEvent(PlayerInteractEvent event, Player player, ItemStack item) {
				event.setCancelled(true);
				NBTItem nbt = new NBTItem(item);
				double price = nbt.getInt("price");
				EconomyResponse response = VaultHelper.ECONOMY.depositPlayer(player, price);
				if (response.transactionSuccess()) {
					Utils.decreaseHand(player);
					player.sendMessage(ChatConvention.title("Settla") + "Du hast " + ChatConvention.spezial(price + "$") + " übertragen bekommen");
				} else {
					player.sendMessage(ChatConvention.title("Settla") + "Es ist ein Problem aufgetreten, hast du evtl. zu viel Geld?");
				}
			}
		});	
	}

	@Override
	public ItemStack getItemStack(ItemStack item) {
		return getMoneyPaper(item.getAmount() * 10);
	}
	
	public static ItemStack getMoneyPaper(double price) {
		ItemBuilder builder = new ItemBuilder(Material.PAPER);
		builder.addEnchantEffect();
		builder.setName("§2§lGeldschein: §a" + price+"$");
		NBTItem nbt = new NBTItem(builder.build());
		nbt.setString(SpecialItemModule.NBT_ID, "money");
		nbt.setDouble("price", price);
		return nbt.getItem();
	}
	
}
