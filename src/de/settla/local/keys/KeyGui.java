package de.settla.local.keys;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.guis.GuiModule;
import de.settla.utilities.local.guis.GuiParser;
import de.settla.utilities.local.guis.GuiParser.RawInventory;
import de.settla.utilities.local.guis.Guis;
import de.settla.utilities.local.guis.Guis.AItemStack;
import de.settla.utilities.local.guis.Guis.Gui;
import de.settla.utilities.local.guis.Guis.IGui;
import de.settla.utilities.local.guis.Guis.IPage;
import de.settla.utilities.local.guis.Guis.ISpine;
import de.settla.utilities.local.guis.Guis.Page;
import de.settla.utilities.local.guis.Guis.Spine;

public class KeyGui extends Gui {

	private final Key key;
    private boolean ready = false;
    private boolean viewing = false;

    public KeyGui(Guis guis, Player player, Key key) {
 		super(guis, player, null, null);
 		this.key = key;
 		GuiParser parser = LocalPlugin.getInstance().getModule(GuiModule.class).getParser();
		RawInventory raw = parser.getInventory("key");
		Spine spine = raw.getSpine();
		spine = new Spine(spine.lines(), ChatColor.translateAlternateColorCodes('&', key.getName() + spine.title()));
		setMain(new KeyPage(this, null, spine, raw.buildItems(parser)));
 	}
    
    class KeyPage extends Page {

       
    	public KeyPage(IGui gui, IPage root, ISpine spine, Map<Integer, AItemStack> items) {
			super(gui, root, spine, items);
		}

		@Override
    	public void onPageClickEvent(InventoryClickEvent event) {
    		event.setCancelled(true);
            if(ready && event.getRawSlot() == KeyModule.UPDATE_SLOT) {
                ready = false;
                close();
                final ItemStack item = event.getCurrentItem();
                final Player player = (Player) event.getWhoClicked();
//                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1); todo: 1.8 sounds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(player.isOnline()) {
                            //TODO
                            KeyModule.getInstance().sendMessage("Player:"+player.getName()+" Key:"+key.getId()+" Result:"+(item.getItemMeta().getDisplayName() == null ? Utils.prettifyText(item.getType().toString()) : item.getItemMeta().getDisplayName())+" Amount:"+item.getAmount());
                            player.sendMessage(KeyModule.convert(KeyModule.MESSAGE_SUCCESS_KEY, new String[][]{{"player",player.getName()}
                                    ,{"key",key.getName()}
                                    ,{"amount", item.getAmount()+""}
                                    ,{"item", item.getItemMeta().getDisplayName() == null ? Utils.prettifyText(item.getType().toString()) : item.getItemMeta().getDisplayName()}}));
                            KeyModule.getInstance().giveItemStack(player, Guis.unsafe(item));
//                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1); 
                        }
                    }
                }.runTaskLaterAsynchronously(LocalPlugin.getInstance(), 20L);
            }
    	}
    	
    	@Override
    	public void onPageCloseEvent(InventoryCloseEvent event) {
    		  viewing = false;
    	}
    	
    	@Override
    	public void onPageDragEvent(InventoryDragEvent event) {
    		event.setCancelled(true);
    	}

        @Override
        public void open() {
            viewer().sendMessage(KeyModule.convert(KeyModule.MESSAGE_REDEEM_KEY, new String[][]{{"player",viewer().getName()},{"key",key.getName()}}));
            super.open();
            viewing = true;
            new BukkitRunnable() {
                int i = 0;
                @Override
                public void run() {
                    if(viewer().isOnline() && viewing && i < ((20D/((double)KeyModule.UPDATE_SPEED)))*((double)KeyModule.UPDATE_TIME)) {
                        update();
                        i += (20D/((double)KeyModule.UPDATE_SPEED));
                    } else {
                        cancel();
                        ready = true;
                    }
                }
            }.runTaskTimerAsynchronously(LocalPlugin.getInstance(), 0L, KeyModule.UPDATE_SPEED);
        }

        public void update() {
            getInventory().setItem(KeyModule.UPDATE_SLOT, Guis.safe(key.getRandomItem().getItemStack().clone()));
        }
    }
}