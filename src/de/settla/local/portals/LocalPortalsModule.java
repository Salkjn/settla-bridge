package de.settla.local.portals;

import org.bukkit.Bukkit;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.region.Galaxy;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.module.Module;
import de.settla.utilities.sakko.protocol.SakkoProtocol;

public class LocalPortalsModule extends Module<LocalPlugin> {

	public static final String GALAXY_NAME = "portals";
	private final SakkoProtocol protocol;
	
	public LocalPortalsModule(LocalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
	}

	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}

	private void registerSomeThings() {
		Galaxy galaxy = getModuleManager().getModule(Universe.class).getGalaxy(GALAXY_NAME);
		
		Bukkit.getWorlds().forEach(world -> {
			galaxy.createWorld(world.getName(), new PortalsWildness());
		});
		
	}
	
	@Override
	public void onEnable() {
		getModuleManager().getModule(Universe.class).registerGalaxy(GALAXY_NAME);
		getModuleManager().getModule(Universe.class).addToWaitingActionList(() -> registerSomeThings());
		getModuleManager().registerCommand(new PortalCommand("portals"));
	}
	
}

