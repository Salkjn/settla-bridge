package de.settla.utilities.global.playerdata;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import de.settla.global.GlobalPlugin;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@Serial("GlobalPlayers")
public class GlobalPlayers implements Storable, Listener {

	private final ConcurrentMap<UUID, GlobalPlayer> players = new ConcurrentHashMap<UUID, GlobalPlayer>();
	private final Object lock = new Object();
	
	public GlobalPlayers() {
		GlobalPlugin.getInstance().getProxy().getPluginManager().registerListener(GlobalPlugin.getInstance(), this);
	}
	
	public GlobalPlayers(Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			players.putIfAbsent(UUID.fromString(entry.getKey()), deserialize(entry.getValue(), GlobalPlayer.class));
		}
		GlobalPlugin.getInstance().getProxy().getPluginManager().registerListener(GlobalPlugin.getInstance(), this);
	}
	
	@Override
	public boolean isDirty() {
		synchronized (lock) {
			for (GlobalPlayer data : players.values()) {
				if(data.isDirty())
					return true;
			}
			return false;
		}
	}

	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			for (GlobalPlayer data : players.values()) {
				data.setDirty(dirty);
			}
		}
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			for (Entry<UUID, GlobalPlayer> entry : players.entrySet()) {
				map.putIfAbsent(entry.getKey().toString(), entry.getValue().serialize());
			}
			return map;
		}
	}
	
	public GlobalPlayer getGlobalPlayer(UUID uuid, boolean updateNames, String ...names) {
		checkNotNull(uuid);
		synchronized (lock) {
			GlobalPlayer data = players.get(uuid);
			if(data == null) {
				data = new GlobalPlayer(uuid, names);
				data.setDirty(true);
				players.put(data.id(), data);
			}
			if(updateNames)
				data.addNames(names);
			return data;
		}
	}
	
	public GlobalPlayer getGlobalPlayer(ProxiedPlayer player) {
		checkNotNull(player);
		return getGlobalPlayer(player.getUniqueId(), true, player.getName());
	}
	
	public GlobalPlayer getGlobalPlayer(UUID uuid) {
		checkNotNull(uuid);
		return getGlobalPlayer(uuid, false);
	}
	
	public boolean contains(UUID uuid) {
		checkNotNull(uuid);
		synchronized (lock) {
			return players.containsKey(uuid);
		}
	}
	
	public void consume(Consumer<GlobalPlayer> consumer) {
		checkNotNull(consumer);
		synchronized (lock) {
			for (GlobalPlayer data : players.values()) {
				consumer.accept(data);
			}
		}
	}
	
	@EventHandler
	public void e(PostLoginEvent event) {
		getGlobalPlayer(event.getPlayer());
	}
	
}
