package de.settla.utilities.storage;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.settla.utilities.storage.Storable.Memory;

public class Database<S extends Storable> {
	
	public static boolean PRITTY_STORAGE = false;
//	private static final Map<String, Function<Map<String, Object>, Storable>> functions = new HashMap<>();
//	
//	public static <T extends Storable> T deserialize(Map<String, Object> map, Class<T> clazz) {
//		Function<Map<String, Object>, Storable> function = functions.get(getId(clazz));
//		try {
//			return clazz.cast(function.apply(map));
//		} catch (ClassCastException e) {
//			return null;
//		}
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static <T extends Storable> T deserialize(Object object, Class<T> clazz) {
//		return deserialize((Map<String, Object>)object, clazz);
//	} 
//	
//	public static boolean register(Class<?> clazz, Function<Map<String, Object>, Storable> function) {
//		String id = getId(clazz);
//		if(functions.containsKey(id)) {
//			return false;
//		} else {
//			functions.put(id, function);
//			return true;
//		}
//	}
//	
//	private static String getId(Class<?> clazz) {
//		String id = clazz.getName();
//		if(clazz.isAnnotationPresent(Serial.class)) {
//			Serial ser = clazz.getAnnotation(Serial.class);
//			if(ser != null) {
//				id = ser.value();
//			}
//		}
//		return id;
//	}
//	
//	
	
    private final String name;
    private final File file;
    private final Function<String, S> serialFunction;
    private final Class<S> clazz;
    private final Object lock = new Object();
	
    public Database(String name, File file, Function<String, S> serialFunction, Class<S> clazz) {
        checkNotNull(name, "name");
        checkNotNull(file, "file");
        checkNotNull(serialFunction, "function");
        checkNotNull(clazz, "clazz");
        this.name = name;
        this.file = file;
        this.serialFunction = serialFunction;
        this.clazz = clazz;
    }
    
	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public S load() throws StorageException {
		synchronized (lock) {
			try {
				return (S) Memory.deserialize(new Gson().fromJson(new JsonParser().parse(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)))), Map.class), clazz);
			} catch (JsonIOException | JsonSyntaxException | IOException e) {
				return serialFunction.apply(name);
			}
		}
	}
	
	public void save(S data) throws StorageException {
        checkNotNull(data);

        synchronized (lock) {
            File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");

            if(!tempFile.exists() && tempFile.getParentFile() != null)
            	tempFile.getParentFile().mkdirs();
            
            GsonBuilder gsonBuilder = new GsonBuilder();
            if(PRITTY_STORAGE)
            	gsonBuilder.setPrettyPrinting();
            String json = gsonBuilder.create().toJson(data.serialize());
            
            try {
            	GZIPOutputStream stream = new GZIPOutputStream(new FileOutputStream(tempFile));
            	byte[] bytes = json.getBytes();
            	stream.write(bytes);
            	stream.flush();
            	stream.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            file.delete();
            if (!tempFile.renameTo(file)) {
                throw new StorageException("Failed to rename temporary file to " + file.getAbsolutePath());
            }
		}
	}
}
