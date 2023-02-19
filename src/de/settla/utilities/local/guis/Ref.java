package de.settla.utilities.local.guis;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

public class Ref {
	
	public static final String VERSION = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

	public static Class<?> getNMS(String classPath){
		try {
			Class<?> clazz = Class.forName("net.minecraft.server." + VERSION + "." + classPath);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getOBC(String classPath){
		try {
			Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + VERSION + "." + classPath);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params){
		while (clazz != null) {
		    Method[] methods = clazz.getDeclaredMethods();
		    for (Method method : methods) {
		        if (method.getName().equals(methodName) && compare(method.getParameterTypes(), params)) {
		        	method.setAccessible(true);
		            return method;
		        }
		    }
		    clazz = clazz.getSuperclass();
		}
		return null;
	}
	
	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... params){
		while (clazz != null) {
		    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		    for (Constructor<?> cons : constructors) {
		        if(compare(cons.getParameterTypes(), params)){
		        	return cons;
		        }
		    }
		    clazz = clazz.getSuperclass();
		}
		return null;
	}
	
	public static Method getMethod(Object object, String methodName, Class<?> ... params){
		return getMethod(object.getClass(), methodName, params);
	}
	
	public static Field getField(Class<?> clazz, String fieldName){
		while(clazz != null){
			Field[] fields = clazz.getDeclaredFields();
			for(Field f : fields){
				if(f.getName().equals(fieldName)){
					f.setAccessible(true);
					return f;
				}
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}
	
	public static boolean compare(Class<?>[] classes, Class<?>[] otherClasses){
		if(classes == null || otherClasses == null)
			return false;
		if(classes.length != otherClasses.length)
			return false;
		for(int i = 0; i < classes.length; i++){
			if(classes[i] != otherClasses[i])
				return false;
		}
		
		return true;
	}
	
	private static Class<?>[] params(Class<?>...classes){
		return classes;
	}
	
	public static class NBT {
		
		public static final Class<?>
		CLASS_NBT_TAG_BASE = getNMS("NBTBase"),
		CLASS_NBT_TAG_COMPOUND = getNMS("NBTTagCompound"),
		CLASS_NBT_LIST = getNMS("NBTTagList"),
		CLASS_INTEGER_ARRAY = new int[]{}.getClass(),
		CLASS_BYTE_ARRAY = new int[]{}.getClass(),
		CLASS_CRAFT_ITEM_STACK = getOBC("inventory.CraftItemStack"),
		CLASS_NMS_ITEM_STACK = getNMS("ItemStack");
		
		private final static Method AS_NMY_COPY_METHOD = getMethod(CLASS_CRAFT_ITEM_STACK, "asNMSCopy", org.bukkit.inventory.ItemStack.class);
		public static Object toNMSItem(ItemStack item){
			if(item == null)
				return null;
			try {
				return AS_NMY_COPY_METHOD.invoke(null, item);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		private static final Method AS_BUKKIT_COPY_METHOD = getMethod(CLASS_CRAFT_ITEM_STACK, "asBukkitCopy", CLASS_NMS_ITEM_STACK);
		public static ItemStack toBukkitItem(Object obcItem){
			if(obcItem == null)
				return null;
			try {
				return (ItemStack) AS_BUKKIT_COPY_METHOD.invoke(null, obcItem);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
//			ItemStack it = CraftItemStack.asBukkitCopy(null);
		}
		
		private static final Method GET_TAG_METHOD = getMethod(CLASS_NMS_ITEM_STACK, "getTag");
		public static Object getNBTTag(Object nmsItem){
			try {
				Object nbt = GET_TAG_METHOD.invoke(nmsItem);
				if(nbt == null){
//					System.out.println("it was null");
					nbt = createNBTTag();
				}
				return nbt;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		Constructor<?> NBT_TAG_COMPOUND_CONSTRUCTOR = getConstructor(CLASS_NBT_TAG_COMPOUND);
		public static Object createNBTTag() {
			try {
//				return CLASS_NBT_TAG_COMPOUND.newInstance();
				return CLASS_NBT_TAG_COMPOUND.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		private static final Method SET_TAG_METHOD = getMethod(CLASS_NMS_ITEM_STACK, "setTag", CLASS_NBT_TAG_COMPOUND);
		public static void applyNBT(Object nmsItem, Object nbt){
			try {
				SET_TAG_METHOD.invoke(nmsItem, nbt);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		private static final Method HAS_KEY_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "hasKey", String.class);
		public static boolean hasKey(Object nbt, String key){
			try {
				return (boolean) HAS_KEY_METHOD.invoke(nbt, key);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		private static final Method SET_STRING_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "setString", params(String.class, String.class));
		public static void setString(Object nbt, String key, String val){
			try {
				SET_STRING_METHOD.invoke(nbt, key, val);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		private static final Method SET_INT_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "setInt", String.class, Integer.TYPE);
		public static void setInt(Object nbt, String key, int val){
			try {
				SET_INT_METHOD.invoke(nbt, key, val);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		private static final Method SET_BOOLEAN_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "setBoolean", String.class, Boolean.TYPE);
		public static void setBoolean(Object nbt, String key, boolean val){
			try {
				SET_BOOLEAN_METHOD.invoke(nbt, key, val);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		private static final Method SET_DOUBLE_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "setDouble", String.class, Double.TYPE);
		public static void setDouble(Object nbt, String key, double val){
			try {
				SET_DOUBLE_METHOD.invoke(nbt, key, val);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		private static final Method SET_LONG_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "setLong", String.class, Double.TYPE);
		public static void setLong(Object nbt, String key, long val){
			try {
				SET_LONG_METHOD.invoke(nbt, key, val);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		private static final Method GET_DOUBLE_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "getDouble", String.class);
		public static double getDouble(Object nbt, String key){
			try {
				return (double) GET_DOUBLE_METHOD.invoke(nbt, key);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			throw new RuntimeException("Could not invoke getDouble");
		}

		private static final Method GET_INT_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "getInt", String.class);
		public static int getInt(Object nbt, String key) {
			try {
				return (int) GET_INT_METHOD.invoke(nbt, key);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			throw new RuntimeException("Could not invoke getInt");
		}
		
		private static final Method GET_LONG_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "getLong", String.class);
		public static long getLong(Object nbt, String key){
			try {
				return (long) GET_LONG_METHOD.invoke(nbt, key);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			throw new RuntimeException("Could not invoke getLong");
		}
		
		private static final Method GET_STRING_METHOD = getMethod(CLASS_NBT_TAG_COMPOUND, "getString", String.class);
		public static String getString(Object nbt, String key) {
			try {
				return (String) GET_STRING_METHOD.invoke(nbt, key);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			throw new RuntimeException("Could not invoke getString");
		}

		private static final Method REMOVE_KEY = getMethod(CLASS_NBT_TAG_COMPOUND, "remove", String.class);
		public static void remove(Object nbt, String key) {
			try {
				REMOVE_KEY.invoke(nbt, key);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}	
	}
}