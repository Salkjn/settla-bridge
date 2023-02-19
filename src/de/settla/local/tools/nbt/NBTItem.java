package de.settla.local.tools.nbt;

import org.bukkit.inventory.ItemStack;

public class NBTItem extends NBTObject {

	private ItemStack item;
	private Object nmsItem;
	
	public NBTItem(ItemStack item) {
		this.item = item;
		nmsItem = Ref.NBT.toNMSItem(this.item);
		nbt = Ref.NBT.getNBTTag(nmsItem);
	}
	
	public ItemStack getItem(){
		Ref.NBT.applyNBT(nmsItem, nbt);
		item = Ref.NBT.toBukkitItem(nmsItem);
		return item;
	}
	
}
