package de.settla.global.guilds;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.settla.utilities.module.Module;
import de.settla.global.GlobalPlugin;
import de.settla.utilities.sakko.protocol.SakkoProtocol;
import de.settla.utilities.storage.Database;
import de.settla.utilities.storage.Storage;
import de.settla.utilities.storage.UniqueIdBlock;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GuildGlobalModule extends Module<GlobalPlugin> {

	private final SakkoProtocol protocol;
	private Storage<GlobalGuildList> guildList;

	public GuildGlobalModule(GlobalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
		initAnswers();
	}

	@Override
	public void onEnable() {
		Database<GlobalGuildList> database = new Database<>("guilds", new File("plugins/SettlaBridge/guilds.data"),
				n -> new GlobalGuildList(), GlobalGuildList.class);
		guildList = new Storage<>(database);
		ProxyServer.getInstance().getScheduler().schedule(GlobalPlugin.getInstance(), guildList, 0, 5 * 60,
				TimeUnit.SECONDS);

		getModuleManager().registerCommand(new GuildCommand(this, "guild", "g"));
	}

	@Override
	public void onDisable() {
		guildList.run();
	}

	public GlobalGuildList getGuildList() {
		return guildList.object();
	}

	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}

	private void initAnswers() {
		getSakkoProtocol().answer("get_guild_id", answer -> {
			UUID uuid = answer.getQuestion("uuid", UUID.class);
			return answer.answer().put("data", new GuildBlock(getGuildList().getCache().getGuildByUniqueId(uuid)));
		});
		getSakkoProtocol().answer("get_guild_player", answer -> {
			UUID uuid = answer.getQuestion("uuid", UUID.class);
			return answer.answer().put("data", new GuildBlock(getGuildList().getCache().getGuildByPlayer(uuid)));
		});
		getSakkoProtocol().answer("get_guild_long", answer -> {
			String name = answer.getQuestion("name", String.class);
			return answer.answer().put("data", new GuildBlock(getGuildList().getCache().getGuildByLongName(name)));
		});
		getSakkoProtocol().answer("get_guild_short", answer -> {
			String name = answer.getQuestion("name", String.class);
			return answer.answer().put("data", new GuildBlock(getGuildList().getCache().getGuildByShortName(name)));
		});

		getSakkoProtocol().answer("get_guild_id_player", answer -> {
			UUID uuid = answer.getQuestion("uuid", UUID.class);
			Guild g = getGuildList().getCache().getGuildByPlayer(uuid);
			return answer.answer().put("data", new UniqueIdBlock(g == null ? null : g.id()));
		});
		getSakkoProtocol().answer("get_guild_id_long", answer -> {
			String name = answer.getQuestion("name", String.class);
			Guild g = getGuildList().getCache().getGuildByLongName(name);
			return answer.answer().put("data", new UniqueIdBlock(g == null ? null : g.id()));
		});
		getSakkoProtocol().answer("get_guild_id_short", answer -> {
			String name = answer.getQuestion("name", String.class);
			Guild g = getGuildList().getCache().getGuildByShortName(name);
			return answer.answer().put("data", new UniqueIdBlock(g == null ? null : g.id()));
		});
	}

	public void sendMessage(Guild guild, BaseComponent... message) {

		guild.getOwner().forEach(uuid -> {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
			if (player != null)
				player.sendMessage(message);
		});
		guild.getHelper().forEach(uuid -> {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
			if (player != null)
				player.sendMessage(message);
		});
		guild.getMember().forEach(uuid -> {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
			if (player != null)
				player.sendMessage(message);
		});

	}

}
