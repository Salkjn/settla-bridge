package de.settla.global.guilds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("GlobalGuildList")
public class GlobalGuildList implements Storable {

	private final List<Guild> guilds = new ArrayList<>();
	private final Object lock = new Object();
	
	private final CachedGlobalGuildList cache = new CachedGlobalGuildList(this);
	
	public GlobalGuildList() {
		
	}
	
	@SuppressWarnings("unchecked")
	public GlobalGuildList(Map<String, Object> map) {
		((List<Map<String, Object>>)map.get("guilds")).stream().forEach(s -> guilds.add(deserialize(s, Guild.class)));
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("guilds", guilds.stream().map(g -> g.serialize()).collect(Collectors.toList()));
			return map;
		}	
	}
	
	@Override
	public boolean isDirty() {
		synchronized (lock) {
			for (Guild globalGuild : guilds) {
				if(globalGuild.isDirty())
					return true;
			}
			return false;
		}
	}
	
	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			guilds.forEach(g -> g.setDirty(dirty));
		}
	}
	
	public UUID generateUniqueId() {
		UUID id = null;
		do {
			id = UUID.randomUUID();
		} while(getGuildByUniqueId(id) != null);
		return id;
	}
	
	public Guild getGuildByUniqueId(UUID uuid) {
		synchronized (lock) {
			return guilds.stream().filter(g -> g.id().equals(uuid)).findFirst().orElse(null);
		}
	}
	
	public Guild getGuildByLongName(String name) {
		synchronized (lock) {
			return guilds.stream().filter(g -> g.getName().isLongName(name)).findFirst().orElse(null);
		}
	}
	
	public Guild getGuildByShortName(String name) {
		synchronized (lock) {
			return guilds.stream().filter(g -> g.getName().isShortName(name)).findFirst().orElse(null);
		}
	}
	
	public Guild getGuildByPlayer(UUID uuid) {
		synchronized (lock) {
			return guilds.stream().filter(g -> g.getOwner().contains(uuid) || g.getMember().contains(uuid) || g.getHelper().contains(uuid)).findFirst().orElse(null);
		}
	}
	
	public void forEach(Consumer<List<Guild>> consumer) {
		synchronized (lock) {
			consumer.accept(guilds);
		}
	}
	
	public boolean removeGuild(Guild guild) {
		synchronized (lock) {
			cache.removeGuild(guild);
			boolean bool = guilds.remove(guild);
			setDirty(true);
			return bool;
		}
	}
	
	public Guild createNewGuild() {
		synchronized (lock) {
			Guild guild = new Guild(generateUniqueId(), null, null);
			guild.setDirty(true);
			guilds.add(guild);
			return guild;
		}
	}

	public CachedGlobalGuildList getCache() {
		return cache;
	}
	
}
