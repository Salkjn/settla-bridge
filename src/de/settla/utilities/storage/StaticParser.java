package de.settla.utilities.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.settla.utilities.functions.BijectiveFunction;

@SuppressWarnings("rawtypes")
public class StaticParser {
	
	private static final Map<Class<?>, BijectiveFunction<?, String>> functions = new HashMap<>();
	
	public static void register() {
		put(String.class, new BijectiveFunction<String, String>(s -> s, s -> s));
		put(UUID.class, new BijectiveFunction<UUID, String>(uuid -> uuid.toString(), string -> {
			if (string == null)
				return null;
			try {
				return UUID.fromString(string);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}));
		put(Integer.class, new BijectiveFunction<Integer, String>(i -> i.toString(), string -> {
			if (string == null)
				return null;
			try {
				return Integer.parseInt(string);
			} catch (NumberFormatException e) {
				return null;
			}
		}));
		put(Double.class, new BijectiveFunction<Double, String>(i -> i.toString(), string -> {
			if (string == null)
				return null;
			try {
				return Double.parseDouble(string);
			} catch (NumberFormatException e) {
				return null;
			}
		}));
		put(Long.class, new BijectiveFunction<Long, String>(i -> i.toString(), string -> {
			if (string == null)
				return null;
			try {
				return Long.parseLong(string);
			} catch (NumberFormatException e) {
				return null;
			}
		}));
		put(Float.class, new BijectiveFunction<Float, String>(i -> i.toString(), string -> {
			if (string == null)
				return null;
			try {
				return Float.parseFloat(string);
			} catch (NumberFormatException e) {
				return null;
			}
		}));
		put(Class.class, new BijectiveFunction<Class, String>(clazz -> clazz.getName(), string -> {
			if (string == null)
				return null;
			try {
				return Class.forName(string);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}));
		put(Boolean.class, new BijectiveFunction<Boolean, String>(bool -> bool ? "true" : "false", string -> {
			if (string == null)
				return null;
			return string.equalsIgnoreCase("true") ? true : ((string.equalsIgnoreCase("false") ? false : null));
		}));
	}
	
	@SuppressWarnings("unchecked")
	private static <A> BijectiveFunction<A, String> get(Class<A> clazz) {
		return (BijectiveFunction<A, String>) functions.get(clazz);
	}
	
	public static <A> A parse(String string, Class<A> clazz) {
		if (string == null)
			return null;
		try {
			return clazz.cast(get(clazz).backward(string));
		} catch (ClassCastException e) {
			return null;
		}
	}
	
	public static <A> String unparse(A object, Class<A> clazz) {
		return get(clazz).forward(object);
	}
	
	@SuppressWarnings("unchecked")
	public static <A> A parse(String string) {
		return (A) functions.values().stream().filter(f -> f.backward(string) != null).findFirst().orElse(null);
	}
	
	public static <A> void put(Class<A> clazz, BijectiveFunction<A, String> function) {
		functions.put(clazz, function);
	}
	
}