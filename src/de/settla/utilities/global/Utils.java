package de.settla.utilities.global;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;

public class Utils {
	
//	public static int getTopPermission(UUID player, String permission) {
//		int top = player.getEffectivePermissions().stream()
//				.filter(perm -> perm.getPermission().startsWith(permission))
//				.map(perm -> Integer.parseInt(perm.getPermission().replace(permission, "")))
//				.max((x, y) -> Integer.compare(x, y)).orElse(0);
//		return top;
//	}
	
	public static boolean checkMaterials(int toCheck, int[] materials) {
		for (int material : materials) {
			if(material == toCheck)
				return true;
		}
		return false;
	}
	
	public static <T> int count(T[] objects, Function<T, Boolean> function) {
		int i = 0;
		for (T t : objects) {
			if(function.apply(t))
				i++;
		}
		return i;
	}
	
	public static <T> int count(Iterable<T> objects, Function<T, Boolean> function) {
		int i = 0;
		for (T t : objects) {
			if(function.apply(t))
				i++;
		}
		return i;
	}
	
	public static <T> List<T> filter(T[] objects, Function<T, Boolean> function) {
		List<T> list = new ArrayList<T>();
		for (T t : objects) {
			if(function.apply(t))
				list.add(t);
		}
		return list;
	}
	
	public static <T> List<T> filter(Iterable<T> objects, Function<T, Boolean> function) {
		List<T> list = new ArrayList<T>();
		for (T t : objects) {
			if(function.apply(t))
				list.add(t);
		}
		return list;
	}
	
	public static <T, M extends T> List<M> filter(Iterable<T> objects, Class<? extends M> clazz) {
		List<M> list = new ArrayList<>();
		for (T obj : objects) {
			if(clazz.isAssignableFrom(obj.getClass())) {
				list.add(clazz.cast(obj));
			}
		}
		return list;
	}
	
	public static <T, M extends T> List<M> filter(Iterable<T> objects, Class<? extends M> clazz, Function<M, Boolean> function) {
		List<M> list = new ArrayList<>();
		for (T obj : objects) {
			if(clazz.isAssignableFrom(obj.getClass())) {
				M m = clazz.cast(obj);
				if(function.apply(m))
					list.add(clazz.cast(obj));
			}
		}
		return list;
	}
	
	public static <T> void consumtion(T[] objects, Consumer<T> consumer) {
		for (T t : objects) {
			consumer.accept(t);
		}
	}
	
	public static <T> void consumtion(Iterable<T> objects, Consumer<T> consumer) {
		for (T t : objects) {
			consumer.accept(t);
		}
	}
	
	public static <T, M> List<M> transfer(Iterable<T> input, Function<T, M> transformation) {
		List<M> list = new ArrayList<>();
		input.forEach(t -> list.add(transformation.apply(t)));
		return list;
	}
	
	public static <T> String toString(Iterable<T> iterable, String splitter, Function<T, String> function) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (T object : iterable) {
			if(first) {
				first = false;
			} else {
				sb.append(splitter);
			}
			sb.append(function.apply(object));
		}
		return sb.toString();
	}
	
	public static <T> String toString(T[] iterable, String splitter, Function<T, String> function) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (T object : iterable) {
			if(first) {
				first = false;
			} else {
				sb.append(splitter);
			}
			sb.append(function.apply(object));
		}
		return sb.toString();
	}
	
    private static final Pattern pattern = Pattern.compile("([^\\%]*)(\\%)(\\w+)(\\%)([^\\%]*)", Pattern.CASE_INSENSITIVE);

    public static String replace(String line, String[]... replacements) {
        Matcher match = pattern.matcher(line);
        StringBuilder newLine = new StringBuilder();
        boolean noMatch = true;
        while (match.find()) {
            noMatch = false;
            String id = match.group(3);
            loop : for (String[] replace : replacements) {
                if(replace[0].equalsIgnoreCase(id)) {
                    id = replace[1];
                    break loop;
                }
            }
            newLine.append(match.group(1));
            newLine.append(id);
            newLine.append(match.group(5));
        }
        return noMatch ? line : newLine.toString();
    }
    
    public static String prettifyText(String ugly) {
        if (!ugly.contains("_") && (!ugly.equals(ugly.toUpperCase())))
            return ugly;
        String fin = "";
        ugly = ugly.toLowerCase();
        if (ugly.contains("_")) {
            String[] splt = ugly.split("_");
            int i = 0;
            for (String s : splt) {
                i += 1;
                fin += Character.toUpperCase(s.charAt(0)) + s.substring(1);
                if (i < splt.length)
                    fin += " ";
            }
        } else {
            fin += Character.toUpperCase(ugly.charAt(0)) + ugly.substring(1);
        }
        return fin;
    }
    
	private static DecimalFormat format = new DecimalFormat("0.00");
	
	public static String round(double a){
		return format.format(a);	
	}
	
	public static String getPercent(double a) {
		return format.format(a*100);
	}
	
}
