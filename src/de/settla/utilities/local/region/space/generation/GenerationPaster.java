package de.settla.utilities.local.region.space.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import de.settla.local.LocalPlugin;
import de.settla.utilities.functions.Action;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.region.World;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.BlockList;
import de.settla.utilities.local.region.space.Room;
import de.settla.utilities.local.region.space.selection.FormPaster;
import de.settla.utilities.local.region.space.selection.SelectionException;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("GenerationPaster")
public class GenerationPaster extends FormPaster implements Storable {

	//0,9,8,11,12,17,175,18,38,37,32,31,6,106,111,171,3,2,12,162
	
	public final static int[] AIR_ITEMS = new int[] {
			17,18,162,161
	};
	
	public final static int[] AIR2_ITEMS = new int[] {
			0
	};
	
	private final GenerationRegion generation;

	private int index;
	private final String blockListId;
	private int blocksPerPeriod, period;
	private final boolean deleteBlockList;
	private final boolean ignoreAir;

	private GenerationState state = GenerationState.NOTHING;

	public GenerationPaster(GenerationRegion generation, Form form, Room room, Vector origin, World world,
			BlockList blocks, String blockListId, boolean delete, boolean ignoreAir) throws SelectionException {
		this(generation, form, room, origin, world, blocks, blockListId, 0, 1, 0, delete, ignoreAir);
	}

	public GenerationPaster(GenerationRegion generation, Form form, Room room, Vector origin, World world,
			BlockList blocks, String blockListId, int index, int blocksPerPeriod, int period, boolean delete, boolean ignoreAir)
			throws SelectionException {
		super(form, room, origin, world, blocks);
		this.generation = generation;
		this.blockListId = blockListId;
		this.index = index;
		this.blocksPerPeriod = blocksPerPeriod;
		this.period = period;
		this.deleteBlockList = delete;
		this.ignoreAir = ignoreAir;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("blockListId", blockListId);
		map.put("form", getForm().serialize());
		map.put("room", room().serialize());
		map.put("origin", getOrigin().serialize());
		map.put("index", index);
		map.put("blocksPerPeriod", blocksPerPeriod);
		map.put("period", period);
		map.put("delete", deleteBlockList);
		map.put("air", ignoreAir);
		return map;
	}

	public GenerationRegion getGenerationRegion() {
		return generation;
	}

	private void changeState(GenerationState to) {
		generation.changeState(state, to);
		state = to;
	}

	@Override
	public void paste() { 
		
		if (!generation.getRegionRegistery().isRegistered())
			generation.getRegionRegistery().register(getWorld());
		
		BukkitRunnable runner = new BukkitRunnable() {
			@Override
			public void run() {
				switch (state) {
				case NOTHING:
				case RESTART:
					changeState(GenerationState.STRAT);
					break;
				case CALCULATING:
					return;
				case TERMINATE:
					delete();
				case STOP:
					cancel();
					changeState(GenerationState.END);
					return;
				default:
					break;
				}
				changeState(GenerationState.RUNNING);
				for (int i = 0; (i < blocksPerPeriod && index < size()); i++) {
					if (!pasteAndCheckIndex(index)) {
						i--;
					}
					index++;
				}

				generation.runningGeneration(GenerationPaster.this);
				
				if (index >= size() || blocksPerPeriod == 0) {
					cancel();
					changeState(GenerationState.END);
					delete();
				}
			}
		};
		runner.runTaskTimer(LocalPlugin.getInstance(), 0, period);
	}

	private void delete() {
//		getWorld().delete(generation);
		generation.getWorld().delete(generation);
		if (deleteBlockList) {
			BlockList.deleteBlockList(blockListId);
		}
	}

	public boolean pasteAndCheckIndex(int index) {
		short[] data = getBlockList().get(index);
		if (data == null)
			return false;
		Vector position = indexToVector(index).add(getOrigin());
		short[] data1 = getWorld().getBukkitWorld().getBlock(position);
		if (canUpdate(data1, data, index)) {
			
			if(ignoreAir && data[0] == 132) {
				getWorld().getBukkitWorld().setBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ(),
						0, 0, false);
			} else {
				getWorld().getBukkitWorld().setBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ(),
						data[0], data[1], false);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean canUpdate(short[] data1, int index) {
		return canUpdate(data1, getBlockList().get(index), index);
	}

	public boolean canUpdate(final short[] data1, final short[] data2, int index) {
		//2 = to 1 = from
		if (data2 == null || data1 == null)
			return false;
		
		if (ignoreAir && (Utils.checkMaterials(data2[0], AIR2_ITEMS) && !Utils.checkMaterials(data1[0], AIR_ITEMS))) {
			return false;
		}
		return !(data1[0] == data2[0] && data1[1] == data2[1]);
	}

	public List<Integer> getUpdatableBlockId() {
		List<Integer> list = new ArrayList<>();
		throughData(0, getWorld().getBukkitWorld(), (index, data) -> {
			if (canUpdate(data, index))
				list.add(index);
		});
		return list;
	}

	private int getUpdatableBlocks() {
		Counter counter = new Counter(0);
		throughData(0, getWorld().getBukkitWorld(), (index, data) -> {
			if (canUpdate(data, index))
				counter.up();
		});
		return counter.count();
	}

	public void calculateTime(int time, Action action) {
		changeState(GenerationState.CALCULATING);
		new BukkitRunnable() {
			@Override
			public void run() {
				int blocks = getUpdatableBlocks();
				int[] c = calculateTime(blocks, 20 * time);
				GenerationPaster.this.blocksPerPeriod = c[0];
				GenerationPaster.this.period = c[1];
				changeState(GenerationState.NOTHING);
				action.action();
			}
		}.runTaskAsynchronously(LocalPlugin.getInstance());
	}
	
	private static int[] calculateTime(double totalCount, double time) {
		
		double finalPeriod = 0;
		double finalCount = 0;

		if (totalCount != 0) {
			if (time >= totalCount) {
				finalCount = 1;
				finalPeriod = time / (totalCount);
			} else {
				finalPeriod = 1;
				finalCount = (totalCount) / time;
			}
		}
		
		int a = (int) Math.ceil(finalCount);
		int b = (int) Math.ceil(finalPeriod);
		return new int[]{a,b};
	}

	public GenerationState getState() {
		return state;
	}

	public void stop() {
		changeState(GenerationState.STOP);
	}

	public void terminate() {
		changeState(GenerationState.TERMINATE);
	}

	public void restart() {
		if (state == GenerationState.END) {
			changeState(GenerationState.RESTART);
			paste();
		}
	}

	public int getIndex() {
		return index;
	}

	public int getBlocksPerPeriod() {
		return blocksPerPeriod;
	}

	public int getPeriod() {
		return period;
	}

	public boolean isIgnoreAir() {
		return ignoreAir;
	}
	
	public double getPercent() {
		return ((double) index) / ((double) size());
	}
	
}
