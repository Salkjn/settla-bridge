package de.settla.global.essentials;

import de.settla.global.GlobalPlugin;
import de.settla.utilities.module.Module;

public class EssentialsModule extends Module<GlobalPlugin> {

	public EssentialsModule(GlobalPlugin moduleManager) {
		super(moduleManager);
	}

	@Override
	public void onEnable() {
		this.getModuleManager().getProxy().getPluginManager().registerListener(this.getModuleManager(), new EssentialsListener());
	}
	
}
