package de.settla.utilities.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SimpleStorage<T> {

    private final Function<T, JsonObject> serialize;
    private final Function<JsonObject, T> deserialize;
    private final Function<Boolean, File> fileFunction;
	
    private State state;
    
    private enum State {
    	READING, SAVING, READY
    }
    
    public SimpleStorage(Function<Boolean, File> fileFunction, Function<T, JsonObject> serialize, Function<JsonObject, T> deserialize) {
		super();
    	this.state = State.READY;
		this.fileFunction = fileFunction;
		this.serialize = serialize;
		this.deserialize = deserialize;
	}
	
    public T read() {
    	if (state == State.READY) {
    		state = State.READING;
    		File file = fileFunction.apply(false);
            if (file.exists()) {
                try {
                    JsonParser parser = new JsonParser();
                    JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
                    state = State.READY;
                    return deserialize.apply(json);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            state = State.READY;
    	} else {
    		System.err.println("SimpleStorage: tries to read object while saving or already reading...");
    	}
        return null;
    }
    
    public void save(T object) {
    	if (state == State.READY) {
    		state = State.SAVING;
    		try {
                File tempFile = fileFunction.apply(true);
                FileWriter writer = new FileWriter(tempFile);
                JsonObject json = serialize.apply(object);
                new Gson().toJson(json, writer);
                writer.flush();
                writer.close();
                File permFile = fileFunction.apply(false);
                if (permFile.exists()) {
                    permFile.delete();
                }
                tempFile.renameTo(permFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
    		state = State.READY;
    	} else {
    		System.err.println("SimpleStorage: tries to save object while reading or already saving...");
    	}
    }

}
