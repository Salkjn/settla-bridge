package de.settla.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleLog<T> {

	private final List<T> log = new ArrayList<>();
	private boolean error = true;
	
	public void log(T msg) {
		log.add(msg);
	}
	
	public void success() {
		error = false;
	}
	
	public boolean isSuccessful() {
		return !error;
	}
	
	public void forEach(Consumer<T> consumer) {
		log.forEach(consumer);
	}
	
	public void clear() {
		log.clear();
	}
	
	public void confusion(SimpleLog<T> log) {
		log.forEach(msg -> this.log(msg));
		log.clear();
		if (log.isSuccessful())
			success();
	}
}
