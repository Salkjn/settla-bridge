package de.settla.global.guilds;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CachedGlobalGuildList {
	
	private final Object lock = new Object();
	private final GlobalGuildList guilds;
	
	private final Map<String, Guild> longNameCache = new HashMap<>();
	private final Map<String, Guild> shortNameCache = new HashMap<>();
	private final Map<UUID, Guild> uniqueIdCache = new HashMap<>();
	private final Map<UUID, Guild> playerCache = new HashMap<>();
	
	public CachedGlobalGuildList(GlobalGuildList guilds) {
		this.guilds = guilds;
	}
	
	public Guild getGuildByUniqueId(UUID uuid) {
		synchronized (lock) {
			Guild guild = uniqueIdCache.get(uuid);
			if(guild == null) {
				guild = guilds.getGuildByUniqueId(uuid);
				uniqueIdCache.put(uuid, guild);
			}
			return guild;
		}
	}
	
	public Guild getGuildByShortName(String name) {
		synchronized (lock) {
			Guild guild = shortNameCache.get(name);
			if(guild == null) {
				guild = guilds.getGuildByShortName(name);
				shortNameCache.put(name, guild);
			} else {
				if(!guild.getName().isShortName(name)) {
					shortNameCache.remove(name);
					guild = guilds.getGuildByShortName(name);
					if(guild != null)
						shortNameCache.put(guild.getName().getShortName(), guild);
				}
			}
			return guild;
		}
	}
	
	public Guild getGuildByLongName(String name) {
		synchronized (lock) {
			Guild guild = longNameCache.get(name);
			if(guild == null) {
				guild = guilds.getGuildByLongName(name);
				longNameCache.put(name, guild);
			} else {
				if(!guild.getName().isLongName(name)) {
					longNameCache.remove(name);
					guild = guilds.getGuildByLongName(name);
					if(guild != null)
						longNameCache.put(guild.getName().getLongName(), guild);
				}
			}
			return guild;
		}
	}
	
	public Guild getGuildByPlayer(UUID uuid) {
		synchronized (lock) {
			Guild guild = playerCache.get(uuid);
			if(guild == null) {
				guild = guilds.getGuildByPlayer(uuid);
				playerCache.put(uuid, guild);
			} else {
				if(!(guild.getMember().contains(uuid) || guild.getHelper().contains(uuid) || guild.getOwner().contains(uuid))) {
					playerCache.remove(uuid);
					guild = guilds.getGuildByPlayer(uuid);
					if(guild != null)
						playerCache.put(uuid, guild);
				}
			}
			return guild;
		}
	}
	
	public void removeGuild(Guild guild) {
		synchronized (lock) {
			uniqueIdCache.remove(guild.id());
			longNameCache.remove(guild.getName().getLongName());
			shortNameCache.remove(guild.getName().getShortName());
			guild.getHelper().forEach(uuid -> playerCache.remove(uuid));
			guild.getMember().forEach(uuid -> playerCache.remove(uuid));
			guild.getOwner().forEach(uuid -> playerCache.remove(uuid));
		}
	}
	
	public void clear() {
		synchronized (lock) {
			longNameCache.clear();
			shortNameCache.clear();
			uniqueIdCache.clear();
			playerCache.clear();
		}
	}
	
}
