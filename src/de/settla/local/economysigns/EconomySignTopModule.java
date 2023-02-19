package de.settla.local.economysigns;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import de.settla.economy.accounts.PurseHandler;
import de.settla.local.LocalPlugin;
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
