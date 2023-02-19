package de.settla.utilities.local.region;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.region.form.Vector;

public class BukkitWorld {

	public static final short[] FAILED_BLOCK_DATA = new short[]{0, 1};
	
	private final World world;
	private final Object lock = new Object();
	private org.bukkit.World bukkitWorld;
	
	private List<BlockData> waitingQue = new ArrayList<>();
	
	private static final long CONNECTION_DELAY = 500;
	
	public BukkitWorld(World world) {
		this.world = world;
		connectToBukkitWorld();
	}
	
	private void connectToBukkitWorld() {
		new BukkitRunnable() {			
			@Override
			public void run() {
				while (waiting()) {
					bukkitWorld = Bukkit.getWorld(world.getName());
					try {
						Thread.sleep(CONNECTION_DELAY);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				setBlocksInQue();
			}
		}.runTaskAsynchronously(LocalPlugin.getInstance());
	}
	
	private boolean waiting() {
		return bukkitWorld == null;
	}
	
	private List<BlockData> getBlockData() {
		synchronized (lock) {
			List<BlockData> list = waitingQue;
			waitingQue = new ArrayList<>();
			return list;
		}
	}
	
	private boolean hasBlockDataInQue() {
		synchronized (lock) {
			return !waitingQue.isEmpty();
		}
	}
	
	/**
	 * @return world the world of this bukkitworld.
	 */
	public World getRegionWorld() {
		return world;
	}
	
	public void connectToBukkitWorld(org.bukkit.World bukkitWorld) {
		this.bukkitWorld = bukkitWorld;
		if(bukkitWorld == null) 
			connectToBukkitWorld();
	}
	
	/**
	 * Sets the blocks in que.
	 */
	public void setBlocksInQue() {
		synchronized (lock) {
			if(hasBlockDataInQue()) {
				List<BlockData> list = getBlockData();
				list.forEach(a -> setBlock(a.x, a.y, a.z, a.type, a.data, a.applyPhysics));
			}
		}
	}
	
	private void addBlockToQue(BlockData block) {
		synchronized (lock) {
			waitingQue.add(block);
		}
	}
	
	/**
	 * Sets a block to the bukkitworld when no bukkitworld tracked it will be stored temporally in the block que.
	 * 
	 * @param x the x.
	 * @param y the y.
	 * @param z the z.
	 * @param type the type of the block.
	 * @param data the data of the block.
	 * @param applyPhysics if true physics will fire.
	 */
	@SuppressWarnings("deprecation")
	public void setBlock(int x, int y, int z, int type, int data, boolean applyPhysics) {
		
//		System.out.println("Block: (" + x + "," + y + "," + z + ") Type:" + type + ":" + data);
//		
//		System.out.println("World: " + bukkitWorld);
		
		if(bukkitWorld == null) {
			synchronized (lock) {
				addBlockToQue(new BlockData(x, y, z, type, data, applyPhysics));
			}
			return;
		}
		
		setBlocksInQue();
		
		Block block = bukkitWorld.getBlockAt(x, y, z);
		
//		System.out.println("Block: " + block);
		
		if(block.getState() instanceof InventoryHolder) {
			((InventoryHolder)block.getState()).getInventory().clear();
		}
		
		block.setTypeIdAndData(type, (byte) data, applyPhysics);
		
	}
	
	/**
	 * Checks the data.
	 * 
	 * @param data the block data.
	 * @return true if the data is no block.
	 */
	public boolean isFailBlockData(short[] data) {
		return data[0] == -1 && data[1] == -1;
	}
	
	/**
	 * Gets the block at position. If no world is loaded it returns a fail block data.
	 * 
	 * @param x the x.
	 * @param y the y.
	 * @param z the z.
	 * @return block data.
	 */
	@SuppressWarnings("deprecation")
	public short[] getBlock(int x, int y, int z) {
		if(bukkitWorld == null) {
			return new short[]{-1, -1};
		}
		org.bukkit.block.Block block = bukkitWorld.getBlockAt(x, y, z);
		return new short[]{(short)block.getTypeId(), block.getData()};
	}
	
	public short[] getBlock(Vector vector) {
		return getBlock(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}
	
	private class BlockData {
		
		int x, y, z, type, data;
		boolean applyPhysics;
		
		public BlockData(int x, int y, int z, int type, int data, boolean applyPhysics) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.type = type;
			this.data = data;
			this.applyPhysics = applyPhysics;
		}
		
	}
	
	/**
	 * @return the world from bukkit.
	 */
	public org.bukkit.World getBukkitWorld() {
		return bukkitWorld;
	}
	
	public String getName() {
		return world.getName();
	}
	
}
