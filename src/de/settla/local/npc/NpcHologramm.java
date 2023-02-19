package de.settla.local.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NpcHologramm {
	
	private final NpcEntity npcEntity;
	private final List<HologrammLine> lines = new ArrayList<>();

	public NpcHologramm(NpcEntity kitEntity, Location location, List<Function<Player, String>> lines) {
		super();
		this.npcEntity = kitEntity;
		if (lines.size() > 0) {
			int i = lines.size() - 1;
			for (Function<Player, String> line : lines) {
				if (line != null) {
					this.lines.add(new HologrammLine(location.clone().add(0, i * 0.25, 0), line));
				}
				i--;
			}
		}
	}

	public void spawn(Player player) {
		lines.forEach(line -> line.spawn(player));
	}

	public void destroy(Player player) {
		lines.forEach(line -> line.destroy(player));
	}

	public void updateLines(Player player) {
		lines.forEach(line -> line.updateLine(player));
	}

	public List<HologrammLine> getLines() {
		return lines;
	}

	public NpcEntity getNpcEntity() {
		return npcEntity;
	}

	public static class HologrammLine {

		private final Object lock = new Object();

		private Location location;
		private EntityArmorStand entity;
		private Function<Player, String> line;
		private final Map<UUID, String> backup = new HashMap<>();
//		private String backup;
		
		public HologrammLine(Location location, Function<Player, String> line) {
			super();
			this.location = location;
			this.line = line;
			initPackets();
		}

		public void updateLine(List<Player> players) {
			synchronized (lock) {
				players.forEach(player -> updateLine(player));
			}
		}

		public Function<Player, String> getLine() {
			return line;
		}

		public boolean hasBackup(Player player) {
			return backup != null;
		}
		
		public void updateLine(Player player) {
			String text = line.apply(player);
			String backup = this.backup.get(player.getUniqueId());
			if(text != null && !text.equals(backup)) {
				synchronized (lock) {
					
					//System.out.println("UPDATE " + backup +" -> "+ text + "   " + player);
					
					if (backup == null) {
						spawn(player);
					} else {
						this.backup.put(player.getUniqueId(), text);
						DataWatcher dataWatcher = new DataWatcher(null);
						dataWatcher.a(2, text);
						PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entity.getId(), dataWatcher,
								true);
						sendPacket(packet, player);
					}
				}
			}
			
			if (text == null) {
				destroy(player);
			}
			
		}

		public void update(Player player) {
			sendPacket(packetPlayOutEntityTeleport, player);
		}

		public void destroy(Player player) {
			sendPacket(packetPlayOutEntityDestroy, player);
			backup.put(player.getUniqueId(), null);
		}

		private void sendPacket(Packet<?> packet, Player player) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}

		public void spawn(Player player) {
			synchronized (lock) {
				if (line != null) {
					String text = line.apply(player);
					if (text != null) {
						backup.put(player.getUniqueId(), text);
						entity.setCustomName(text);
						sendPacket(packetPlayOutSpawnEntityLiving, player);
					}
				}
			}
		}

		private PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving;
		private PacketPlayOutEntityTeleport packetPlayOutEntityTeleport;
		private PacketPlayOutEntityDestroy packetPlayOutEntityDestroy;

		private void initPackets() {
			WorldServer s = ((CraftWorld) location.getWorld()).getHandle();
			entity = new EntityArmorStand(s);
			entity.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
			entity.setCustomNameVisible(true);
			entity.setInvisible(true);

			ArmorStand a = (ArmorStand) entity.getBukkitEntity();
			a.setMarker(true);

			this.packetPlayOutSpawnEntityLiving = new PacketPlayOutSpawnEntityLiving(entity);
			this.packetPlayOutEntityTeleport = new PacketPlayOutEntityTeleport(entity);
			this.packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(new int[] { entity.getId() });
		}

	}
}
