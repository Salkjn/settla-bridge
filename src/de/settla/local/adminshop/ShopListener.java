package de.settla.local.adminshop;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.util.concurrent.AtomicDouble;

import de.settla.economy.accounts.AdminShopHandler;
import de.settla.economy.accounts.PurseHandler;
import de.settla.local.LocalPlugin;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.commands.ArgumentParser;

public class ShopListener implements Listener {

	private final ShopModule module;
	
	public ShopListener(ShopModule module) {
		super();
		this.module = module;
	}
	//TEST
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getLine(0) != null && e.getLine(1) != null && e.getLine(2) != null) {
			ArgumentParser parser = new ArgumentParser(e.getLines());
			String as = parser.get(1);
			if (as.equalsIgnoreCase("AdminShop")) {
				Double price = parser.getDouble(2);
				Boolean buyable = parser.getBoolean(3);
				if (price != null && buyable != null) {
					ShopSign sign = new ShopSign(e.getBlock().getLocation(), null, price, buyable);
					module.consumeSigns(c -> {
						c.add(sign);
						module.setDirty(true);
						e.setCancelled(true);
					});
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if (b != null && b.getState() instanceof Sign) {
			Location signLocation = b.getLocation();
			module.consumeSigns(list -> {
				ShopSign shopSign = list.stream()
						.filter(sign -> sign.getLocation().getWorld().equals(signLocation.getWorld())
								&& sign.getLocation().distanceSquared(signLocation) == 0)
						.findFirst().orElse(null);
				if (shopSign == null)
					return;
				if (shopSign.getItemStack() == null)
					return;
				e.setCancelled(true);
				
				double price = shopSign.getPlayerPrice(p);
				
				int maxStackSize = shopSign.getItemStack().getMaxStackSize();
				AtomicDouble amount = new AtomicDouble(p.isSneaking()
						? (shopSign.getItemStack().getAmount() < 16 ? 16 : 64) : shopSign.getItemStack().getAmount());
				if (amount.get() > maxStackSize)
					amount.set(maxStackSize);

				double oldAmount = shopSign.getItemStack().getAmount();
				ItemStack cloned = shopSign.getItemStack().clone();
				double finalBalance = Utils.wrapper.backward(Utils.wrapper.forward(price * amount.get() / oldAmount));
				cloned.setAmount((int) amount.get());
				if (shopSign.isBuyable()) {
					// Check full inventory
					boolean space = Arrays.stream(p.getInventory().getContents())
							.anyMatch(i -> i == null || i.getType() == Material.AIR);

					if (space) {
						LocalPlugin.getInstance().getEconomy().transfer(PurseHandler.class, p.getUniqueId(), UUID.class,
								AdminShopHandler.class, shopSign.getItemStack().getTypeId(), Integer.class,
								finalBalance, answer -> {
									if (answer.isSuccess()) {
										p.getInventory().addItem(cloned);
										Utils.sendActionbarMessage(p,
												ChatConvention.title("AdminShop") + "Du hast "
														+ ChatConvention.spezial((int) amount.get()) + " §7"
														+ Utils.prettifyText(
																shopSign.getItemStack().getType().toString())
														+ " gekauft für " + finalBalance + "$.");
									} else {
										// Fehler
										Utils.sendActionbarMessage(p,
												ChatConvention.title("AdminShop") + "Du hast nicht genügend Geld.");
									}
								});
					} else {
						Utils.sendActionbarMessage(p,
								ChatConvention.title("AdminShop") + "Du hast nicht genügend Platz im Inventar.");
					}

				} else {
					// Richtiges Item in inv check

					Map<Integer, ItemStack> map = p.getInventory().removeItem(cloned);
					if (map.isEmpty()) {
						LocalPlugin.getInstance().getEconomy().transfer(AdminShopHandler.class,
								shopSign.getItemStack().getTypeId(), Integer.class, PurseHandler.class, p.getUniqueId(),
								UUID.class, finalBalance, answer -> {
									if (answer.isSuccess()) {
										Utils.sendActionbarMessage(p,
												ChatConvention.title("AdminShop") + "Du hast "
														+ ChatConvention.spezial((int) amount.get()) + " §7"
														+ Utils.prettifyText(
																shopSign.getItemStack().getType().toString())
														+ " verkauft für " + finalBalance + "$.");
									} else {
										// Fehler
										p.getInventory().addItem(cloned);
										Utils.sendActionbarMessage(p,
												ChatConvention.title("AdminShop") + "Du hast bereits zu viel Geld.");
									}
								});
					} else {
						map.forEach((index, item) -> {
							item.setAmount((int) amount.get() - item.getAmount());
							if (item.getAmount() <= 0)
								return;
							p.getInventory().addItem(item);
						});
						Utils.sendActionbarMessage(p, ChatConvention.title("AdminShop")
								+ "Du hast nicht genug Items zum Verkauf im Inventar.");
					}
				}
			});
		}
	}
}
