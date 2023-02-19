package de.settla.utilities.local.region.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import de.settla.utilities.local.region.Universe;

public class InternalEventListener implements Listener {

	private Universe plugin;

	public InternalEventListener(Universe plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin.getModuleManager());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void v(FoodLevelChangeEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void v(VehicleEntityCollisionEvent e) {
		plugin.fire(e, e.getVehicle().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void v(VehicleCreateEvent e) {
		plugin.fire(e, e.getVehicle().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void v(VehicleEnterEvent e) {
		plugin.fire(e, e.getVehicle().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void v(VehicleExitEvent e) {
		plugin.fire(e, e.getVehicle().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void v(VehicleDamageEvent e) {
		plugin.fire(e, e.getVehicle().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void h(HangingBreakEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void h(HangingPlaceEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerPortalEvent e) {
		plugin.fire(e, e.getFrom());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerLoginEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerToggleFlightEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerToggleSneakEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerInteractEntityEvent e) {
		plugin.fire(e, e.getRightClicked().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerInteractEvent e) {
		if (e.hasBlock()) {
			plugin.fire(e, e.getClickedBlock().getLocation());
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
				plugin.fire(e, e.getClickedBlock().getRelative(e.getBlockFace()).getLocation());
		} else {
			plugin.fire(e, e.getPlayer().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerItemBreakEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerItemConsumeEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerItemDamageEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerItemHeldEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerLeashEntityEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerLevelChangeEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerKickEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerShearEntityEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerPickupItemEvent e) {
		plugin.fire(e, e.getItem().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerToggleSprintEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerBucketFillEvent e) {
		plugin.fire(e, e.getBlockClicked().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerQuitEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerJoinEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerArmorStandManipulateEvent e) {
		plugin.fire(e, e.getRightClicked().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerBedEnterEvent e) {
		plugin.fire(e, e.getBed().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerBedLeaveEvent e) {
		plugin.fire(e, e.getBed().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerBucketEmptyEvent e) {
		plugin.fire(e, e.getBlockClicked().getRelative(e.getBlockFace()).getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerDeathEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerDropItemEvent e) {
		plugin.fire(e, e.getItemDrop().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerEditBookEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerEggThrowEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerCommandPreprocessEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerGameModeChangeEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerExpChangeEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerFishEvent e) {
		if(e.getCaught() != null) {
			plugin.fire(e, e.getCaught().getLocation());
		} else {
			plugin.fire(e, e.getPlayer().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerInteractAtEntityEvent e) {
		plugin.fire(e, e.getRightClicked().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void p(PlayerUnleashEntityEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void i(InventoryClickEvent e) {
		plugin.fire(e, e.getWhoClicked().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void i(InventoryCloseEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void i(InventoryCreativeEvent e) {
		plugin.fire(e, e.getWhoClicked().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void i(InventoryDragEvent e) {
		plugin.fire(e, e.getWhoClicked().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void i(InventoryInteractEvent e) {
		plugin.fire(e, e.getWhoClicked().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void i(InventoryOpenEvent e) {
		plugin.fire(e, e.getPlayer().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void i(InventoryPickupItemEvent e) {
		plugin.fire(e, e.getItem().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityBreakDoorEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityChangeBlockEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityCreatePortalEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityDamageByBlockEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityDamageEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityMountEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityDismountEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityDeathEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityInteractEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityPortalEnterEvent e) {
		plugin.fire(e, e.getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityPortalEvent e) {
		plugin.fire(e, e.getFrom());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityPortalExitEvent e) {
		plugin.fire(e, e.getFrom());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityRegainHealthEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntitySpawnEvent e) {
		plugin.fire(e, e.getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityShootBowEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityTargetEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(EntityUnleashEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void e(CreatureSpawnEvent e) {
		plugin.fire(e, e.getEntity().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void item(ItemDespawnEvent e) {
		plugin.fire(e, e.getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void item(ItemSpawnEvent e) {
		plugin.fire(e, e.getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockBreakEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockPlaceEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockPhysicsEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockRedstoneEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockDamageEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockSpreadEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockMultiPlaceEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockBurnEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockExpEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockFadeEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockFormEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void b(BlockGrowEvent e) {
		plugin.fire(e, e.getBlock().getLocation());
	}
	
//	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//	public void b(BlockIgniteEvent e) {
//		plugin.fire(e, e.getBlock().getLocation());
//	}
	
}
