package de.settla.utilities.local.region.space.selection;

import de.settla.utilities.local.region.BukkitWorld;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.BlockList;
import de.settla.utilities.local.region.space.Room;
import de.settla.utilities.local.region.space.blocks.NormalBlockList;

public class FormSelector extends FormSelection implements Selector {
	
	public FormSelector(Form form, Room room, Vector origin) {
		super(form, room, origin);
	}

	@Override
	public BlockList select(BukkitWorld world) {
		short[][] blocks = new short[size()][2];
		throughData(0, world, (index, data) -> blocks[index] = data);
		return new NormalBlockList(blocks);
	}

}
