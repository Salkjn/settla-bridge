package de.settla.utilities.local.guis;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import de.settla.utilities.local.guis.Guis.AItemStack;
import de.settla.utilities.local.guis.Guis.IGui;
import de.settla.utilities.local.guis.Guis.IPage;
import de.settla.utilities.local.guis.Guis.ISpine;
import de.settla.utilities.local.guis.Guis.Page;

abstract public class ScrollPage<T> extends Page {

	private int page = 1;
	private ItemStack item_page;

	public ScrollPage(IGui gui, IPage root, ISpine spine, Map<Integer, AItemStack> items) {
		super(gui, root, spine, items);
	}
	
	public int getPageNumber() {
		return page;
	}

	@Override
	public void open() {
		super.open();
		update(1);
	}
	
	abstract public List<T> getList();
	abstract public int[] getArrangement();
	abstract public ItemStack getScrollItem(T element);
	public void clickItem(T element) {}
	public void clickItem(InventoryClickEvent e, ItemStack item, T element) {
		clickItem(element);
	}
	
	abstract public String getPageNumberItemId();
	abstract public String getNextPageItemId();
	abstract public String getLastPageItemId();
	
	final protected boolean update(int newpage) {
		if((newpage-1) * getArrangement().length >= getList().size() || newpage <= 0)
			return false;
		page = newpage;
		int start_index = (page-1) *  getArrangement().length;
		if(item_page == null)
			item_page = this.getItem(getPageNumberItemId());
		
		for (int i = 0; i < getArrangement().length; i++) {
			int position = getArrangement()[i];
			int index = i + start_index;
			ItemStack item = index < getList().size() ? getScrollItem(getList().get(index)) : null;
			this.setItem(position, item);
		}
		if(item_page != null) {
			item_page.setAmount(page);
		}
		gui().viewer().updateInventory();
		return true;
	}
	
	@Override
	public void onPageClickEvent(InventoryClickEvent event) {
		super.onPageClickEvent(event);
		event.setCancelled(true);
		ItemStack itemstack = event.getCurrentItem();
		
		if(itemstack != null && (itemstack.getType() != Material.AIR)) {
			NBTItem nbt = new NBTItem(itemstack);
			
			String id = nbt.getString("id");
			
			if(!(id == null || id.isEmpty())) {
				if (id.equalsIgnoreCase(getNextPageItemId())) {
					update(page + 1);
//					if(update(page + 1))
//						super.open();
				} else if (id.equalsIgnoreCase(getLastPageItemId())) {
					update(page - 1);
//					if(update(page - 1))
//						super.open();
				}
			}
		}
		
		int slot = event.getRawSlot();
		
		int start_index = (page-1) *  getArrangement().length;
		for (int i = 0; i < getArrangement().length; i++) {
			int position = getArrangement()[i];
			if(position == slot) {
				int index = i + start_index;
				if(index < getList().size())
					clickItem(event, itemstack, getList().get(index));
				return;
			}
		}	
	}
	
	@Override
	public void onPageDragEvent(InventoryDragEvent event) {
		event.setCancelled(true);
	}
}
