package de.settla.local.adminshop;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.settla.utilities.local.Utils;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.WorldServer;

@SerializableAs("SHOPSIGN")
public class ShopSign implements ConfigurationSerializable {

	private final int viewDistance = 50;
	private final Object lock = new Object();

	private final Location location;
	private ItemStack itemStack;
	private final double price;
	private final boolean buyable;
	private EntityItem entity;
	private PacketPlayOutSpawnEntity packetPlayOutSpawnEntity;
	private PacketPlayOutEntityMetadata packetPlayOutEntityMetadata;
	private PacketPlayOutEntityVelocity packetPlayOutEntityVelocity;
	private PacketPlayOutEntityDestroy packetPlayOutEntityDestroy;

	private final Set<Player> watchers = new HashSet<>();

	public ShopSign(Location location, ItemStack itemStack, double price, boolean buyable) {
		super();
		this.location = location;
		this.itemStack = itemStack;
		this.price = price;
		this.buyable = buyable;
		this.initPackets();
	}

	public ShopSign(Map<String, Object> map) {
		this.location = (Location) map.get("location");
		this.itemStack = (ItemStack) map.get("itemstack");
		this.price = (double) map.get("price");
		this.buyable = (boolean) map.get("buyable");
		this.initPackets();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("location", location);
		map.put("itemstack", itemStack);
		map.put("price", price);
		map.put("buyable", buyable);
		return map;
	}

	public Location getLocation() {
		return location;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
		entity.setItemStack(CraftItemStack.asNMSCopy(getItemStack()));
		this.packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(this.entity.getId(),
				this.entity.getDataWatcher(), true);
	}

	public double getPrice() {
		return price;
	}

	public boolean isBuyable() {
		return buyable;
	}

	public double getPlayerPrice(Player player) {
		if (isBuyable()) {
			int rabatt = Utils.getTopPermission(player, "adminshop.");
			return getPrice() * Utils.wrapper.backward((long) (100 - rabatt));
		} else {
			return getPrice();
		}
	}

	public boolean isInsideViewingDistance(Location check) {
		if (check.getWorld() == location.getWorld()) {
			return check.distanceSquared(location) <= viewDistance * viewDistance;
		} else
			return false;
	}

	public boolean isBlockSign() {
		Block b = getLocation().getBlock();
		if (b != null) {
			if (b.getState() instanceof Sign) {
				return true;
			}
		}
		return false;
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
				}
			}

			// add new players

			for (Player player : possible) {
				if (isInsideViewingDistance(player.getLocation()) && player.isOnline()) {
					if (watchers.add(player))
						spawn(player);
					updateSign(player);
				}
			}
		}
	}

	private void destroy(Player player) {
		clearSign(player);
		sendPacket(packetPlayOutEntityDestroy, player);
	}

	private void sendPacket(Packet<?> packet, Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	private void spawn(Player player) {
		sendPacket(packetPlayOutSpawnEntity, player);
		sendPacket(packetPlayOutEntityMetadata, player);
		sendPacket(packetPlayOutEntityVelocity, player);
	}

	private void clearSign(Player player) {
// not really important
//		if (isBlockSign()) {
//			String[] lines = new String[] { "AdminShop", "Loading...", "Loading...", "Loading..." };
//			player.sendSignChange(location, lines);
//		}
	}

	private void updateSign(Player player) {
		if (isBlockSign()) {
			String price = Utils.wrapper.backward(Utils.wrapper.forward(getPlayerPrice(player))) + "";
			String[] lines = new String[] { "AdminShop",
					getItemStack() == null ? "EMPTY"
							: String.valueOf(getItemStack().getAmount()) + " "
									+ Utils.prettifyText(getItemStack().getType().toString()),
					price + "$", isBuyable() ? "Kaufen" : "Verkaufen" };
			player.sendSignChange(location, lines);
		}
	}

	private void initPackets() {
		WorldServer s = ((CraftWorld) location.getWorld()).getHandle();
		this.entity = new EntityItem(s);
		entity.setLocation(location.getX() + 0.5, location.getY() - 1, location.getZ() + 0.5, 0, 0);
		entity.setItemStack(CraftItemStack.asNMSCopy(getItemStack()));
		this.packetPlayOutSpawnEntity = new PacketPlayOutSpawnEntity(this.entity, 2);
		this.packetPlayOutEntityMetadata = new PacketPlayOutEntityMetadata(this.entity.getId(),
				this.entity.getDataWatcher(), true);
		this.packetPlayOutEntityVelocity = new PacketPlayOutEntityVelocity(entity.getId(), 0, 0, 0);
		this.packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(new int[] { entity.getId() });
	}

}
