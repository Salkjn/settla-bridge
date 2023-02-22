package de.settla.global.warp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("WarpPoints")
public class WarpPoints implements Storable {

	private List<WarpPoint> warps = new ArrayList<>();
	private List<String> warpNames;
	private final Object lock = new Object();
	
	private boolean dirty;
	
	public WarpPoints() {
		
	}
	
	public WarpPoints(List<WarpPoint> points) {
		this.warps = points;
	}
	
	@SuppressWarnings("unchecked")
	public WarpPoints(Map<String, Object> map) {
		((List<Map<String, Object>>)map.get("warps")).forEach(s -> warps.add(deserialize(s, WarpPoint.class)));
	}
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("warps", warps.stream().map(WarpPoint::serialize).collect(Collectors.toList()));
			return map;
		}	
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public WarpPoint getWarp(String name) {
		synchronized (lock) {
			return warps.stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
		}
	}
	
	public List<WarpPoint> getWarpsOfServer(String server) {
		synchronized (lock) {
			return warps.stream().filter(g -> g.getServer().equalsIgnoreCase(server)).collect(Collectors.toList());
		}
	}
	
	public boolean addWarp(WarpPoint point) {
		synchronized (lock) {
			setDirty(true);
			if(getWarp(point.getName()) == null) {
				warpNames = null;
				return warps.add(point);
			} else {
				return false;
			}
		}
	}
	
	public boolean removeWarp(WarpPoint point) {
		synchronized (lock) {
			setDirty(true);
			warpNames = null;
			return warps.remove(point);
		}
	}
	
	public void removeWarp(String name) {
		synchronized (lock) {
			setDirty(true);
			warpNames = null;
			warps = warps.stream().filter(p -> !p.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
		}
	}
	
	public WarpPoints getWarpPoints(String server) {
		synchronized (lock) {
			return new WarpPoints(warps.stream().filter(p -> p.getServer().equalsIgnoreCase(server)).collect(Collectors.toList()));
		}
	}
	
	public List<String> getWarps() {
		synchronized (lock) {
			return warpNames == null ? warpNames = warps.stream().map(w -> w.getName()).collect(Collectors.toList()) : warpNames;
		}
	}
	
}
