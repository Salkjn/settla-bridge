package de.settla.utilities.local.playerdata;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import de.settla.local.LocalPlugin;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("GlobalPlayers")
public class LocalPlayers implements Storable, Listener {

	private final ConcurrentMap<UUID, LocalPlayer> players = new ConcurrentHashMap<UUID, LocalPlayer>();
	private final Object lock = new Object();
	
	public LocalPlayers() {
		LocalPlugin.getInstance().getServer().getPluginManager().registerEvents(this, LocalPlugin.getInstance());
	}
	
	public LocalPlayers(Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			players.putIfAbsent(UUID.fromString(entry.getKey()), deserialize(entry.getValue(), LocalPlayer.class));
		}
		LocalPlugin.getInstance().getServer().getPluginManager().registerEvents(this, LocalPlugin.getInstance());
	}
	
	@Override
	public boolean isDirty() {
		synchronized (lock) {
			for (LocalPlayer data : players.values()) {
				if(data.isDirty())
					return true;
			}
			return false;
		}
	}

	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			for (LocalPlayer data : players.values()) {
				data.setDirty(dirty);
			}
		}
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			for (Entry<UUID, LocalPlayer> entry : players.entrySet()) {
				map.putIfAbsent(entry.getKey().toString(), entry.getValue().serialize());
			}
			return map;
		}
	}
	
	public LocalPlayer getLocalPlayer(UUID uuid, String ...names) {
		checkNotNull(uuid);
		synchronized (lock) {
			LocalPlayer data = players.get(uuid);
			if(data == null) {
				data = new LocalPlayer(uuid, names);
				data.setDirty(true);
				players.put(data.id(), data);
			}
			data.addNames(names);
			return data;
		}
	}
	
	public LocalPlayer getLocalPlayer(Player player) {
		checkNotNull(player);
		return getLocalPlayer(player.getUniqueId(), player.getName());
	}
	
	public boolean contains(UUID uuid) {
		checkNotNull(uuid);
		synchronized (lock) {
			return players.containsKey(uuid);
		}
	}
	
	public void consume(Consumer<LocalPlayer> consumer) {
		checkNotNull(consumer);
		synchronized (lock) {
			for (LocalPlayer data : players.values()) {
				consumer.accept(data);
			}
		}
	}
	
	public void throughPlayers(Consumer<ConcurrentMap<UUID, LocalPlayer>> consumer) {
		checkNotNull(consumer);
		synchronized (lock) {
			consumer.accept(players);
		}
	}
	
}
