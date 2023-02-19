package de.settla.utilities.local.region.form;

import java.util.HashMap;
import java.util.Map;

public interface FormSerializable extends Formable {
	
	default Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("type", ((Formable)this).type());
		return map;
	}

}
