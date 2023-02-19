package de.settla.utilities.local;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.settla.utilities.functions.BijectiveFunction;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class Utils {

    public static void decreaseHand(Player forPlayer) {
        ItemStack current = forPlayer.getItemInHand();
        decreaseHand(forPlayer, current);
    }

    public static void decreaseHand(Player player, ItemStack inHand) {
        if (inHand != null) {
            int newAmount = inHand.getAmount() - 1;
            if (newAmount <= 0) {
                inHand = null;
            } else {
                inHand.setAmount(newAmount);
            }
        }
        player.setItemInHand(inHand);
    }
	
    public static boolean hasEnoughPlaceFor(List<ItemStack> inventory, ItemStack item, int amount) {
        return canHaveItemHowOften(inventory, item) >= amount;
    }

    public static int canHaveItemHowOften(List<ItemStack> inventory, ItemStack item) {
        final int maxSize = item.getType().getMaxStackSize();
        return inventory.stream().filter(it -> it == null || item.isSimilar(it)).mapToInt(it -> it == null ? maxSize : maxSize - it.getAmount()).sum();
    }
	
	public static void sendActionbarMessage(Player player, String message) {
		IChatBaseComponent actionbartext = ChatSerializer.a("{\"text\": \"" + message + "\"}");
		PacketPlayOutChat actionbar = new PacketPlayOutChat(actionbartext, (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionbar);
	}
	
	public static final BijectiveFunction<Double, Long> wrapper = new BijectiveFunction<Double, Long>(
			a -> (long) (a * 100.0), b -> ((double) b) / 100D);

	public static int getTopPermission(Player player, String permission) {
		int top = player.getEffectivePermissions().stream()
				.filter(perm -> perm.getPermission().startsWith(permission))
				.map(perm -> Integer.parseInt(perm.getPermission().replace(permission, "")))
				.max((x, y) -> Integer.compare(x, y)).orElse(0);
		return top;
	}
	
	public static String timeToString(long time) {
		final long leftTime = time;
		long days = TimeUnit.MILLISECONDS.toDays(leftTime);
		if (days == 0) {
			long hours = TimeUnit.MILLISECONDS.toHours(leftTime);
			if (hours == 0) {
				long minutes = TimeUnit.MILLISECONDS.toMinutes(leftTime);
				if (minutes == 0) {
					long seconds = TimeUnit.MILLISECONDS.toSeconds(leftTime);
					return seconds == 1 ? seconds + " Sekunde" : seconds + " Sekunden";
				} else {
					return minutes == 1 ? minutes + " Minute" : minutes + " Minuten";
				}
			} else {
				return hours == 1 ? hours + " Stunde" : hours + " Stunden";
			}
		} else {
			return days == 1 ? days + " Tag" : days + " Tage";
		}
	}

	public static String timeToFancyString(long time) {
		final long leftTime = time;
		long days = TimeUnit.MILLISECONDS.toDays(leftTime);
		if (days == 0) {
			long hours = TimeUnit.MILLISECONDS.toHours(leftTime);
			if (hours == 0) {
				long minutes = TimeUnit.MILLISECONDS.toMinutes(leftTime);
				if (minutes == 0) {
					long seconds = TimeUnit.MILLISECONDS.toSeconds(leftTime);
					return seconds == 1 ? "Jede Sekunde" : "Alle " + seconds + " Sekunden";
				} else {
					return minutes == 1 ? "Jede Minute" : "Alle " + minutes + " Minuten";
				}
			} else {
				return hours == 1 ? "Jede Stunde" : "Alle " + hours + " Stunden";
			}
		} else {
			return days == 1 ? "Jeden Tag" : "Alle " + days + " Tage";
		}
	}

	public static boolean checkMaterials(int toCheck, int[] materials) {
		for (int material : materials) {
			if (material == toCheck)
				return true;
		}
		return false;
	}

	public static boolean checkMaterials(Material toCheck, Material... materials) {
		for (Material material : materials) {
			if (material == toCheck)
				return true;
		}
		return false;
	}

	public static boolean checkEntities(EntityType toCheck, EntityType... materials) {
		for (EntityType material : materials) {
			if (material == toCheck)
				return true;
		}
		return false;
	}

	public static <T> int count(T[] objects, Function<T, Boolean> function) {
		int i = 0;
		for (T t : objects) {
			if (function.apply(t))
				i++;
		}
		return i;
	}

	public static <T> int count(Iterable<T> objects, Function<T, Boolean> function) {
		int i = 0;
		for (T t : objects) {
			if (function.apply(t))
				i++;
		}
		return i;
	}

	public static <T> List<T> filter(T[] objects, Function<T, Boolean> function) {
		List<T> list = new ArrayList<T>();
		for (T t : objects) {
			if (function.apply(t))
				list.add(t);
		}
		return list;
	}

	public static <T> List<T> filter(Iterable<T> objects, Function<T, Boolean> function) {
		List<T> list = new ArrayList<T>();
		for (T t : objects) {
			if (function.apply(t))
				list.add(t);
		}
		return list;
	}

	public static <T, M extends T> List<M> filter(Iterable<T> objects, Class<? extends M> clazz) {
		List<M> list = new ArrayList<>();
		for (T obj : objects) {
			if (clazz.isAssignableFrom(obj.getClass())) {
				list.add(clazz.cast(obj));
			}
		}
		return list;
	}

	public static <T, M extends T> List<M> filter(Iterable<T> objects, Class<? extends M> clazz,
			Function<M, Boolean> function) {
		List<M> list = new ArrayList<>();
		for (T obj : objects) {
			if (clazz.isAssignableFrom(obj.getClass())) {
				M m = clazz.cast(obj);
				if (function.apply(m))
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
			if (first) {
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
			if (first) {
				first = false;
			} else {
				sb.append(splitter);
			}
			sb.append(function.apply(object));
		}
		return sb.toString();
	}

	private static final Pattern pattern = Pattern.compile("([^\\%]*)(\\%)(\\w+)(\\%)([^\\%]*)",
			Pattern.CASE_INSENSITIVE);

	public static String replace(String line, String[]... replacements) {
		Matcher match = pattern.matcher(line);
		StringBuilder newLine = new StringBuilder();
		boolean noMatch = true;
		while (match.find()) {
			noMatch = false;
			String id = match.group(3);
			loop: for (String[] replace : replacements) {
				if (replace[0].equalsIgnoreCase(id)) {
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

	public static String roundFormat(double a) {
		return format.format(a);
	}

	public static double round(double a) {
		return (double)((int)(100.0*a))/100.0;
	}
	
	public static String getPercent(double a) {
		return format.format(a * 100);
	}

}
