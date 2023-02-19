package de.settla.utilities.storage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Serial("TupleList")
public class TupleList implements Storable {

	private final List<StringTuple> list;

	public TupleList(List<StringTuple> list) {
		super();
		this.list = list;
	}
	
	@SuppressWarnings("unchecked")
	public TupleList(Map<String, Object> map) {
		super();
		this.list = ((List<Map<String, Object>>)map.get("list")).stream().map(m -> deserialize(m, StringTuple.class)).collect(Collectors.toList());
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("list", list.stream().map(t -> t.serialize()).collect(Collectors.toList()));
		return map;
	}

	public List<StringTuple> list() {
		return list;
	}
	
}
