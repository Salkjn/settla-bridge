package de.settla.global.commands;

import java.util.Map;

import de.settla.utilities.global.TextBuilder;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.PlayerCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SpawnCommand extends PlayerCommand {

	public SpawnCommand() {
		super("lobby", "hub", "l");
	}

	@Override
	protected void execute(ProxiedPlayer player, ArgumentParser ap) {
		
		Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();

		ServerInfo server = (ServerInfo) servers.get("settla");
		
		if (server == null) {
			new TextBuilder().title("Spawn").text("Dieser Server wird derzeit restartet.").send(player);
		} else if (!server.canAccess(player)) {
			new TextBuilder().title("Spawn").text("Der Server befindet sich derzeit in Wartungsarbeiten.").send(player);
		} else {
			
			ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "warp Spawn");
			
		}
	}

	
}
