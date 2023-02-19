package de.settla.local.adminshop;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.Description;
import de.settla.utilities.local.commands.OverviewCommand;
import de.settla.utilities.local.commands.Perm;
import de.settla.utilities.local.commands.PlayerCommand;

public class ShopCommand extends OverviewCommand {

	public ShopCommand(String name, String... aliases) {
		super(name, aliases);
		addSubCommand(new SetItemStackShopCommand("setitem"));
		addSubCommand(new RemoveShopCommand("remove"));
	}

	@Description(description = "Ändert bei Shop den ItemStack.")
	@Perm("shop.setitem")
	public class SetItemStackShopCommand extends PlayerCommand {

		public SetItemStackShopCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
				player.sendMessage(ChatConvention.title("AdminShop") + "Du musst ein Item in der Hand haben!");
				return;
			}
			@SuppressWarnings("deprecation")
			Location signLocation = player.getTargetBlock((HashSet<Byte>) null, 10).getLocation();

			if (signLocation == null) {
				player.sendMessage(ChatConvention.title("AdminShop") + "Du schaust auf keinen Block.");
			} else {
				LocalPlugin.getInstance().getModule(ShopModule.class).consumeSigns(list -> {
					List<ShopSign> signs = list.stream()
							.filter(sign -> sign.getLocation().getWorld().equals(signLocation.getWorld())
									&& sign.getLocation().distanceSquared(signLocation) == 0)
							.collect(Collectors.toList());
					signs.forEach(sign -> {
						sign.setItemStack(player.getItemInHand().clone());
					});
					LocalPlugin.getInstance().getModule(ShopModule.class).setDirty(true);
					player.sendMessage(ChatConvention.title("AdminShop") + "Item geändert.");
				});
			}

		}

	}

	@Description(description = "Entfernt Shop.")
	@Perm("shop.remove")
	public class RemoveShopCommand extends PlayerCommand {

		public RemoveShopCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			@SuppressWarnings("deprecation")
			Location signLocation = player.getTargetBlock((HashSet<Byte>) null, 10).getLocation();

			if (signLocation == null) {
				player.sendMessage(ChatConvention.title("AdminShop") + "Du schaust auf keinen Block.");
			} else {
				LocalPlugin.getInstance().getModule(ShopModule.class).consumeSigns(list -> {
					List<ShopSign> signs = list.stream()
							.filter(sign -> !(sign.getLocation().getWorld().equals(signLocation.getWorld())
									&& sign.getLocation().distanceSquared(signLocation) == 0))
							.collect(Collectors.toList());
					list.clear();
					list.addAll(signs);
					LocalPlugin.getInstance().getModule(ShopModule.class).setDirty(true);
					player.sendMessage(ChatConvention.title("AdminShop") + "Schild entfernt.");
				});
			}

		}

	}

}
