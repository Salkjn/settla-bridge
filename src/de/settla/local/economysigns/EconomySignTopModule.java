package de.settla.local.economysigns;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import de.settla.economy.accounts.GuildAccountHandler;
import de.settla.economy.accounts.HeadHunterAccountHandler;
import de.settla.economy.accounts.KillsAccountHandler;
import de.settla.economy.accounts.PurseHandler;
import de.settla.local.LocalPlugin;
import de.settla.local.guilds.CachedGuild;
import de.settla.local.guilds.LocalGuildModule;
import de.settla.utilities.module.Module;
import de.settla.utilities.module.ModuleManager;

public class EconomySignTopModule extends Module<LocalPlugin> implements ModuleManager {

	private final Map<String, Module<?>> modules = new HashMap<>();

	public EconomySignTopModule(LocalPlugin moduleManager) {
		super(moduleManager);
	}

	@Override
	public void onPreEnable() {
		
		ConfigurationSerialization.registerClass(EconomySign.class);

		getModuleManager().registerCommand(new EconomySignCommand("headhunter", "purse", "kills", "guild"));

		modules.put("purse",
				new EconomySignTop<PurseHandler>(this, "purse", PurseHandler.class,
						(tuple, sign) -> new String[] { "#" + sign.getRank(), tuple != null ? tuple.getX() : "???",
								tuple != null ? String.valueOf(tuple.getY()) + "$" : "???", "" },
						str -> Bukkit.getOfflinePlayer(UUID.fromString(str)).getName()));
		
		modules.put("headhunter",
				new EconomySignTop<HeadHunterAccountHandler>(this, "headhunter", HeadHunterAccountHandler.class,
						(tuple, sign) -> new String[] { "#" + sign.getRank(), tuple != null ? tuple.getX() : "???",
								tuple != null ? String.valueOf(tuple.getY()) + "$" : "???", "" }, 
						str -> Bukkit.getOfflinePlayer(UUID.fromString(str)).getName()));
		
		modules.put("kills",
				new EconomySignTop<KillsAccountHandler>(this, "kills", KillsAccountHandler.class,
						(tuple, sign) -> new String[] { "#" + sign.getRank(), tuple != null ? tuple.getX() : "???",
								tuple != null ? String.valueOf(tuple.getY()) + "$" : "???", "" },
						str -> Bukkit.getOfflinePlayer(UUID.fromString(str)).getName()));
		
		
		modules.put("guild",
				new EconomySignTop<GuildAccountHandler>(this, "guild", GuildAccountHandler.class,
						(tuple, sign) -> new String[] { "#" + sign.getRank(), tuple != null ? tuple.getX() : "???",
								tuple != null ? String.valueOf(tuple.getY()) + "$" : "???", "" },
						str -> {
							UUID uuid = UUID.fromString(str);
							CachedGuild cachedGuild = getModuleManager().getModule(LocalGuildModule.class).getCachedGuilds().getGuildByUniqueId(uuid);
							if(cachedGuild.isDownloaded() && cachedGuild.getGuild() != null)
								return cachedGuild.getGuild().getName().getShortName();
							return "???";
						}));
		
	}

	@Override
	public void onEnable() {
		enable();
	}

	@Override
	public void onDisable() {
		disable();
	}

	@Override
	public <M extends Module<?>> M getModule(Class<M> clazz) {
		return clazz.cast(modules.get(clazz));
	}

	@Override
	public void disable() {
		modules.values().forEach(m -> m.onDisable());
	}

	@Override
	public void enable() {
		modules.values().forEach(m -> m.onPreEnable());
		modules.values().forEach(m -> m.onEnable());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Module<?>> void modules(Consumer<M> consumer) {
		modules.values().forEach((Consumer<? super Module<?>>) consumer);
	}

}
