package de.settla.local.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import de.settla.local.tools.nbt.NBTItem;

public class SpecialItem<T extends SpecialItemUser> {
	
	private final Map<UUID, T> users = new HashMap<>();
	private final List<SpecialItemEvent> events = new ArrayList<>();
	
	private final SpecialItemModule toolModule;
	private final Object lock = new Object();
	private final String itemId;
	
	public SpecialItem(SpecialItemModule toolModule, String itemId) {
		this.toolModule = toolModule;
		this.itemId = itemId;
	}
	
	public String getItemId() {
		return itemId;
	}

	public void registerEvent(SpecialItemEvent event) {
		synchronized (lock) {
			events.add(event);
		}
	}

	public SpecialItemModule getSpecialItemModule() {
		return toolModule;
	}
	
	public void forEachUser(Consumer<SpecialItemUser> consumer) {
		synchronized (lock) {
			users.values().forEach(user -> consumer.accept(user));
		}
	}
	
	public void forEachEvent(Consumer<SpecialItemEvent> consumer) {
		synchronized (lock) {
			events.forEach(event -> consumer.accept(event));
		}
	}
	
	public boolean isSpecialItemUser(UUID uuid) {
		synchronized (lock) {
			return users.containsKey(uuid);
		}
	}
	
	public T getSpecialItemUser(UUID uuid) {
		synchronized (lock) {
			return users.get(uuid);
		}
	}
	
	public void addSpecialItemUser(UUID uuid, T user) {
		synchronized (lock) {
			users.put(uuid, user);
		}
	}
	
	public void removeSpecialItemUser(UUID uuid) {
		synchronized (lock) {
			users.remove(uuid);
		}
	}
	
	public ItemStack getItemStack(ItemStack item) {
		NBTItem nbt = new NBTItem(item);
		nbt.setString(SpecialItemModule.NBT_ID, getItemId());
		return nbt.getItem();
	}
}
