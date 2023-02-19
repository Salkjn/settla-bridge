package de.settla.utilities.local.region.space.blocks;

import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.BlockData;
import de.settla.utilities.local.region.space.BlockList;
import de.settla.utilities.storage.Serial;

@Serial("nbl")
public class NormalBlockList extends BlockList {

	private final short[][] blocks;
	
	public NormalBlockList(int size) {
		super(size);
		this.blocks = new short[size][2];
	}
	
	public NormalBlockList(short[][] blocks) {
		super(blocks.length);
		this.blocks = blocks;
	}
	
	@Override
	public short[] get(int index) {
		return index < size() ? this.blocks[index] : null;
	}
	
	@Override
	public NormalBlockList duplicate() {
		short[][] duplicate = new short[size()][2];
		for (int i = 0; i < duplicate.length; i++) {
			short[] block = blocks[i];
			duplicate[i] = new short[]{block[0], block[1]};
		}
		return new NormalBlockList(duplicate);
	}
	
	@Override
	public NormalBlockList rotate90(Vector origin) {
		
		for (int index = 0; index < blocks.length; index++) {
			short[] block = blocks[index];
			if(block != null) {
				short type = block[0];
				short data = block[1];
				short newData = (short) BlockData.rotate90(type, data);
				blocks[index][1] = newData;
			}
		}
		
		return this;
	}
}
