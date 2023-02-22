package de.settla.spigot.universe.cluster;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.settla.spigot.universe.SuperForm;
import de.settla.spigot.universe.Vector;
import de.settla.spigot.universe.cluster.collect.LongHashTable;
import de.settla.spigot.universe.form.Form;

public class Cluster {
	
	private final LongHashTable<ClusterChunk> chunks = new LongHashTable<>();
    private final Object clusterLock = new Object();
    private final BiConsumer<SuperForm, Consumer<ClusterChunk>> throughChunkForms = (form, consumer) -> {
        Vector max = form.getForm().maximum().ceil();
        Vector min = form.getForm().minimum().floor();
        for (int x = ((int) min.getX() >> 4); x <= ((int) max.getX() >> 4); x++) {
            for (int z = ((int) min.getZ() >> 4); z <= ((int) max.getZ() >> 4); z++) {
                consumer.accept(get(x, z, true));
            }
        }
    };
    
    public Cluster() {}
    
    private ClusterChunk get(int x, int z, boolean create) {
        synchronized (clusterLock) {
        	ClusterChunk chunk = chunks.get(x, z);
            if (chunk == null && create) {
            	chunk = new ClusterChunk();
                chunks.put(x, z, chunk);
            }
            return chunk;
        }
    }
	
    public <T extends SuperForm> List<T> getSuperForms(Vector vector, Class<? extends T> clazz) {
        checkNotNull(vector);
        checkNotNull(clazz);
        synchronized (clusterLock) {
        	ClusterChunk chunk = get(vector.getBlockX() >> 4, vector.getBlockZ() >> 4, false);
            if (chunk == null)
                return Collections.emptyList();
            return chunk.getSuperForms(vector, clazz);
        }
    }
    
    public <T extends SuperForm> List<T> getSuperForms(Form form, Class<? extends T> clazz) {
        checkNotNull(form);
        checkNotNull(clazz);
        synchronized (clusterLock) {
            List<T> list = new ArrayList<>();
            throughChunkForms.accept(() -> form, chunk -> {
                for (SuperForm f : chunk.superForms) {
                    if ((f != null && clazz.isAssignableFrom(f.getClass()) && !list.contains(clazz.cast(f)) && f.getForm().intersect(form))) {
                        list.add(clazz.cast(f));
                    }
                }
            });
            return list;
        }
    }
    
    private void add(SuperForm form, ClusterRegistry registry) {
        synchronized (clusterLock) {
            throughChunkForms.accept(form, chunk -> chunk.add(form, registry));
        }
    }
	
    public ClusterRegistry openRegistry(SuperForm form) {
    	checkNotNull(form);
    	return new ClusterRegistry(form);
    }
    
    private class ClusterChunk {

        private final Set<SuperForm> superForms = new HashSet<>();
        private final Object chunkLock = new Object();

        private ClusterChunk() {}

        private void add(SuperForm form, ClusterRegistry registry) {
            synchronized (chunkLock) {
            	registry.chunks.add(this);
                this.superForms.add(form);
            }
        }
        
        private <T extends SuperForm> List<T> getSuperForms(Vector vector, Class<? extends T> clazz) {
            synchronized (chunkLock) {
                return this.superForms.stream().filter(form -> form.getForm().overlaps(vector)).filter(form -> clazz.isAssignableFrom(form.getClass())).map(clazz::cast).collect(Collectors.toList());
            }
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + chunkLock.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ClusterChunk other = (ClusterChunk) obj;
            return chunkLock.equals(other.chunkLock);
        }
        
    }
    
	public class ClusterRegistry {
    	
    	private final Set<ClusterChunk> chunks = new HashSet<>();
        private final SuperForm form;
        private final Object lock = new Object();
        
        private ClusterRegistry(SuperForm form) {
            this.form = form;
        }
        
        public void register() {
            synchronized (lock) {
            	add(form, this);
            }
        }

        public void unregister() {
            synchronized (lock) {
                Iterator<ClusterChunk> ite = chunks.iterator();
                while (ite.hasNext()) {
                	ClusterChunk chunk = ite.next();
                    chunk.superForms.remove(form);
                    ite.remove();
                }
            }
        }
    }
}
