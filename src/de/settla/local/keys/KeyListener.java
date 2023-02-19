package de.settla.local.keys;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.settla.local.LocalPlugin;
import de.settla.local.tools.nbt.NBTItem;
import de.settla.utilities.local.guis.GuiModule;

public class KeyListener implements Listener {

    private final KeyModule module;

    public KeyListener(KeyModule module) {
        this.module = module;
    }

    @EventHandler
    public void e(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK /*&& event.getHand() == EquipmentSlot.HAND*/ && event.hasBlock()) { //Hand does not exist as of 1.8
            Key key = module.getKey(event.getClickedBlock().getLocation());
            if(key == null)
                return;
            event.setCancelled(true);

            if(event.hasItem()) {
                ItemStack item = event.getItem();
                NBTItem nbt = new NBTItem(item);
                String id = nbt.getString("id");
                if(id != null && !id.isEmpty() && id.equalsIgnoreCase(key.getId())) {
                    ItemStack itemClone = item.clone();
                    itemClone.setAmount(1);
                    player.getInventory().removeItem(itemClone);
                    //player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 10));
                    player.updateInventory();
                    key.openKey(player);
                    return;
                }
            }
            //boost
            player.setVelocity(player.getLocation().getDirection().normalize().multiply(-1 * key.getKnockback()));
            player.sendMessage(KeyModule.convert(KeyModule.MESSAGE_NO_KEY, new String[][]{{"player",player.getName()},{"key",key.getName()}}));
//            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1); todo: 1.8 sounds
            return;
        }
        if(event.getAction() == Action.LEFT_CLICK_BLOCK /*&& event.getHand() == EquipmentSlot.HAND */&& event.hasBlock()) {
            Key key = module.getKey(event.getClickedBlock().getLocation());
            if(key == null)
                return;
            event.setCancelled(true);
            new KeyShowGui(LocalPlugin.getInstance().getModule(GuiModule.class).getGuis(), player, key).main().open();
        }
    }
}
