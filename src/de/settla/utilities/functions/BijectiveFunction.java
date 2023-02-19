package de.settla.utilities.functions;

import java.util.function.Function;

public class BijectiveFunction<A, B> {

	private final Function<A, B> forward;
	private final Function<B, A> backward;
	
	public BijectiveFunction(Function<A, B> forward, Function<B, A> backward) {
		super();
		this.forward = forward;
		this.backward = backward;
	}
	
	public B forward(A a) {
		return forward.apply(a);
	}

	public A backward(B b) {
		return backward.apply(b);
	}

}
