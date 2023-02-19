package de.settla.utilities.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonSerialization<T> {

    private Function<T, JsonObject> serialize;
    private Function<JsonObject, T> deserialize;
    private Function<Boolean, File> fileFunction;
    private Consumer<JsonObject> additionalSerialization, additionalDeserialization;

    public JsonSerialization(Function<T, JsonObject> serialize, Function<JsonObject, T> deserialize,
                             Function<Boolean, File> fileFunction) {
        this.serialize = serialize;
        this.deserialize = deserialize;
        this.fileFunction = fileFunction;
    }

    public JsonSerialization(Function<T, JsonObject> serialize, Function<JsonObject, T> deserialize,
                             Function<Boolean, File> fileFunction, Consumer<JsonObject> additionalSerialization,
                             Consumer<JsonObject> additionalDeserialization) {
        this.serialize = serialize;
        this.deserialize = deserialize;
        this.fileFunction = fileFunction;
        this.additionalSerialization = additionalSerialization;
        this.additionalDeserialization = additionalDeserialization;
    }

    private boolean saving = false;
    public void save(List<T> toSave) {
        if (toSave != null && !saving) {
            saving = true;
            try {
                File tempFile = fileFunction.apply(true);
                FileWriter writer = new FileWriter(tempFile);
                JsonArray array = new JsonArray();
                for (T saveMe : toSave) {
                    try {
                        JsonObject serialized = serialize.apply(saveMe);
                        if (serialized != null)
                            array.add(serialized);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                JsonObject json = new JsonObject();
                if (additionalSerialization != null) {
                    try {
                        additionalSerialization.accept(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                json.add("arr", array);
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
            saving = false;
        }
    }

    public List<T> read() {
        File file = fileFunction.apply(false);
        if (file.exists()) {
            try {
                JsonParser parser = new JsonParser();
                JsonObject json = parser.parse(new FileReader(file)).getAsJsonObject();
                JsonArray array = json.get("arr").getAsJsonArray();
                int size = array.size();
                List<T> toSave = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    try {
                        toSave.add(deserialize.apply(array.get(i).getAsJsonObject()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Could not deserialize element at index " + i + " which was:" + array.get(i).getAsJsonObject().toString());
                    }
                }
                if (additionalDeserialization != null) {
                    try {
                        additionalDeserialization.accept(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return toSave;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

}
