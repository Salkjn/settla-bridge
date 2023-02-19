package de.settla.global;

import java.util.List;

import net.md_5.bungee.config.Configuration;

public class GlobalConfig {

	private final List<String> networkServers;
	
	public GlobalConfig(GlobalPlugin plugin) {
		Configuration config = plugin.loadConfig("global_config.yml");
		
		networkServers = config.getStringList("networkServers");

	}
	
	public boolean isNetworkServers(String server) {
		if(networkServers == null)
			return false;
		return networkServers.contains(server);
	}
	
}
