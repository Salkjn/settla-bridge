package de.settla.utilities.local.region.space.generation;

public class Counter {

	private int counter;

	public Counter(int counter) {
		this.counter = counter;
	}
	
	public void up() {
		up(1);
	}
	
	public void up(int dif) {
		counter = counter+dif;
	}
	
	public void down() {
		down(1);
	}
	
	public void down(int dif) {
		counter = counter-dif;
	}
	
	public int count() {
		return counter;
	}
	
}
