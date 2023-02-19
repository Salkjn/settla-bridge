package de.settla.utilities.local.region.space.selection;

import java.util.function.Consumer;

import de.settla.utilities.functions.TriConsumer;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.Room;

public interface ISelection {

	Room room();
	
	int size();

	int index(int x, int y, int z);

	Vector indexToVector(int idx);

	int[] indexToCoord(int idx);

	void throughIndex(int startIndex, Consumer<Integer> consumer);

	void throughVector(int startIndex, Consumer<Vector> consumer);

	void throughCoord(int startIndex, TriConsumer<Integer, Integer, Integer> consumer);

	void throughIndex(Consumer<Integer> consumer);

	void throughVector(Consumer<Vector> consumer);

	void throughCoord(TriConsumer<Integer, Integer, Integer> consumer);

}