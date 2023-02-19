package de.settla.utilities.storage;

import java.util.Map;

@Serial("Tuple")
public class StringTuple implements Storable {

	private final String first, second;

	public StringTuple(String first, String second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public StringTuple(Map<String, Object> map) {
		super();
		this.first = (String) map.get("1");
		this.second = (String) map.get("2");
	}

	public String getFirst() {
		return first;
	}

	public String getSecond() {
		return second;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("1", first);
		map.put("2", second);
		return map;
	}
	
}
