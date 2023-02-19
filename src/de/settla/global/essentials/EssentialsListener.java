package de.settla.global.essentials;

import java.util.Iterator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EssentialsListener implements Listener {
	
//	@EventHandler
//	public void e(PlayerDisconnectEvent event) {
//		final BaseComponent[] msg = new ComponentBuilder("§e> §6"+event.getPlayer().getName() + "§e hat das Netzwerk verlassen. (Online: " + (ProxyServer.getInstance().getOnlineCount()-1) + ")").color(ChatColor.YELLOW).create();
//		ProxyServer.getInstance().getServers().values().forEach(s -> s.getPlayers().forEach(p -> p.sendMessage(msg)));
//	}
	
	@EventHandler
	public void e(ServerConnectedEvent event) {
		ProxiedPlayer player = event.getPlayer();
		final String name = event.getServer().getInfo().getName();
		final BaseComponent[] msg = new ComponentBuilder("§e> §6" +player.getName() + "§e hat sich auf dem ").color(ChatColor.YELLOW).append(name).color(ChatColor.GOLD).append(" Server eingeloggt. (Online: " + ProxyServer.getInstance().getOnlineCount() + ")").color(ChatColor.YELLOW).create();
		ProxyServer.getInstance().getServers().values().stream().filter(s -> !s.getName().equalsIgnoreCase(name)).forEach(s -> s.getPlayers().forEach(p -> p.sendMessage(msg)));	
	}
	
	@EventHandler
	public void onPingEvent(ProxyPingEvent event) {
		PlayerInfo[] sample = new PlayerInfo[ProxyServer.getInstance().getPlayers().size()];
		Iterator<ProxiedPlayer> ite = ProxyServer.getInstance().getPlayers().iterator();
		int i = 0;
		while(ite.hasNext()) {
			ProxiedPlayer p = ite.next();
			sample[i] = new PlayerInfo(p.getName(), p.getUniqueId());
			i++;
		}
        event.getResponse().getPlayers().setSample(sample);
	}
	
}
