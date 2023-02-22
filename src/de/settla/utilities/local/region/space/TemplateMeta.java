package de.settla.utilities.local.region.space;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.util.Map;

import de.settla.utilities.local.region.Normal;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;

@Serial("TemplateMeta")
public class TemplateMeta implements Storable {

	private final String id;
	private final Room room;
	private final Form form;
	private final Vector offset;
	private final Vector origin;

	private final Object lock = new Object();
	
	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = Storable.super.serialize();
			map.put("form", form.serialize());
			map.put("offset", offset.serialize());
			map.put("origin", origin.serialize());
			map.put("room", room.serialize());
			map.put("id", id);
			return map;
		}
	}
	
	@SuppressWarnings("unchecked")
	private TemplateMeta(Map<String, Object> map) {
		this.id = (String) map.get("id");
		this.form = Form.deserialize((Map<String, Object>) map.get("form"));
		this.room = deserialize(map.get("room"), Room.class);
		this.offset = deserialize(map.get("offset"), Vector.class);
		this.origin = deserialize(map.get("origin"), Vector.class);
	}
	
	public static TemplateMeta loadTemplate(TemplateModule module, String id) throws FileNotFoundException, DataException {
		checkNotNull(module);
		checkNotNull(id);

		id = Normal.normalize(id);
		
		Map<String, Object> map = module.loadJson("/templates/"+id+".template");
		
		return new TemplateMeta(map);
	}

	public String getId() {
		return id;
	}

	public Room getRoom() {
		return room;
	}

	public Form getForm() {
		return form;
	}

	public Vector getOffset() {
		return offset;
	}

	public Vector getOrigin() {
		return origin;
	}
	
}
