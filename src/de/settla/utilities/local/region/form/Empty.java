package de.settla.utilities.local.region.form;

public class Empty extends Form {

	@Override
	public String type() {
		return "EMPTY";
	}

	@Override
	public Vector minimum() {
		return Vector.ZERO;
	}

	@Override
	public Vector maximum() {
		return Vector.ZERO;
	}

	@Override
	public boolean overlaps(Vector vector) {
		return false;
	}

	@Override
	public boolean intersect(Form form) {
		return false;
	}

	@Override
	public Form move(Vector vector) {
		return this;
	}

	@Override
	public Empty rotate90(Vector origin) {
		return this;
	}
	
}
