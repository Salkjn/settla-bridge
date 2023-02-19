package de.settla.global.cloud;

import de.settla.global.GlobalPlugin;
import de.settla.utilities.module.Module;
import de.settla.utilities.sakko.protocol.SakkoProtocol;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

public class GlobalCloudModule extends Module<GlobalPlugin> {

	private final SakkoProtocol protocol;
	
	public GlobalCloudModule(GlobalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
		initAnswer();
	}
	
	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}
	
	private void initAnswer() {
		
		getSakkoProtocol().answer("cloud_command", answer -> {
			
			String sender = answer.getQuestion("sender", String.class);
			String cmd = answer.getQuestion("cmd", String.class);
			
			CommandSender s = null;
			
			if (sender.equalsIgnoreCase("+CONSOLE+")) {
				s = ProxyServer.getInstance().getConsole();
			} else {
				s = ProxyServer.getInstance().getPlayer(sender);
			}
			
			if (s == null) {
				return answer.answer().put("success", false, Boolean.class);
			} else {
				ProxyServer.getInstance().getPluginManager().dispatchCommand(s, cmd);
				return answer.answer().put("success", true, Boolean.class);
			}
		});
		
	}
	
}
