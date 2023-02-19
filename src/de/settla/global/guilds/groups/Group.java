package de.settla.global.guilds.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.settla.utilities.storage.Storable;

public abstract class Group<A> implements Storable {
	
	private boolean dirty;

	private final List<A> elements = new ArrayList<>();
	private final Object lock = new Object();
	
	protected abstract String a(A a);
	protected abstract A b(String str);
	
	public Group() {
		
	}
	
	@SuppressWarnings("unchecked")
	public Group(Map<String, Object> map) {
		((List<String>)map.get("elements")).stream().forEach(b -> elements.add(b(b)));
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("elements", elements.stream().map(a -> a(a)).collect(Collectors.toList()));
			return map;
		}
	}

	public boolean contains(A element) {
		synchronized (lock) {
			return elements.contains(element);
		}
	}
	
	public boolean add(A element) {
		synchronized (lock) {
			if(elements.contains(element)) {
				return false;
			} else {
				setDirty(true);
				return elements.add(element);
			}
		}
	}
	
	public boolean remove(A element) {
		synchronized (lock) {
			if(!elements.contains(element)) {
				return false;
			} else {
				setDirty(true);
				return elements.remove(element);
			}
		}
	}
	
	public void forEach(Consumer<A> consumer) {
		synchronized (lock) {
			elements.forEach(consumer);
		}
	}
	
	public Stream<A> stream() {
		synchronized (lock) {
			return elements.stream();
		}
	}
	
	public int size() {
		synchronized (lock) {
			return elements.size();
		}
	}
	
	@Override
	public void setDirty(boolean dirty) {
		synchronized (lock) {
			this.dirty = dirty;
		}
	}
	
	public boolean isDirty() {
		synchronized (lock) {
			return dirty;
		}
	}

}
