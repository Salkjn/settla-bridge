package de.settla.spigot.universe;

import java.util.Collection;
import java.util.function.Function;

@FunctionalInterface
public interface Searchable<IN> {

//	public <OUT> OUT search(Function<Stream<IN>, OUT> filter);
	public <OUT> OUT search(Function<Collection<IN>, OUT> filter);
	
}
