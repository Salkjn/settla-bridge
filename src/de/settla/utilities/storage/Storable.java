package de.settla.utilities.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface Storable {

    /**
     * Tests whether changes have been made.
     *
     * @return true if changes have been made
     */
	public default boolean isDirty() {
		return false;
	}

    /**
     * Set whether changes have been made.
     *
     * @param dirty a new dirty state
     */
    public default void setDirty(boolean dirty) {
    	
    }
    
    
	/**
	 * A method to store objects.
	 * 
	 * @return a map.
	 */
	
	public default Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put(Memory.MEMORY_KEY, Memory.id(this.getClass()));
		return map;
	}
	
	public default <T extends Storable> T deserialize(Object object, Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) object;
		return Memory.deserialize(map, clazz);
	}
	
	public static class Memory {
		
		public static final String MEMORY_KEY = "+";
		private static final Map<String, Function<Map<String, Object>, Storable>> functions = new HashMap<>();
		
		public static <T extends Storable> T deserialize(Map<String, Object> map, Class<T> clazz) {
			String id = (String) map.remove(MEMORY_KEY);
//			System.out.println("ID: " + id);
			if(id == null)
				return null;
			Function<Map<String, Object>, Storable> function = functions.get(id.toLowerCase());
			return clazz.cast(function.apply(map));
		}
		
		@SuppressWarnings("unchecked")
		public static <T extends Storable> T deserialize(Object object, Class<T> clazz) {
			return deserialize((Map<String, Object>) object, clazz);
		}
		
		public static boolean register(Class<?> clazz, Function<Map<String, Object>, Storable> function) {
			String id = id(clazz);
			if(functions.containsKey(id)) {
				return false;
			} else {
				functions.put(id, function);
				return true;
			}
		}
		
		private static String id(Class<?> clazz) {
			String id = clazz.getName();
			if(clazz.isAnnotationPresent(Serial.class)) {
				Serial ser = clazz.getAnnotation(Serial.class);
				if(ser != null) {
					id = ser.value();
				}
			}
			return id.toLowerCase();
		}
		
	}
	
}
