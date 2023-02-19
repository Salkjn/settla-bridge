package de.settla.utilities.sakko.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.settla.utilities.storage.Serial;

@Serial("EmptyAnswer")
public class EmptyAnswer extends SakkoAnswer {

	private static UUID empty = UUID.fromString("00000000-0000-0000-0000-000000000000");

	public EmptyAnswer() {
		super(empty, new HashMap<>());
	}
	
	public EmptyAnswer(Map<String, Object> map) {
		super(map);
	}
	
}
