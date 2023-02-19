package de.settla.utilities.local.region.space.generation;

import java.util.Map;

import de.settla.utilities.local.region.Region;
import de.settla.utilities.local.region.form.Form;

public class EmptyRegion extends Region {

	public EmptyRegion(Form form, String id) {
		super(form, 0, true, id);
	}

	public EmptyRegion(Map<String, Object> map) {
		super(map);
	}

	@Override
	public boolean isDirty() {
		return false;
	}
	
}
