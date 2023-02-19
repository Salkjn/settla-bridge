package de.settla.utilities.local.region.space;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.util.Map;

import de.settla.utilities.local.region.Normal;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.storage.Serial;
import de.settla.utilities.storage.Storable;
import de.settla.utilities.storage.StorageException;

@Serial("Template")
public class Template implements Storable {
	
	private final String id;
	private final BlockList blocks;
	private final Room room;
	private final Form form;
	private final Vector offset;
	private final Vector origin;
	private final Object lock = new Object();
	private final TemplateModule module;
	
	public static Template createTemplate(TemplateModule module, String id, BlockList blocks, Room room, Form form, Vector offset, Vector origin) {
		checkNotNull(module);
		checkNotNull(id);
		checkNotNull(blocks);
		checkNotNull(room);
		checkNotNull(form);
		checkNotNull(offset);
		checkNotNull(origin);
		id = Normal.normalize(id);
		return new Template(module, id, blocks, room, form, offset, origin);
	}
	
	public static Template loadTemplate(TemplateModule module, String id) throws FileNotFoundException, DataException {
		checkNotNull(module);
		checkNotNull(id);
		checkNotNull(module);
		id = Normal.normalize(id);
		
		Map<String, Object> map = module.loadJson("/templates/"+id+".template");
		
		return new Template(module, map);
	}
	
	public void saveTemplate() throws StorageException {
		synchronized (lock) {
			BlockList.saveBlockList(id, blocks);
			module.saveJson(serialize(), "/templates/"+id+".template", true);
		}
	}
	
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
	private Template(TemplateModule module, Map<String, Object> map) throws FileNotFoundException, DataException {
		this.module = module;
		this.id = (String) map.get("id");
		this.form = Form.deserialize((Map<String, Object>) map.get("form"));
		this.room = deserialize(map.get("room"), Room.class);
		this.offset = deserialize(map.get("offset"), Vector.class);
		this.origin = deserialize(map.get("origin"), Vector.class);
		this.blocks = BlockList.loadBlockList(id);
	}
	
	private Template(TemplateModule module, String id, BlockList blocks, Room room, Form form, Vector offset, Vector origin) {
		this.module = module;
		this.id = id;
		this.blocks = blocks;
		this.room = room;
		this.form = form;
		this.offset = offset;
		this.origin = origin;
	}
	
	public String getId() {
		return id;
	}

	public BlockList getBlockList() {
		return blocks;
	}
	
	public Clipboard clipboard() {
		synchronized (lock) {
			return  blocks != null && form != null ? new Clipboard(blocks.duplicate(), new Room(room), form, offset, origin) : null;
		}
	}
	
}
