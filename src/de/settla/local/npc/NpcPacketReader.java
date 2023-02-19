package de.settla.local.npc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.settla.local.LocalPlugin;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;

public class NpcPacketReader {
	
	private static final Map<UUID, NpcPacketReader> packetReaders = new HashMap<>();
	private static final Object lock = new Object();
	
	private Player player;
	private Channel channel;
	
	public static NpcPacketReader getOrCreatePacketReader(Player player) {
		synchronized (lock) {
			NpcPacketReader reader = packetReaders.get(player.getUniqueId());
			if (reader == null) {
				reader = new NpcPacketReader(player);
				packetReaders.put(player.getUniqueId(), reader);
			}
			reader.player = player;
			reader.channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
			return reader;
		}
	}

	public static void reinjectAll() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			NpcPacketReader reader = NpcPacketReader.getOrCreatePacketReader(player);
			reader.uninject();
			reader.inject();
		});
	}
	
	private NpcPacketReader(Player player) {
		this.player = player;
	}

	public void inject() {
		CraftPlayer player = (CraftPlayer) this.player;
		channel = player.getHandle().playerConnection.networkManager.channel;
		channel.pipeline().addAfter("decoder", "PacketInjectorNPC2", new MessageToMessageDecoder<Packet<?>>() {
			@Override
			protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2)
					throws Exception {
				arg2.add(packet);
				readPackets(packet);
			}
		});
	}

	public void uninject() {
		CraftPlayer player = (CraftPlayer) this.player;
		channel = player.getHandle().playerConnection.networkManager.channel;
		if (channel != null && channel.pipeline() != null && channel.pipeline().get("PacketInjectorNPC2") != null)
			channel.pipeline().remove("PacketInjectorNPC2");
	}

	private void readPackets(Packet<?> packet) {
		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
			int id = (Integer) getValue(packet, "a");
			String action = getValue(packet, "action").toString();
			if(action.equalsIgnoreCase("INTERACT_AT"))
				return;
			Npc npc = LocalPlugin.getInstance().getModule(NpcModule.class).getNpcByEntityId(id);
			if(npc != null) {
				
				if (action.equalsIgnoreCase("ATTACK")) {
					npc.getAttack().accept(player);
				} else if (action.equalsIgnoreCase("INTERACT")) {
					npc.getInteract().accept(player);
				}
			}
		}
	}

	private Object getValue(Object obj, String name) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
		}
		return null;
	}
}
