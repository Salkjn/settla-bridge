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
 * <p>
 * This cache is meant to be used in plugins such that plugins can look up the
 * UUID of a player by using the name of the player.
 * <p>
 * For the most part, when the plugin asks the cache for the UUID of an online
 * player, it should have it available immediately because the cache registers
 * itself for the player join/quit events and does background fetches.
 *
 * @author James Crasta
 */
public class NameCache implements Listener {
	
	private final Map<UUID, String> cache = new ConcurrentHashMap<>();
	
    private Plugin plugin;
    
    public NameCache(Plugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }
    
    /**
     * Get the UUID from the cache for the player named 'name'.
     * <p>
     * If the id does not exist in our database, then we will queue
     * a fetch to get it, and return null. A fetch at a later point
     * will then be able to return this id.
     */
    public String getNameOptimistic(UUID uuid) {
        String name = cache.get(uuid);
        if (name == null) {
            ensurePlayerName(uuid);
            return null;
        }
        return name;
    }

    public String getName(UUID uuid) {
        String name = cache.get(uuid);
        if (name == null) {
            syncFetch(uuidList(uuid));
            return cache.get(uuid);
        }
        return name;
    }

    public void ensurePlayerName(UUID uuid) {
        if (cache.containsKey(uuid)) return;
        cache.put(uuid, "ERROR");
        asyncFetch(uuidList(uuid));
    }

    private void asyncFetch(final ArrayList<UUID> uuids) {
    	plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			@Override
			public void run() {
				syncFetch(uuids);
			}
		});
    }

    private void syncFetch(ArrayList<UUID> uuids) {
        final NameFetcher fetcher = new NameFetcher(uuids);
        try {
            cache.putAll(fetcher.call());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<UUID> uuidList(UUID uuid) {
        ArrayList<UUID> uuids = new ArrayList<UUID>();
        uuids.add(uuid);
        return uuids;
    }
    
	@EventHandler
	public void e(PostLoginEvent event) {
		cache.put(event.getPlayer().getUniqueId(), event.getPlayer().getName());
	}
    
}