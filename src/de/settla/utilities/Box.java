package de.settla.utilities;

public class Box<T> {

	private T object;

	public Box(T object) {
		super();
		this.object = object;
	}

	public T get() {
		return object;
	}
	
	public void set(T object) {
		this.object = object;
	}
	
}
