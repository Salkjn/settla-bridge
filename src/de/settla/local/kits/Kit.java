package de.settla.local.kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.settla.global.kits.KitMeta;

public class Kit {

	private final KitMeta meta;
	private final List<ItemStack> items = new ArrayList<>();
	
	public Kit(KitMeta meta) {
		super();
		this.meta = meta;
	}
	
	public Kit(KitMeta meta, ItemStack ... item) {
		this(meta);
		for (ItemStack itemStack : item) {
			addItem(itemStack);
		}
	}
	
	public void addItem(ItemStack item) {
		items.add(item);
	}
	
	public KitMeta getMeta() {
		return meta;
	}
	
	public boolean enoughInventorySpace(Player player) {
		long spaces = Arrays.stream(player.getInventory().getContents())
				.filter(i -> i == null || i.getType() == Material.AIR).count();
		return spaces >= items.size();
	}
	
	public List<ItemStack> getItems() {
		return items;
	}
	
	public void equipPlayer(Player player) {
		for (ItemStack r : items) {
			if (r != null) {
				ItemStack item = r.clone();
				if (item != null) {
					switch (item.getType()) {
					case DIAMOND_BOOTS:
					case IRON_BOOTS:
					case CHAINMAIL_BOOTS:
					case LEATHER_BOOTS:
					case GOLD_BOOTS:
						if (player.getInventory().getBoots() != null)
							player.getInventory().addItem(player.getInventory().getBoots());
						player.getInventory().setBoots(item);
						break;

					case DIAMOND_LEGGINGS:
					case IRON_LEGGINGS:
					case CHAINMAIL_LEGGINGS:
					case LEATHER_LEGGINGS:
					case GOLD_LEGGINGS:
						if (player.getInventory().getLeggings() != null)
							player.getInventory().addItem(player.getInventory().getLeggings());
						player.getInventory().setLeggings(item);
						break;

					case DIAMOND_CHESTPLATE:
					case IRON_CHESTPLATE:
					case CHAINMAIL_CHESTPLATE:
					case LEATHER_CHESTPLATE:
					case GOLD_CHESTPLATE:
						if (player.getInventory().getChestplate() != null)
							player.getInventory().addItem(player.getInventory().getChestplate());
						player.getInventory().setChestplate(item);
						break;

					case DIAMOND_HELMET:
					case IRON_HELMET:
					case CHAINMAIL_HELMET:
					case LEATHER_HELMET:
					case GOLD_HELMET:
						if (player.getInventory().getHelmet() != null)
							player.getInventory().addItem(player.getInventory().getHelmet());
						player.getInventory().setHelmet(item);
						break;

					default:
						player.getInventory().addItem(item);
						break;
					}
				}
			}
		}
	}
}
