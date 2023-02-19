package de.settla.utilities.local.region.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class RegionListener {
	
	private final Map<Class<? extends Event>, LMethods> METHODS = new HashMap<>();
	
	public RegionListener() {
		findMethods();
	}
	
	public RegionListener(Map<String, Object> map) {
		findMethods();
	}
	
	@SuppressWarnings("unchecked")
	private void findMethods() {
		for (Method method : getClass().getMethods()) {
			if (Modifier.isStatic(method.getModifiers()))
				continue;
			if(method.isAnnotationPresent(EventHandler.class)) {
				Class<?>[] clazzes = method.getParameterTypes();
				EventHandler eventHandler = method.getDeclaredAnnotation(EventHandler.class);
				Class<?> clazz = null;
				if(clazzes.length == 1 && Event.class.isAssignableFrom(clazz = clazzes[0])) {
					LMethods listenerMethods = METHODS.get(clazz);
					if(listenerMethods == null) {
						listenerMethods = new LMethods();
						METHODS.put((Class<? extends Event>) clazz, listenerMethods);
					}
					listenerMethods.add(new LMethod(method, eventHandler.ignoreCancelled(), eventHandler.priority()));
				}
			}
		}
	}
	
	/**
	 * Fires the event.
	 * 
	 * @param event the event to fire.
	 * @return true if a method exists.
	 */
	public boolean fire(Event event) {
		LMethods listenerMethods = METHODS.get(event.getClass());
		if(listenerMethods == null)
			return false;
		for (LMethod listenerMethod : listenerMethods) {
			listenerMethod.fire(this, event);
		}
		return true;
	}
	
	private class LMethods implements Iterable<LMethod> {
		
		private List<LMethod> methods = new ArrayList<>();
		private Object lock = new Object();
		
		public void add(LMethod method) {
			synchronized(lock) {
				methods.add(method);
				Collections.sort(methods);
			}
		}

		@Override
		public Iterator<LMethod> iterator() {
			synchronized (lock) {
				return methods.iterator();
			}
		}
		
	}
	
	private class LMethod implements Comparable<LMethod> {
		
		private Method method;
		private boolean ignoreCancelled;
		private EventPriority priority;
		
		public LMethod(Method method, boolean ignoreCancelled, EventPriority priority) {
			this.method = method;
			this.ignoreCancelled = ignoreCancelled;
			this.priority = priority;
		}
		
		@Override
		public int compareTo(LMethod o) {
			return priority.compareTo(o.priority);
		}
		
		public void fire(RegionListener listener, Event event) {
			try {
				if(ignoreCancelled) {
					method.invoke(listener, event);
				} else {
					if(!(event instanceof Cancellable && ((Cancellable)event).isCancelled())) {
						method.invoke(listener, event);
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}	
	}
}
