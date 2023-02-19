package de.settla.utilities.local.playerdata;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.entity.Player;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("GlobalPlayer")
public class LocalPlayer implements Storable {

	private final static Map<Class<? extends LocalData>, Function<UUID, ? extends LocalData>> playerDefaultData = new HashMap<>(); 
	
	public static <T extends LocalData> void addDefaultData(Function<UUID, T> function, Class<T> clazz) {
		playerDefaultData.put(clazz, function);
	}
	
	public <T extends LocalData> T getData(Class<T> clazz) {
		synchronized (lock) {
			retry();
			for (LocalData d : data) {
				if (clazz.isInstance(d)) {
					return clazz.cast(d);
				}
			}
			
			if (nodata.isEmpty()) {
				Function<UUID, ? extends LocalData> fun = playerDefaultData.get(clazz);
				if(fun == null)
					return null;
				setDirty(true);
				T d = clazz.cast(fun.apply(uuid));
				this.data.add(d);
				return d;
			} else {
				return null;
			}
		}
	}
	
	public <T extends LocalData> boolean existsData(Class<T> clazz) {
		return getData(clazz) != null;
	}
	
	private boolean hasPlayedBefore;
	
	private final UUID uuid;
	private final List<String> names;
	private final List<LocalData> data = new ArrayList<>();
	
	private final List<Map<String, Object>> nodata = new ArrayList<>();
	
	private final Object lock = new Object();
	private boolean dirty = true;
	
	public LocalPlayer(Player player) {
		this(player.getUniqueId(), player.getName());
	}
	
	public LocalPlayer(UUID uuid, String... names) {
		checkNotNull(uuid);
		this.uuid = uuid;
		this.names = new ArrayList<>();
		for (Function<UUID, ? extends LocalData> fun : playerDefaultData.values()) {
			this.data.add(fun.apply(uuid));
		}
		addNames(names);
	}
	
	@SuppressWarnings("unchecked")
	public LocalPlayer(Map<String, Object> map) {
		this.uuid = UUID.fromString((String) map.get("uuid"));
		this.names = (List<String>) map.get("names");
		this.hasPlayedBefore = (boolean) map.get("online");
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("data");
		list.forEach(m -> {
			LocalData d = deserialize(m, LocalData.class);
			if (d == null) {
				nodata.add(m);
			} else {
				data.add(d);
			}
		});
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("uuid", uuid.toString());
			map.put("names", names);
			map.put("online", hasPlayedBefore);
			List<Map<String, Object>> list = Stream.concat(data.stream().map(d -> d.serialize()), nodata.stream()).collect(Collectors.toList());
			map.put("data", list);
			return map;
		}
	}
	
	public void retry() {
		synchronized (lock) {
			if (!nodata.isEmpty()) {
				Iterator<Map<String, Object>> ite = nodata.iterator();
				while(ite.hasNext()) {
					LocalData a = deserialize(ite.next(), LocalData.class);
					if (a != null) {
						ite.remove();
						data.add(a);
					}
				}
			}
		}
	}
	
	public boolean hasPlayedBefore() {
		return hasPlayedBefore;
	}

	public void setPlayedBefore(boolean hasPlayedBefore) {
		synchronized (lock) {
			this.hasPlayedBefore = hasPlayedBefore;
			this.dirty = true;
		}
	}

	public boolean containsName(String name) {
		checkNotNull(name);
		String normal = name.toLowerCase();
		synchronized (lock) {
			for (String n : names) {
				if(n.equalsIgnoreCase(normal))
					return true;
			}
			return false;
		}
	}
	
	public boolean addName(String name) {
		checkNotNull(name);
		String normal = name.toLowerCase();
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
		String normal = name.toLowerCase();
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
	
	public UUID getUniqueId() {
		return uuid;
	}
	
}
