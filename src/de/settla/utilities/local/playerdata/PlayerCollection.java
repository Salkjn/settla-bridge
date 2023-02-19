package de.settla.utilities.local.playerdata;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.settla.utilities.Notify;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("PLAYER_COLLECTION")
public class PlayerCollection implements Storable, Notify {

	private final Map<UUID, OfflinePlayer> chache = new HashMap<>();
	
	private final Set<UUID> uniqueIds = new CopyOnWriteArraySet<UUID>();
	private final Object lock = new Object();
	private boolean dirty = true;
	
	public PlayerCollection() {
		
	}
	
	@SuppressWarnings("unchecked")
	public PlayerCollection(Map<String, Object> map) {
		List<String> uuidList = (List<String>) map.get("uuids");
		uuidList.forEach(uuid -> uniqueIds.add(UUID.fromString(uuid)));
	}

	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			List<String> uuidList = new ArrayList<>();
			uniqueIds.forEach(uuid -> uuidList.add(uuid.toString()));
			map.put("uuids", uuidList);
			return map;
		}
	}
	
	public void forEach(Consumer<UUID> consumer) {
		synchronized (lock) {
			for (UUID uuid : uniqueIds) {
				consumer.accept(uuid);
			}
		}
	}
	
	public boolean isEmpty() {
		synchronized (lock) {
			return uniqueIds.isEmpty();
		}
	}
	
	public UUID getFirst() {
		synchronized (lock) {
			return uniqueIds.stream().findFirst().orElse(null);
		}
	}
	
	public void notify(Object... message) {
		String[] msg = new String[message.length];
		for (int i = 0; i < msg.length; i++) {
			msg[i] = message[i].toString();
		}
		synchronized (lock) {
			for (UUID uuid : uniqueIds) {
				Player player = Bukkit.getPlayer(uuid);
				if (player != null)
					player.sendMessage(msg);
			}
		}
	}
	
	public List<String> getNames() {
		synchronized (lock) {
			List<String> names = new ArrayList<>();
			for (UUID uuid : uniqueIds) {
				OfflinePlayer player = chache.get(uuid);
				if(player == null) {
					player = Bukkit.getOfflinePlayer(uuid);
					if(player != null)
						chache.put(uuid, player);
				}
				if(player != null) {
					names.add(player.getName());
				}
			}
			return names;
		}
	}
	
	public List<UUID> getUniqueIdsList() {
		synchronized (lock) {
			return uniqueIds.stream().collect(Collectors.toList());
		}
	}
	
	/**
	 * Add the given player to the domain, identified by the player's UUID.
	 *
	 * @param uniqueId
	 *            the UUID of the player
	 */
	public void addPlayer(UUID uniqueId) {
		synchronized (lock) {
			checkNotNull(uniqueId);
			setDirty(true);
			uniqueIds.add(uniqueId);
		}
	}
	
	/**
	 * Remove the given player from the domain, identified by the player's UUID.
	 *
	 * @param uuid
	 *            the UUID of the player
	 */
	public void removePlayer(UUID uuid) {
		synchronized (lock) {
			checkNotNull(uuid);
			setDirty(true);
			uniqueIds.remove(uuid);
		}
	}
	
	/**
	 * Get the set of player UUIDs.
	 *
	 * @return the set of player UUIDs
	 */
	public Set<UUID> getUniqueIds() {
		synchronized (lock) {
			return Collections.unmodifiableSet(uniqueIds);
		}
	}
	
	/**
     * Returns true if a domain contains a player.
     *
     * <p>This method doesn't check for groups!</p>
     *
     * @param uniqueId the UUID of the user
     * @return whether this domain contains a player by that name
     */
	public boolean contains(UUID uniqueId) {
		synchronized (lock) {
			checkNotNull(uniqueId);
			return uniqueIds.contains(uniqueId);
		}
	}
	
	public int size() {
		synchronized (lock) {
			return uniqueIds.size();
		}
	}
	
	public void clear() {
		synchronized (lock) {
			setDirty(true);
			uniqueIds.clear();
		}
	}
	
	/**
	 * @param collection the collection
	 * @return true if at least one uuid contains to collection.
	 */
	public boolean contains(PlayerCollection collection) {
		synchronized (lock) {
			checkNotNull(collection);
			for (UUID uuid : uniqueIds) {
				if(collection.contains(uuid))
					return true;
			}
			return false;
		}
	}

	@Override
	public boolean isDirty() {
		synchronized (lock) {
			return dirty;
		}
	}

	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			this.dirty = dirty;
		}
	}
}
