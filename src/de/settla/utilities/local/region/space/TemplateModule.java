package de.settla.utilities.local.region.space;

import java.io.FileNotFoundException;

import javax.annotation.Nullable;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.Library;
import de.settla.utilities.local.region.space.blocks.NormalBlockList;
import de.settla.utilities.local.region.space.blocks.SimpleBlockList;
import de.settla.utilities.module.Module;
import de.settla.utilities.storage.Storable.Memory;

public class TemplateModule extends Module<LocalPlugin> {

	private final Library<Template> templates = new Library<>();
	
	public TemplateModule(LocalPlugin plugin) {
		super(plugin);
		register();
	}

	private void register() {
		Memory.register(NormalBlockList.class, map -> new NormalBlockList((short[][])map.get("blocks")));
		Memory.register(SimpleBlockList.class, map -> new SimpleBlockList((short[][])map.get("blocks")));
		Memory.register(Room.class, map -> new Room(map));
	}
	
	public boolean isLoaded(String id) {
		return templates.contains(id);
	}
	
	@Nullable
	public Template getTemplate(String id) {
		return getOrLoadTemplate(id, false);
	}
	
	@Nullable
	public Template getOrLoadTemplate(String id, boolean load) {
		Template template = templates.get(id);
		if(template == null && load) {
			try {
				template = Template.loadTemplate(this, id);
			} catch (FileNotFoundException | DataException e) {
				return null;
			}
			templates.put(id, template);
		}
		return template;
	}
	
}
