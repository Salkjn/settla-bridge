package de.settla.utilities.local.region.space.selection;

import java.util.function.BiConsumer;

import de.settla.utilities.local.region.BukkitWorld;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.Room;

public class FormSelection extends Selection {

	private final Form form;
	private final Vector origin;
	
	public FormSelection(Form form, Room room, Vector origin) {
		super(room);
		this.form = form;
		this.origin = origin;
	}
	
	public void throughData(int startIndex, BukkitWorld world, BiConsumer<Integer, short[]> consumer) {
		for (int index = startIndex; index < size(); index++) {
			Vector vector = indexToVector(index).add(origin);
			if(form.overlaps(vector)) {
				consumer.accept(index, world.getBlock(vector));
			} else {
				consumer.accept(index, BukkitWorld.FAILED_BLOCK_DATA);
			}
		}
	}

	public Form getForm() {
		return form;
	}

	public Vector getOrigin() {
		return origin;
	}

}
