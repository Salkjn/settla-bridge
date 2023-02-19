package de.settla.utilities.functions;

@FunctionalInterface
public interface Callback<T> {
	
	public void done(T call);
	
}
