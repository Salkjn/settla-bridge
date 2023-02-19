package de.settla.utilities.local.region.space.blocks;

import de.settla.utilities.local.region.Rotatable;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.BlockList;
import de.settla.utilities.storage.Serial;

@Serial("sbl")
public class SimpleBlockList extends BlockList {

	private static final short[] AIR = new short[]{0,0};
	
	private final short[] block;
	
	public SimpleBlockList(int size) {
		super(size);
		this.block = AIR;
	}
	
	public SimpleBlockList(int size, short[] block) {
		super(size);
		this.block = block;
	}

	public SimpleBlockList(short[][] blocks) {
		super(blocks.length);
		this.block = blocks.length > 0 ? blocks[0] : AIR;
	}

	@Override
	public short[] get(int index) {
		return index < size() ? block : null;
	}

	@Override
	public SimpleBlockList duplicate() {
		return new SimpleBlockList(this.size(), new short[] {block[0], block[1]});
	}

	@Override
	public Rotatable rotate90(Vector origin) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
