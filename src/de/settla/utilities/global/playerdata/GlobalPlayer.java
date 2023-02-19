package de.settla.utilities.global.playerdata;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Serial("GlobalPlayer")
public class GlobalPlayer implements Storable {

	private final static Map<Class<? extends GlobalData>, Function<UUID, ? extends GlobalData>> playerDefaultData = new HashMap<>(); 
	
	public static <T extends GlobalData> void addDefaultData(Function<UUID, T> function, Class<T> clazz) {
		playerDefaultData.put(clazz, function);
	}
	
	public <T extends GlobalData> T getData(Class<T> clazz) {
		synchronized (lock) {
			for (GlobalData d : data) {
				if (clazz.isInstance(d)) {
					return clazz.cast(d);
				}
			}
			Function<UUID, ? extends GlobalData> fun = playerDefaultData.get(clazz);
			if(fun == null)
				return null;
			setDirty(true);
			T d = clazz.cast(fun.apply(uuid));
			this.data.add(d);
			return d;
		}
	}
	
	public <T extends GlobalData> boolean existsData(Class<T> clazz) {
		return getData(clazz) != null;
	}
	
	private final UUID uuid;
	private final List<String> names;
	private final List<GlobalData> data = new ArrayList<>();
	
	private final Object lock = new Object();
	private boolean dirty = true;
	
	public GlobalPlayer(ProxiedPlayer player) {
		this(player.getUniqueId(), player.getName());
	}
	
	public GlobalPlayer(UUID uuid, String ...names) {
		checkNotNull(uuid);
		this.uuid = uuid;
		this.names = new ArrayList<>();
		for (Function<UUID, ? extends GlobalData> fun : playerDefaultData.values()) {
			this.data.add(fun.apply(uuid));
		}
		addNames(names);
	}
	
	@SuppressWarnings("unchecked")
	public GlobalPlayer(Map<String, Object> map) {
		this.uuid = UUID.fromString((String) map.get("uuid"));
		this.names = (List<String>) map.get("names");
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("data");
		list.forEach(m -> data.add(deserialize(m, GlobalData.class)));
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("uuid", uuid.toString());
			map.put("names", names);
			List<Map<String, Object>> list = data.stream().map(d -> d.serialize()).collect(Collectors.toList());
			map.put("data", list);
			return map;
		}
	}
	
	public boolean containsIgnoreCaseName(String name) {
		checkNotNull(name);
//		String normal = name.toLowerCase();
		String normal = name;
		synchronized (lock) {
			for (String n : names) {
				if(n.equalsIgnoreCase(normal))
					return true;
			}
			return false;
		}
	}
	
	public boolean containsName(String name) {
		checkNotNull(name);
//		String normal = name.toLowerCase();
		String normal = name;
		synchronized (lock) {
			for (String n : names) {
				if(n.equals(normal))
					return true;
			}
			return false;
		}
	}
	
	public boolean addName(String name) {
		checkNotNull(name);
//		String normal = name.toLowerCase();
		String normal = name;
		if(!containsName(normal)) {
			synchronized (lock) {
				setDirty(true);
				return names.add(normal);
			}
		}
		return false;
	}
	
	public void addNames(String... names) {
		for (String name : names) {
			addName(name);
		}
	}
	
	public boolean removeName(String name) {
		checkNotNull(name);
//		String normal = name.toLowerCase();
		String normal = name;
		synchronized (lock) {
			setDirty(true);
			return names.remove(normal);
		}
	}
	
	public UUID id() {
		return uuid;
	}
	
	public String name() {
		synchronized (lock) {
			return names.isEmpty() ? null : names.get(names.size() - 1);
		}
	}
	
	public String names() {
		synchronized (lock) {
			return names.toString();
		}
	}
	
	public void clearNames() {
		synchronized (lock) {
			setDirty(true);
			names.clear();
		}
	}

	@Override
	public boolean isDirty() {
		synchronized (lock) {
			return dirty || data.stream().filter(d -> d.isDirty()).findFirst().orElse(null) != null;
		}
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		data.forEach(data -> data.setDirty(dirty));
	}
	
	private ProxiedPlayer chachedPlayer;
	
	public ProxiedPlayer getProxiedPlayer() {
		if(chachedPlayer == null)
			chachedPlayer = ProxyServer.getInstance().getPlayer(uuid);
		if(chachedPlayer != null)
			addName(chachedPlayer.getName());
		return chachedPlayer;
	}
	
	public boolean isOnline() {
		return getProxiedPlayer() != null;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
}
