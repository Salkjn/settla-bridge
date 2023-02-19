package de.settla.local.npc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NpcEntity {

	private final Object lock = new Object();
	private final int viewDistance = 50;

	private Location location;
	private EntityType type;
	private Entity entity;
	private NpcHologramm hologramm;

	private final List<Function<Player, String>> lines = new ArrayList<>();

	private final Set<Player> watchers = new HashSet<>();

	public NpcEntity(Location location, EntityType type, List<Function<Player, String>> lines) {
		this.location = location;
		this.type = type;
		this.lines.addAll(lines);
		initPackets();
		hologramm = new NpcHologramm(this, location.clone().add(0, entity.getHeadHeight(), 0), lines);
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public EntityType getType() {
		return type;
	}

	public Entity getEntity() {
		return entity;
	}

	public NpcHologramm getHologramm() {
		return hologramm;
	}

	public int getId() {
		return entity.getId();
	}
	
	public boolean isInsideViewingDistance(Location check) {
		if (check.getWorld() == location.getWorld()) {
			return check.distanceSquared(location) <= viewDistance * viewDistance;
		} else
			return false;
	}
	
	public void removeWatcher(Player player) {
		synchronized (lock) {
			watchers.remove(player);
//			Iterator<Player> itr = watchers.iterator();
//			while (itr.hasNext()) {
//				Player p = itr.next();
//				if (p == null || !p.isOnline() || !isInsideViewingDistance(p.getLocation()) || p.getUniqueId().equals(player.getUniqueId())) {
//					itr.remove();
//					if (player != null && player.isOnline())
//						destroy(player);
//				} else {
//					
//				}
//			}
		}
	}
	
	public void clearWatchers(Collection<? extends Player> possible) {
		synchronized (lock) {
			
			// clear old players away!
			
			Iterator<Player> itr = watchers.iterator();
			while (itr.hasNext()) {
				Player player = itr.next();
				if (player == null || !player.isOnline() || !isInsideViewingDistance(player.getLocation())) {
					itr.remove();
					if (player != null && player.isOnline())
						destroy(player);
				} else {
					
				}
			}
			
			// add new players
			
			for (Player player : possible) {
				if (isInsideViewingDistance(player.getLocation()) && player.isOnline()) {
					if (watchers.add(player)) {
						if (isLivingEntity())
							sendPacket(packetPlayOutSpawnEntityLiving, player);
						else
							sendPacket(packetPlayOutSpawnEntity, player);
						hologramm.spawn(player);
					} else {
						//maybe update
						hologramm.updateLines(player);
					}
				}
			}
		}
	}
	
	private void destroy(Player player) {
		sendPacket(packetPlayOutEntityDestroy, player);
		hologramm.destroy(player);
	}
	
	public void destroy() {
		synchronized (lock) {
			watchers.stream().filter(watcher -> watcher != null && watcher.isOnline()).forEach(watcher -> destroy(watcher));
			watchers.clear();
		}
	}

	private void sendPacket(Packet<?> packet, Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	private PacketPlayOutSpawnEntity packetPlayOutSpawnEntity;
	private PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving;
	private PacketPlayOutEntityDestroy packetPlayOutEntityDestroy;

	private void setValue(Object obj, String name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
		}
	}

	public boolean isLivingEntity() {
		return entity instanceof EntityLiving;
	}
	
	@SuppressWarnings("deprecation")
	private void initPackets() {
		WorldServer s = ((CraftWorld) location.getWorld()).getHandle();
		
		entity = EntityTypes.createEntityByName(type.getName(), s);
		entity.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
		if (isLivingEntity()) {
			this.packetPlayOutSpawnEntityLiving = new PacketPlayOutSpawnEntityLiving((EntityLiving)entity);
			setValue(packetPlayOutSpawnEntityLiving, "j", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
			setValue(packetPlayOutSpawnEntityLiving, "k", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
		} else {
			this.packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(entity, 2, 1);
//			setValue(packetPlayOutSpawnEntity, "h", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
//			setValue(packetPlayOutSpawnEntity, "i", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
		}
		this.packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(new int[] { entity.getId() });
	}

}
