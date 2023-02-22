/*
 *
 *     Copyright (C) 2019  Salkin (mc.salkin@gmail.com)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.settla.memory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class MemoryObject<M extends MemoryStorable<?>> implements Runnable {

    private final Supplier<M> initial;
    private final File file;
    private final Class<M> clazz;
    private final boolean fancy = true;
    private final Object lock = new Object();
    private M object;

    public MemoryObject(File file, Class<M> clazz, Supplier<M> initial) {
        checkNotNull(file);
        checkNotNull(clazz);
        checkNotNull(initial);
        this.initial = initial;
        this.file = file;
        this.clazz = clazz;
    }

    public M object() {
        return object;
    }

    public boolean loaded() {
        return object() != null;
    }

    private M read() throws MemoryException {
        synchronized (lock) {
            if (file.exists()) {
                try {
                    return fancy
                            ? Memory.deserialize(new JsonParser().parse(new InputStreamReader(new FileInputStream(file))).getAsJsonObject(), clazz)
                            : Memory.deserialize(new JsonParser().parse(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)))).getAsJsonObject(), clazz);
                } catch (MemoryException e) {
                    e.printStackTrace();
                    M o = initial.get();
                    o.setDirty(true);
                    return o;
                } catch (Exception ignored) {
                }
            }
            System.out.println("No correct data file found ...\nCreate initial object in file '" + file.getName() + "'...");
            M o = initial.get();
            o.setDirty(true);
            return o;
        }
    }

    private void write(M data) throws MemoryException {
        checkNotNull(data);
        synchronized (lock) {
            File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");

            if (!tempFile.exists() && tempFile.getParentFile() != null)
                tempFile.getParentFile().mkdirs();

            try {
                OutputStreamWriter outputStreamWriter = fancy
                        ? new OutputStreamWriter(new FileOutputStream(tempFile))
                        : new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(tempFile)));
                Gson gson = fancy
                        ? new GsonBuilder().setPrettyPrinting().create()
                        : new Gson();
                gson.toJson(data.serialize(), outputStreamWriter);
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File last = new File(file.getParentFile(), file.getName() + ".last");

            if (last.exists())
                last.delete();

            if (!file.exists() || (file.exists() && file.renameTo(last)))
                if (tempFile.renameTo(file)) {
                    return;
                }
            throw new MemoryException("Failed to rename temporary file to " + file.getAbsolutePath());
        }
    }

    @Override
    public void run() {
        synchronized (lock) {
            if (object == null) {
                try {
                    System.out.println("Start reading file '" + file.getName() + "' and generating object...");
                    object = read();
                    System.out.println("File '" + file.getName() + "' successfully loaded!");
                } catch (MemoryException e) {
                    e.printStackTrace();
                }

            } else {
                if (object.isDirty()) {
                    try {
                        System.out.println("Start writing file '" + file.getName() + "'...");
                        long t1 = System.currentTimeMillis();
                        write(object);
                        object.setDirty(false);
                        long t2 = System.currentTimeMillis();
                        System.out.println("File '" + file.getName() + "' successfully saved! (" +(t2-t1)+ "ms)");
                    } catch (MemoryException e) {
                        e.printStackTrace();
                    }
                } else {
//                    System.out.println("File '" + file.getName() + "' already saved!");
                }
            }
        }
    }
}
