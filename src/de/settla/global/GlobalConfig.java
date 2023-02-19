package de.settla.global;

import java.util.List;

import net.md_5.bungee.config.Configuration;

public class GlobalConfig {

	private final List<String> sidebarServers;
	private final List<String> beamServers;
	
	public GlobalConfig(GlobalPlugin plugin) {
		Configuration config = plugin.loadConfig("global_config.yml");
		
		sidebarServers = config.getStringList("sidebarServers");
		beamServers = config.getStringList("beamServers");
		
	}

	public boolean isSidebarServer(String server) {
		if(sidebarServers == null)
			return false;
		return sidebarServers.contains(server);
	}
	
	public boolean isBeamServer(String server) {
		if(beamServers == null)
			return false;
		return beamServers.contains(server);
	}
	
}
