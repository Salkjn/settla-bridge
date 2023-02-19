package de.settla.local.guilds;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CachedGuildList {

	private final LocalGuildModule guildModule;

	private final Map<UUID, CachedGuild> uniqueIdCache = new ConcurrentHashMap<>();
	private final Map<UUID, CachedUUID> playerIdCache = new HashMap<>();

	public CachedGuildList(LocalGuildModule guildModule) {
		this.guildModule = guildModule;
	}

	public CachedGuild getGuildByUniqueId(UUID uuid) {
		CachedGuild c = uniqueIdCache.get(uuid);
		
		if (c != null && c.isFresh())
			return c;
		
		final CachedGuild cache = (c == null) ? new CachedGuild() : c;
		uniqueIdCache.put(uuid, cache);
		
		if (cache.isDownloading()) {
			return cache;
		}
		
		cache.startDownload();
		
		guildModule.getGuildByUniqueId(uuid, guild -> {
//			System.out.println("GUILD-DOWNLOAD: GUILD " + msg + ": " + uuid);
			cache.update(guild.getGuild());
		});
		return cache;
	}

	public CachedUUID getGuildByPlayer(UUID uuid) {
		CachedUUID c = playerIdCache.get(uuid);
		
		if (c != null && c.isFresh())
			return c;
		
		final CachedUUID cache = (c == null) ? new CachedUUID() : c;
		playerIdCache.put(uuid, cache);
		
		if (cache.isDownloading()) {
			return cache;
		}
		
		cache.startDownload();
		
		guildModule.getGuildUniqueIdByPlayer(uuid, ub -> {
//			System.out.println("GUILD-DOWNLOAD: PLAYER " + uuid);
			cache.update(ub.getUniqueId());
		});
		return cache;
	}

}
