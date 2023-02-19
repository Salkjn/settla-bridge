package de.settla.utilities.local.guis;

import java.util.Map;

import org.bukkit.event.inventory.InventoryClickEvent;

import de.settla.utilities.local.guis.Guis.AItemStack;
import de.settla.utilities.local.guis.Guis.IGui;
import de.settla.utilities.local.guis.Guis.IPage;
import de.settla.utilities.local.guis.Guis.ISpine;
import de.settla.utilities.local.guis.Guis.Page;

public abstract class ConfirmationPage<T> extends Page {
	
	private final T object;
	
	public ConfirmationPage(IGui gui, IPage root, ISpine spine, Map<Integer, AItemStack> items, T object) {
		super(gui, root, spine, items);
		this.object = object;
		addAction(getAcceptItemId(), item -> onAccept());
		addAction(getDenyItemId(), item -> onDeny());
	}

	public T getObject() {
		return (T) this.object;
	}

	@Override
	public void onPageClickEvent(InventoryClickEvent event) {
		super.onPageClickEvent(event);
	}
	
	public abstract String getAcceptItemId();

	public abstract String getDenyItemId();

	public abstract void onAccept();

	public abstract void onDeny();
}