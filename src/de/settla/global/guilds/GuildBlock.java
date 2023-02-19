package de.settla.global.guilds;

import java.util.Map;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("GuildBlock")
public class GuildBlock implements Storable {
	
	private final Guild guild;
	
	public GuildBlock(Guild guild) {
		this.guild = guild;
	}
	
	public GuildBlock(Map<String, Object> map) {
		Object o = map.get("guild");
		if(o != null)
			this.guild = deserialize(o, Guild.class);
		else
			this.guild = null;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("guild", guild == null ? null : guild.serialize());
		return map;
	}

	public Guild getGuild() {
		return guild;
	}
	
}
