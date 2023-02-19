package de.settla.utilities.local.region.space.selection;

import java.util.function.Consumer;

import de.settla.utilities.functions.TriConsumer;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.Room;

public class Selection implements ISelection {
	
	private final Room room;
	
	public Selection(Room room) {
		this.room = room;
	}
	
	@Override
	public Room room() {
		return room;
	}
	
	@Override
	public int size() {
		return room.volume();
	}
	
	@Override
	public int index(int e1, int e2, int e3) {
		int a = 1;
		int b = room.getMaxE1();
		int c = (room.getMaxE1()) * (room.getMaxE2());
		int d = 0;
		return a * e1 + b * e2 + c * e3 + d;
	}

	@Override
	public Vector indexToVector(int index) {
		int e1 = index % (room.getMaxE1());
		index /= (room.getMaxE1());
		int e2 = index % (room.getMaxE2());
		index /= (room.getMaxE2());
		int e3 = index;
		return room.e1().multiply(e1).add(room.e2().multiply(e2).add(room.e3().multiply(e3)));
	}
	
	@Override
	public int[] indexToCoord(int index) {
		int x = index % (room.getMaxE1());
		index /= (room.getMaxE1());
		int y = index % (room.getMaxE2());
		index /= (room.getMaxE2());
		int z = index;
		return new int[]{x, y, z};
	}
	
	@Override
	public void throughIndex(int startIndex, Consumer<Integer> consumer) {
		for (int index = startIndex; index < size(); index++) {
			consumer.accept(index);
		}
	}
	
	@Override
	public void throughVector(int startIndex, Consumer<Vector> consumer) {
		for (int index = startIndex; index < size(); index++) {
			consumer.accept(indexToVector(index));
		}
	}
	
	@Override
	public void throughCoord(int startIndex, TriConsumer<Integer, Integer, Integer> consumer) {
		for (int index = startIndex; index < size(); index++) {
			int[] coord = indexToCoord(index);
			consumer.accept(coord[0], coord[1], coord[2]);
		}
	}
	
	@Override
	public void throughIndex(Consumer<Integer> consumer) {
		throughIndex(0, consumer);
	}
	
	@Override
	public void throughVector(Consumer<Vector> consumer) {
		throughVector(0, consumer);
	}
	
	@Override
	public void throughCoord(TriConsumer<Integer, Integer, Integer> consumer) {
		throughCoord(0, consumer);
	}
	
}
