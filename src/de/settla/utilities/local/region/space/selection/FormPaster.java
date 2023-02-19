package de.settla.utilities.local.region.space.selection;

import de.settla.utilities.local.region.World;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.BlockList;
import de.settla.utilities.local.region.space.Room;

public class FormPaster extends FormSelection implements Paster {

	private final World world;
	private final BlockList blocks;

	public FormPaster(Form form, Room room, Vector origin, World world, BlockList blocks) {
		super(form, room, origin);

		if (blocks.size() != size())
			System.err.println("The block size does not match with the total size. (blocks: " + blocks.size()
						+ ", volume:" + size() + ")");

		this.world = world;
		this.blocks = blocks;
	}
	
	public World getWorld() {
		return world;
	}

	public BlockList getBlockList() {
		return blocks;
	}

	@Override
	public void pasteIndex(int index) {
		short[] data = blocks.get(index);
		if (data == null)
			return;
		Vector position = indexToVector(index).add(getOrigin());
		world.getBukkitWorld().setBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ(), data[0], data[1], false);
	}

	@Override
	public void paste() {
		throughIndex(index -> pasteIndex(index));
	}

}