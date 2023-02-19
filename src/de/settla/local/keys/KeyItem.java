package de.settla.local.keys;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class KeyItem implements KeyItemable {

	private final ItemStack itemstack;
	private final double chance;

	public KeyItem(ItemStack itemstack, double chance) {
		this.itemstack = itemstack;
		this.chance = chance;
	}

	@Override
	public ItemStack getItemStack() {
		return itemstack;
	}

	@Override
	public double getChance() {
		return chance;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("item", getItemStack());
		map.put("chance", getChance());
		return map;
	}
	
	public static KeyItem deserialize(Map<String, Object> args) {
		return new KeyItem((ItemStack) args.get("item"), (Double)args.get("chance"));
	}

}
