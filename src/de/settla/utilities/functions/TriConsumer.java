package de.settla.utilities.functions;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

	void accept(A a, B b, C c);
	
}
