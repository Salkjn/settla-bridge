package de.settla.utilities;

public class Tuple<X, Y> {
	
	private final X x;
	private final Y y;
	
	public Tuple(X x, Y y) {
		super();
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}
	
}
