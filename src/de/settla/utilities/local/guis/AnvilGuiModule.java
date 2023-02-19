package de.settla.utilities.local.guis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.guis.AnvilGUI.AnvilClickEvent;
import de.settla.utilities.local.guis.AnvilGUI.AnvilSlot;
import de.settla.utilities.local.guis.GuiParser.RawInventory;
import de.settla.utilities.local.guis.Guis.AItemStack;
import de.settla.utilities.local.guis.Guis.IGui;
import de.settla.utilities.local.guis.Guis.ISpine;
import de.settla.utilities.local.guis.Guis.Page;
import de.settla.utilities.local.guis.Guis.Spine;
import de.settla.utilities.module.Module;

public class AnvilGuiModule extends Module<LocalPlugin> {

	private final Set<AnvilGUI> anvils = new HashSet<>();
	private final Object lock = new Object();
	
	public AnvilGuiModule(LocalPlugin plugin) {
		super(plugin);
	}
	
	@Override
	public void onEnable() {
		getModuleManager().registerListener(new Listener() {
			
			@EventHandler
			public void onInventoryClick(InventoryClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {
					@SuppressWarnings("unused")
					Player clicker = (Player) event.getWhoClicked();

					AnvilGUI gui = getAnvilGui(event.getInventory());
					
					if (gui != null) {
						event.setCancelled(true);

						ItemStack item = event.getCurrentItem();
						int slot = event.getRawSlot();
						String name = null;

						if (item != null) {
							if (item.hasItemMeta()) {
								ItemMeta meta = item.getItemMeta();

								if (meta.hasDisplayName()) {
									name = meta.getDisplayName();
								}
							}
						}

						AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);
						gui.getAnvilClickEventHandler().onAnvilClick(clickEvent);
						if (clickEvent.getWillClose()) {
							event.getWhoClicked().closeInventory();
						}
						if (clickEvent.getWillDestroy()) {
							gui.destroy();
							synchronized (lock) {
								anvils.remove(gui);
							}
						}
					}
				}
			}

			@EventHandler
			public void onInventoryClose(InventoryCloseEvent event) {
				if (event.getPlayer() instanceof Player) {
					@SuppressWarnings("unused")
					Player player = (Player) event.getPlayer();
					Inventory inv = event.getInventory();

					AnvilGUI gui = getAnvilGui(inv);
					if (gui != null) {
						inv.clear();
						gui.destroy();
						synchronized (lock) {
							anvils.remove(gui);
						}
					}
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent event) {
				AnvilGUI gui = getAnvilGui(event.getPlayer());
				if (gui != null) {
					gui.destroy();
					synchronized (lock) {
						anvils.remove(gui);
					}
				}
			}
		});
	}
	
	@Override
	public void onDisable() {
		synchronized (lock) {
			Iterator<AnvilGUI> ite = anvils.iterator();
			while (ite.hasNext()) {
				AnvilGUI next = ite.next();
				next.getPlayer().closeInventory();
				next.destroy();
			}
		}
	}
	
	public AnvilGUI getAnvilGui(Inventory inventory) {
		synchronized (lock) {
			return anvils.stream().filter(a -> inventory.equals(a.getInventory())).findFirst().orElse(null);
		}
	}
	
	public AnvilGUI getAnvilGui(Player player) {
		synchronized (lock) {
			return anvils.stream().filter(a -> player.equals(a.getPlayer())).findFirst().orElse(null);
		}
	}

	public void addAnvilGui(AnvilGUI gui) {
		synchronized (lock) {
			anvils.add(gui);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class AcceptPage extends Page {
		public AcceptPage(IGui gui, ISpine spine, Map<Integer, AItemStack> items, String accept_item, String deny_item, Consumer<String> accept, Consumer<String> deny, String string) {
			super(gui, null, spine, items);
			addAction(accept_item, (item) -> {
				gui().close();
				accept.accept(string);
			});
			addAction(deny_item, (item) -> {
				gui().close();
				deny.accept(string);
			});
		}
	}
	
	public static void openAnvilAcceptGui(Player player, String inventory_name, String paper_item, String accept_item, String deny_item, Consumer<String> accept, Consumer<String> deny) {
		AnvilGUI anvil = new AnvilGUI(player, e -> {
			String name = e.getName();
			switch (e.getSlot()) {
			case INPUT_LEFT:
				e.setWillClose(false);
				e.setWillDestroy(false);
				break;
			case INPUT_RIGHT:
				e.setWillClose(false);
				e.setWillDestroy(false);
				break;
			case OUTPUT:
				e.setWillClose(true);
				e.setWillDestroy(true);
				if(name == null) {
				} else {
					new BukkitRunnable() {
						@Override
						public void run() {
							new SimpleGui(LocalPlugin.getInstance().getModule(GuiModule.class).getGuis(), player, g -> {
								GuiParser parser = LocalPlugin.getInstance().getModule(GuiModule.class).getParser();
								RawInventory raw = parser.getInventory(inventory_name);
								Spine s = new Spine(raw.getSpine().lines(), raw.getSpine().title() + name);
								return new AcceptPage(g, s, raw.buildItems(parser), accept_item, deny_item, accept, deny, name);
							}).main().open();
						}
					}.runTaskLater(LocalPlugin.getInstance(), 1);
				}
				break;
			default:
				break;
			}
		});
		anvil.setSlot(AnvilSlot.INPUT_LEFT, LocalPlugin.getInstance().getModule(GuiModule.class).getParser().getItem(paper_item).build().getItemStack());
		LocalPlugin.getInstance().getModule(AnvilGuiModule.class).addAnvilGui(anvil);
		anvil.open();
	}
	
	
}
