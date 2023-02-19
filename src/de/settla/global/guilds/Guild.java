package de.settla.global.guilds;

import java.util.Map;
import java.util.UUID;

import de.settla.global.guilds.groups.PlayerGroup;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.StaticParser;
import de.settla.utilities.storage.Storable;

@Serial("Guild")
public class Guild implements Storable {
	
	private final Object lock = new Object();
	private final PlayerGroup owner, helper, member;
	private final GuildName name;
	private final UUID uuid;
	
	private final PlayerGroup invites = new PlayerGroup();
	
	public Guild(UUID uuid, String longName, String shortName) {
		this.uuid = uuid;
		this.name = new GuildName(longName, shortName);
		this.owner = new PlayerGroup();
		this.helper = new PlayerGroup();
		this.member = new PlayerGroup();
	}
	
	public Guild(Map<String, Object> map) {
		this.uuid = StaticParser.parse((String)map.get("u"), UUID.class);
		this.name = deserialize(map.get("n"), GuildName.class);
		this.owner = deserialize(map.get("o"), PlayerGroup.class);
		this.helper = deserialize(map.get("h"), PlayerGroup.class);
		this.member = deserialize(map.get("m"), PlayerGroup.class);
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("u", StaticParser.unparse(uuid, UUID.class));
			map.put("n", name.serialize());
			map.put("o", owner.serialize());
			map.put("h", helper.serialize());
			map.put("m", member.serialize());
			return map;
		}
	}
	
	@Override
	public boolean isDirty() {
		synchronized (lock) {
			return member.isDirty() || helper.isDirty() || owner.isDirty() || name.isDirty();
		}
	}
	
	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			member.setDirty(dirty);
			helper.setDirty(dirty);
			owner.setDirty(dirty);
			name.setDirty(dirty);
		}
	}

	public PlayerGroup getOwner() {
		return owner;
	}

	public PlayerGroup getHelper() {
		return helper;
	}

	public PlayerGroup getMember() {
		return member;
	}

	public PlayerGroup getInvites() {
		return invites;
	}
	
	public GuildName getName() {
		return name;
	}

	public UUID id() {
		return uuid;
	}
	
	public boolean isMemberOfGuild(UUID uuid) {
		return getMember().contains(uuid) || getHelper().contains(uuid) || getOwner().contains(uuid);
	}
	
	public int getMemberSize() {
		return getMember().size() + getHelper().size() + getOwner().size();
	}
	
}
