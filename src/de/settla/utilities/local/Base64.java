package de.settla.utilities.local;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.common.base.Supplier;

public class Base64 {

	public static Map<Class<?>, Function<BukkitObjectInputStream, ?>> deserializerFunctions = new HashMap<>();
	public static Map<Class<?>, BiConsumer<BukkitObjectOutputStream, ?>> serializerFunctions = new HashMap<>();

	static {

		deserializerFunctions.put(ItemStack[].class, input -> {
			try {
				int size = input.read();
				ItemStack[] items = new ItemStack[size];
				for (int i = 0; i < size; i++)
					items[i] = (ItemStack) input.readObject();
				return items;
			} catch (ClassNotFoundException | IOException e) {
				return null;
			}
		});

		serializerFunctions.put(ItemStack[].class, (output, object) -> {
			try {
				ItemStack[] list = (ItemStack[]) object;
				output.write(list.length);
				for (ItemStack itemStack : list) {
					output.writeObject(itemStack);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		deserializerFunctions.put(PotionEffect[].class, input -> {
			try {
				int size = input.read();
				PotionEffect[] items = new PotionEffect[size];
				for (int i = 0; i < size; i++)
					items[i] = (PotionEffect) input.readObject();
				return items;
			} catch (ClassNotFoundException | IOException e) {
				return null;
			}
		});

		serializerFunctions.put(PotionEffect[].class, (output, object) -> {
			try {
				PotionEffect[] list = (PotionEffect[]) object;
				output.write(list.length);
				for (PotionEffect effect : list) {
					output.writeObject(effect);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		deserializerFunctions.put(Location.class, input -> {
			try {
				return (Location) input.readObject();
			} catch (ClassNotFoundException | IOException e) {
				return null;
			}
		});

		serializerFunctions.put(Location.class, (output, object) -> {
			try {
				output.writeObject(object);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		deserializerFunctions.put(ItemStack.class, input -> {
			try {
				return (ItemStack) input.readObject();
			} catch (ClassNotFoundException | IOException e) {
				return null;
			}
		});

		serializerFunctions.put(ItemStack.class, (output, object) -> {
			try {
				output.writeObject(object);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	public static <T> T fromBase64(String data, Class<T> clazz, Supplier<T> defaultValue) {

		Function<BukkitObjectInputStream, ?> function = deserializerFunctions.get(clazz);
		
		if (data == null || function == null)
			return defaultValue.get();
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			T object = clazz.cast(function.apply(dataInput));
			dataInput.close();
			return object;
		} catch (IOException e) {
			return null;
		}
	}

	public static <T> String toBase64(T object, Class<T> clazz, Supplier<T> defaultValue) {

		@SuppressWarnings("unchecked")
		BiConsumer<BukkitObjectOutputStream, T> function = (BiConsumer<BukkitObjectOutputStream, T>) serializerFunctions
				.get(clazz);
		
		if (function == null)
			return null;

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			function.accept(dataOutput, object == null ? defaultValue.get() : object);
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			return null;
		}
	}

}
