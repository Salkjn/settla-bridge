package de.settla.utilities.global;

import java.util.concurrent.TimeUnit;

import de.settla.global.GlobalPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

@FunctionalInterface
public interface ProxyRunnable extends Runnable {
	
	default ScheduledTask runAsync(){
		return ProxyServer.getInstance().getScheduler().runAsync(GlobalPlugin.getInstance(), this);
	}

	default ScheduledTask runAfter(long time, TimeUnit unit){
		return ProxyServer.getInstance().getScheduler().schedule(GlobalPlugin.getInstance(), this, time, unit);
	}

	default ScheduledTask runAfterEvery(long time, long repeat, TimeUnit unit){
		return ProxyServer.getInstance().getScheduler().schedule(GlobalPlugin.getInstance(), this, time, repeat, unit);
	}
}
