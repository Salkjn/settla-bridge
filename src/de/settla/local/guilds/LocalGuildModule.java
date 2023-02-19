package de.settla.local.guilds;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import de.settla.global.guilds.GuildBlock;
import de.settla.local.LocalPlugin;
import de.settla.utilities.sakko.protocol.SakkoProtocol;
import de.settla.utilities.storage.UniqueIdBlock;

public class LocalGuildModule extends de.settla.utilities.module.Module<LocalPlugin> implements Listener {

	private final SakkoProtocol protocol;
	
	private final CachedGuildList cache;
	
	public LocalGuildModule(LocalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
		this.cache = new CachedGuildList(this);
	}

	@Override
	public void onEnable() {
		getModuleManager().registerListener(this);
	}
	
	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}
	
	public CachedGuildList getCachedGuilds() {
		return cache;
	}
	
	public void getGuildByUniqueId(UUID uuid, Consumer<GuildBlock> consumer) {
		getSakkoProtocol().ask("get_guild_id", question -> question.put("uuid", uuid, UUID.class), answer -> {
			consumer.accept(answer.getStorableAnswer("data", GuildBlock.class));
		});
	}
	
	public void getGuildByPlayer(UUID uuid, Consumer<GuildBlock> consumer) {
		getSakkoProtocol().ask("get_guild_player", question -> question.put("uuid", uuid, UUID.class), answer -> {
			consumer.accept(answer.getStorableAnswer("data", GuildBlock.class));
		});
	}
	
	public void getGuildByLongName(String name, Consumer<GuildBlock> consumer) {
		getSakkoProtocol().ask("get_guild_long", question -> question.put("name", name, String.class), answer -> {
			consumer.accept(answer.getStorableAnswer("data", GuildBlock.class));
		});
	}
	
	public void getGuildByShortName(String name, Consumer<GuildBlock> consumer) {
		getSakkoProtocol().ask("get_guild_short", question -> question.put("name", name, String.class), answer -> {
			consumer.accept(answer.getStorableAnswer("data", GuildBlock.class));
		});
	}
	
	public void getGuildUniqueIdByPlayer(UUID uuid, Consumer<UniqueIdBlock> consumer) {
		getSakkoProtocol().ask("get_guild_id_player", question -> question.put("uuid", uuid, UUID.class), answer -> {
			consumer.accept(answer.getStorableAnswer("data", UniqueIdBlock.class));
		});
	}
	
	public void getGuildUniqueIdByLongName(String name, Consumer<UniqueIdBlock> consumer) {
		getSakkoProtocol().ask("get_guild_id_long", question -> question.put("name", name, String.class), answer -> {
			consumer.accept(answer.getStorableAnswer("data", UniqueIdBlock.class));
		});
	}
	
	public void getGuildUniqueIdByShortName(String name, Consumer<UniqueIdBlock> consumer) {
		getSakkoProtocol().ask("get_guild_id_short", question -> question.put("name", name, String.class), answer -> {
			consumer.accept(answer.getStorableAnswer("data", UniqueIdBlock.class));
		});
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerSpawnLocationEvent event) {
		getCachedGuilds().getGuildByPlayer(event.getPlayer().getUniqueId());
	}
	
	
}
