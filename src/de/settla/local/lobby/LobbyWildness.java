package de.settla.local.lobby;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.material.Openable;
import org.bukkit.material.Redstone;

import de.settla.local.VaultHelper;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.region.Buildable;
import de.settla.utilities.local.region.WildnessRegion;
import de.settla.utilities.storage.Serial;

@Serial("LOBBY_WILDNESS")
public class LobbyWildness extends WildnessRegion implements Buildable {
	
	public LobbyWildness() {
		super();
	}

	public LobbyWildness(Map<String, Object> map) {
		super(map);
	}

	@Override
	public boolean canBuild(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		return player != null && player.getGameMode() == GameMode.CREATIVE && VaultHelper.checkPerm(uuid, "settla.lobby.build");
	}

	@EventHandler
	public void a(PotionSplashEvent event) {
		if (event.getPotion().getShooter() instanceof Player) {
			if (!canBuild(((Player) event.getPotion().getShooter()).getUniqueId())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void a(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void a(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void a(BlockMultiPlaceEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void a(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (!canBuild(player.getUniqueId())) {
			if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
					&& event.getItem() != null
					&& (Utils.checkMaterials(event.getItem().getType(), Material.PAINTING, Material.ITEM_FRAME,
							Material.ARMOR_STAND, Material.BOAT, Material.MINECART, Material.COMMAND_MINECART,
							Material.EXPLOSIVE_MINECART, Material.HOPPER_MINECART, Material.POWERED_MINECART,
							Material.STORAGE_MINECART, Material.EGG, Material.MONSTER_EGG, Material.MONSTER_EGGS,
							Material.INK_SACK, Material.DOUBLE_PLANT, Material.TRIPWIRE_HOOK))) {
				event.setCancelled(true);
				return;
			}

			if (event.hasBlock()) {
				Block block = event.getClickedBlock();
				if (Utils.checkMaterials(block.getType(), Material.DRAGON_EGG, Material.ANVIL, Material.ENDER_CHEST,
						Material.FLOWER_POT)) {
					event.setCancelled(true);
					return;
				}
				if (block.getState().getData() instanceof Redstone
						|| block.getState().getData() instanceof Openable) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void a(PlayerArmorStandManipulateEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void a(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void a(PlayerBedEnterEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void a(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void a(PlayerLeashEntityEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void a(PlayerShearEntityEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void a(PlayerUnleashEntityEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void a(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setCancelled(true);
	}

	@EventHandler
	public void a(HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player) {
			if (!canBuild(event.getRemover().getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			return;
		}

		if (event.getRemover() instanceof Projectile
				&& ((Projectile) event.getRemover()).getShooter() instanceof Player) {
			if (!canBuild(((Player) ((Projectile) event.getRemover()).getShooter()).getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void a(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (!canBuild(event.getDamager().getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			return;
		}

		if (event.getDamager() instanceof Projectile) {
			if(((Projectile) event.getDamager()).getShooter() instanceof Player) {
				if (!canBuild(((Player) ((Projectile) event.getDamager()).getShooter()).getUniqueId())) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void a(VehicleEnterEvent event) {
		if (event.getEntered() instanceof Player && !canBuild(event.getEntered().getUniqueId()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void a(VehicleDamageEvent event) {
		if (event.getAttacker() instanceof Player) {
			if (!canBuild(event.getAttacker().getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			return;
		}

		if (event.getAttacker() instanceof Projectile
				&& ((Projectile) event.getAttacker()).getShooter() instanceof Player) {
			if (!canBuild(((Player) ((Projectile) event.getAttacker()).getShooter()).getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			return;
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void a(VehicleDestroyEvent event) {
		if (event.getAttacker() instanceof Player) {
			if (!canBuild(event.getAttacker().getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			return;
		}

		if (event.getAttacker() instanceof Projectile
				&& ((Projectile) event.getAttacker()).getShooter() instanceof Player) {
			if (!canBuild(((Player) ((Projectile) event.getAttacker()).getShooter()).getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			return;
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void a(VehicleEntityCollisionEvent event) {
		if (event.getEntity() instanceof Player && !canBuild(event.getEntity().getUniqueId()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void a(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void a(PlayerEggThrowEvent event) {
		event.setHatching(false);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void entityExplodeEvent(EntityExplodeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void blockExplodeEvent(BlockExplodeEvent event) {
		event.setCancelled(true);
	}
	
}
