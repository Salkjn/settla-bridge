package de.settla.utilities.global.fetcher;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

/**
 * A cache of username->UUID mappings that automatically cleans itself.
 *
 * This cache is meant to be used in plugins such that plugins can look up the
 * UUID of a player by using the name of the player.
 *
 * For the most part, when the plugin asks the cache for the UUID of an online
 * player, it should have it available immediately because the cache registers
 * itself for the player join/quit events and does background fetches.
 *
 */
public class UUIDCache implements Listener {

	private static final UUID ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private Map<String, UUID> cache = new ConcurrentHashMap<String, UUID>();
	private Plugin plugin;

	public UUIDCache(Plugin plugin) {
		this.plugin = plugin;
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}

	/**
	 * Get the UUID from the cache for the player named 'name'.
	 *
	 * If the id does not exist in our database, then we will queue a fetch to
	 * get it, and return null. A fetch at a later point will then be able to
	 * return this id.
	 */
	public UUID getIdOptimistic(String name) {
		UUID uuid = cache.get(name);
		if (uuid == null) {
			ensurePlayerUUID(name);
			return null;
		}
		return uuid;
	}

	/**
	 * Get the UUID from the cache for the player named 'name', with blocking
	 * get.
	 *
	 * If the player named is not in the cache, then we will fetch the UUID in a
	 * blocking fashion. Note that this will block the thread until the fetch is
	 * complete, so only use this in a thread or in special circumstances.
	 *
	 * @param name
	 *            The player name.
	 * @return a UUID
	 */
	public UUID getId(String name) {
		UUID uuid = cache.get(name);
		if (uuid == null) {
			syncFetch(nameList(name));
			return cache.get(name);
		} else if (uuid.equals(ZERO_UUID)) {
			uuid = null;
		}
		return uuid;
	}

	/**
	 * Asynchronously fetch the name if it's not in our internal map.
	 * 
	 * @param name
	 *            The player's name
	 */
	public void ensurePlayerUUID(String name) {
		if (cache.containsKey(name))
			return;
		cache.put(name, ZERO_UUID);
		asyncFetch(nameList(name));
	}

	private void asyncFetch(final ArrayList<String> names) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			@Override
			public void run() {
				syncFetch(names);
			}
		});
	}

	private void syncFetch(ArrayList<String> names) {
		final UUIDFetcher fetcher = new UUIDFetcher(names);
		try {
			cache.putAll(fetcher.call());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> nameList(String name) {
		ArrayList<String> names = new ArrayList<String>();
		names.add(name);
		return names;
	}

	@EventHandler
	public void e(PostLoginEvent event) {
		cache.put(event.getPlayer().getName(), event.getPlayer().getUniqueId());
	}

}