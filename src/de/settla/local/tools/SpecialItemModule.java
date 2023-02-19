package de.settla.local.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.settla.local.LocalPlugin;
import de.settla.local.tools.tools.BeamItem;
import de.settla.local.tools.tools.ChestLookerItem;
import de.settla.local.tools.tools.MoneyItem;
import de.settla.local.tools.tools.SelectionTool;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.module.Module;

public class SpecialItemModule extends Module<LocalPlugin> {
	
	public static final String NBT_ID = "spitem";
	
	private final Object lock = new Object();
	private final List<SpecialItem<?>> specialItems = new ArrayList<>();
	
	public SpecialItemModule(LocalPlugin plugin) {
		super(plugin);
	}
	
	@Override
	public void onEnable() {
		getModuleManager().getModule(Universe.class).addToWaitingActionList(() -> registerAllEvents());
		getModuleManager().getModule(Universe.class).addToWaitingActionList(() -> getModuleManager().registerCommand(new SpecialItemCommand("tools")));
		
		SelectionTool selectionTool = new SelectionTool(this, "selection");
		this.addSpecialItem(selectionTool);
		
		MoneyItem mon = new MoneyItem(this, "money");
		this.addSpecialItem(mon);
	
		BeamItem bea = new BeamItem(this, "beam");
		this.addSpecialItem(bea);
		
		ChestLookerItem ite = new ChestLookerItem(this, "looker");
		this.addSpecialItem(ite);
		
	}
	
	public void addSpecialItem(SpecialItem<?> specialItem) {
		synchronized (lock) {
			specialItems.add(specialItem);
		}
	}
	
	public void forEach(Consumer<SpecialItem<?>> consumer) {
		synchronized (lock) {
			specialItems.forEach(specialItem -> consumer.accept(specialItem));
		}
	}
	
	public SpecialItem<?> getSpecialItem(String itemId) {
		synchronized (lock) {
			for (SpecialItem<?> specialItem : specialItems) {
				if(specialItem.getItemId().equalsIgnoreCase(itemId))
					return specialItem;
			}
			return null;
		}
	}
	
	public List<String> getToolNames() {
		synchronized (lock) {
			return specialItems.stream().map(tool -> tool.getItemId()).collect(Collectors.toList());
		}
	}
	
	private void registerAllEvents() {
		forEach(specialItem -> specialItem.forEachEvent(event -> getModuleManager().registerListener(event)));
	}
	
}
