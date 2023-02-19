package de.settla.local.cloud;

import java.util.function.Consumer;

import de.settla.local.LocalPlugin;
import de.settla.utilities.module.Module;
import de.settla.utilities.sakko.protocol.SakkoProtocol;

public class LocalCloudModule extends Module<LocalPlugin> {

	private final SakkoProtocol protocol;
	
	public static final String CONSOLE = "+CONSOLE+";
	
	public LocalCloudModule(LocalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
	}

	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}

	public void dispatchBungeeCommand(String sender, String cmd, Consumer<Boolean> result) {
		
		getSakkoProtocol().ask("cloud_command", question -> question.put("cmd", cmd, String.class).put("sender", sender, String.class), answer -> {
			boolean success = answer.getAnswer("success", Boolean.class);
			result.accept(success);
		});
		
	}
	
	@Override
	public void onEnable() {
		getModuleManager().registerCommand(new LocalCloudCommand());
	}
	
}
