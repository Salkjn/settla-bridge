package de.settla.global.beam;

import java.util.UUID;

public class BeamRequest {

	private final long maximalTimeDistance = 1000 * 20;
	private final UUID player, target;
	private long time;
	
	public BeamRequest(UUID player, UUID target) {
		super();
		this.player = player;
		this.target = target;
		this.time = System.currentTimeMillis();
	}

	public UUID getPlayer() {
		return player;
	}

	public UUID getTarget() {
		return target;
	}
	
	public void updateTime() {
		this.time = System.currentTimeMillis();
	}
	
	public boolean isAcceptable() {
		return time + maximalTimeDistance > System.currentTimeMillis();
	}
	
}
