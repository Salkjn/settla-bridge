package de.settla.utilities.local.region.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Directional;
import org.bukkit.projectiles.BlockProjectileSource;

import de.settla.utilities.local.region.Galaxy;
import de.settla.utilities.local.region.Region;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.local.region.World;
import de.settla.utilities.local.region.events.BlockRegionChangeEvent.Case;
import de.settla.utilities.local.region.form.Vector;

public class ExternalEventListener implements Listener {

	private Universe plugin;
	
	public ExternalEventListener(Universe plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin.getModuleManager());
	}
	
	public static void makeCodingGreatAgain(BiFunction<List<Region>, List<Region>, ? extends RegionChangeEvent> function,
			String worldname, Vector from, Vector to, Galaxy galaxy) {
		World world = galaxy.getWorld(worldname);
		if (world != null) {
			List<Region> fromRegions = world.getChunkManager().getRegions(from);
			List<Region> toRegions = world.getChunkManager().getRegions(to);
			
			if(fromRegions.size() == toRegions.size()) {
				if(fromRegions.isEmpty())
					return;
				for (Region region : toRegions) {
					if(!fromRegions.contains(region)) {
						galaxy.fireWildness(world, function.apply(Collections.unmodifiableList(fromRegions),
								Collections.unmodifiableList(toRegions)));
						return;
					}
				}
				for (Region region : fromRegions) {
					if(!toRegions.contains(region)) {
						galaxy.fireWildness(world, function.apply(Collections.unmodifiableList(fromRegions),
								Collections.unmodifiableList(toRegions)));
						return;
					}
				}
			} else {
				galaxy.fireWildness(world, function.apply(Collections.unmodifiableList(fromRegions),
						Collections.unmodifiableList(toRegions)));
			}
			toRegions.clear(); // At the end, clear all elements
			fromRegions.clear(); // At the end, clear all elements
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void playerMoveEvent(PlayerMoveEvent event) {
		if (plugin.isLoaded()) {
			Player player = event.getPlayer();
			String worldname = event.getTo().getWorld().getName();
			Vector from = new Vector(event.getFrom());
			Vector to = new Vector(event.getTo());
			Cancel cancel = new Cancel();
			plugin.consumeGalaxys(registery -> makeCodingGreatAgain(
					(a, b) -> new PlayerRegionChangeEvent(cancel, player, a, b), worldname, from, to, registery));
			if (cancel.isCancelled()) {
				// do something
			}
		}
		plugin.fire(event, event.getFrom());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void entityTeleportEvent(EntityTeleportEvent event) {
		if (plugin.isLoaded()) {
			Entity entity = event.getEntity();
			String worldname = event.getTo().getWorld().getName();
			Vector from = new Vector(event.getFrom());
			Vector to = new Vector(event.getTo());
			Cancel cancel = new Cancel();
			plugin.consumeGalaxys(registery -> makeCodingGreatAgain(
					(a, b) -> new EntityRegionChangeEvent(cancel, entity, a, b), worldname, from, to, registery));
			if (cancel.isCancelled()) {
				event.setCancelled(true);
			}
		}
		plugin.fire(event, event.getFrom());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void playerTeleportEvent(PlayerTeleportEvent event) {
		if (plugin.isLoaded()) {
			Player player = event.getPlayer();
			String worldname = event.getTo().getWorld().getName();
			Vector from = new Vector(event.getFrom());
			Vector to = new Vector(event.getTo());
			Cancel cancel = new Cancel();
			plugin.consumeGalaxys(registery -> makeCodingGreatAgain(
					(a, b) -> new PlayerRegionChangeEvent(cancel, player, a, b), worldname, from, to, registery));
			if (cancel.isCancelled()) {
				event.setCancelled(true);
			}
		}
		plugin.fire(event, event.getFrom());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void playerChangeWorldEvent(PlayerChangedWorldEvent event) {
		if (plugin.isLoaded()) {
			Player player = event.getPlayer();
			{
				String worldname = player.getWorld().getName();
				plugin.consumeGalaxys(galaxy -> {
					World world = galaxy.getWorld(worldname);
					if (world != null) {
						galaxy.fireWildness(world, new PlayerChangedWorldEvent(player, event.getFrom()));
					}
				});
			}
			{
				String worldname = event.getFrom().getName();
				plugin.consumeGalaxys(galaxy -> {
					World world = galaxy.getWorld(worldname);
					if (world != null) {
						galaxy.fireWildness(world, new PlayerChangedWorldEvent(player, event.getFrom()));
					}
				});
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void vehicleMoveEvent(VehicleMoveEvent event) {
		if (plugin.isLoaded()) {
			Entity entity = event.getVehicle();
			String worldname = entity.getWorld().getName();
			Vector from = new Vector(event.getFrom());
			Vector to = new Vector(event.getTo());
			Cancel cancel = new Cancel();
			plugin.consumeGalaxys(registery -> makeCodingGreatAgain(
					(a, b) -> new EntityRegionChangeEvent(cancel, entity, a, b), worldname, from, to, registery));
			if (cancel.isCancelled()) {
				// do something
			}
		}
		plugin.fire(event, event.getFrom());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void structureGrowEvent(StructureGrowEvent event) {
		if (plugin.isLoaded()) {
			String worldname = event.getLocation().getWorld().getName();
			Vector from = new Vector(event.getLocation());
			
			Cancel cancel = new Cancel();
			
			for (int i = 0; i < event.getBlocks().size(); i++) {
				
				BlockState block = event.getBlocks().get(i);
				Vector to = new Vector(block);
				plugin.consumeGalaxys(
						registery -> makeCodingGreatAgain((a, b) -> new BlockRegionChangeEvent(cancel, a, b, Case.GROW), worldname, from, to,
								registery));
				
				if(cancel.isCancelled()) {
					event.getBlocks().remove(i--);
					cancel.setCancelled(false);
				}
			}
			
			if(event.getBlocks().isEmpty()) {
				event.setCancelled(true);
			}
		}
		plugin.fire(event, event.getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockDispenseEvent(BlockDispenseEvent event) {
		if (plugin.isLoaded() && event.getBlock().getState().getData() instanceof Directional) {
			String worldname = event.getBlock().getWorld().getName();
			Vector from = new Vector(event.getBlock());
			Vector to = new Vector(
					event.getBlock().getRelative(((Directional) event.getBlock().getState().getData()).getFacing()));
			plugin.consumeGalaxys(
					registery -> makeCodingGreatAgain((a, b) -> new BlockRegionChangeEvent(event, a, b, Case.DISPENSER),
							worldname, from, to, registery));
		}
		plugin.fire(event, event.getBlock().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockPistonExtendEvent(BlockPistonExtendEvent event) {
		if (plugin.isLoaded()) {
			String worldname = event.getBlock().getWorld().getName();
			Vector from = new Vector(event.getBlock());
			for (Block block : event.getBlocks()) {
				if (event.isCancelled())
					return;
				{
					Vector to = new Vector(block);
					plugin.consumeGalaxys(registery -> makeCodingGreatAgain(
							(a, b) -> new BlockRegionChangeEvent(event, a, b, Case.PISTON), worldname, from, to,
							registery));
				}
				{
					block = block.getRelative(event.getDirection());
					Vector to = new Vector(block);
					plugin.consumeGalaxys(registery -> makeCodingGreatAgain(
							(a, b) -> new BlockRegionChangeEvent(event, a, b, Case.PISTON), worldname, from, to,
							registery));
				}
			}
		}
		plugin.fire(event, event.getBlock().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockPistonRetractEvent(BlockPistonRetractEvent event) {
		if (plugin.isLoaded()) {
			String worldname = event.getBlock().getWorld().getName();
			Vector from = new Vector(event.getBlock());
			BlockFace face = event.getDirection().getOppositeFace();
			for (Block block : event.getBlocks()) {
				if (event.isCancelled())
					return;
				{
					Vector to = new Vector(block);
					plugin.consumeGalaxys(registery -> makeCodingGreatAgain(
							(a, b) -> new BlockRegionChangeEvent(event, a, b, Case.PISTON), worldname, from, to,
							registery));
				}
				{
					block = block.getRelative(face);
					Vector to = new Vector(block);
					plugin.consumeGalaxys(registery -> makeCodingGreatAgain(
							(a, b) -> new BlockRegionChangeEvent(event, a, b, Case.PISTON), worldname, from, to,
							registery));
				}
			}
		}
		plugin.fire(event, event.getBlock().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockFromToEvent(BlockFromToEvent event) {
		if (plugin.isLoaded()) {
			String worldname = event.getBlock().getWorld().getName();
			Vector from = new Vector(event.getBlock());
			Vector to = new Vector(event.getToBlock());
			plugin.consumeGalaxys(
					registery -> makeCodingGreatAgain((a, b) -> new BlockRegionChangeEvent(event, a, b, Case.FROMTO),
							worldname, from, to, registery));
		}
		plugin.fire(event, event.getBlock().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void entityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
		if (plugin.isLoaded() && event.getTarget() != null) {
			
			Vector from = new Vector(event.getEntity().getLocation());
			
			Entity entity = event.getTarget();
			String worldname = entity.getWorld().getName();
			Cancel cancel = new Cancel();

			Vector to = new Vector(entity.getLocation());
			plugin.consumeGalaxys(
					registery -> makeCodingGreatAgain((a, b) -> new EntityRegionChangeEvent(cancel, entity, a, b),
							worldname, from, to, registery));
			if (cancel.isCancelled()) {
				event.setCancelled(true);
			}
		}
		plugin.fire(event, event.getEntity().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (plugin.isLoaded()) {
			
			Vector from = null;
			
			if (event.getDamager() != null && event.getDamager() instanceof LivingEntity) {
				from = new Vector(((LivingEntity) event.getDamager()).getLocation());
			} else if (event.getDamager() != null && event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getDamager();
				if (projectile.getShooter() instanceof Entity) {
					from = new Vector(((Entity) projectile.getShooter()).getLocation());
				} else if (projectile.getShooter() instanceof BlockProjectileSource) {
					from = new Vector(((BlockProjectileSource) projectile.getShooter()).getBlock());
				} else {
					from = new Vector(event.getDamager().getLocation());
				}
			} else {
				from = new Vector(event.getDamager().getLocation());
			}

			final Vector finalFrom = from;
			Entity entity = event.getEntity();
			String worldname = entity.getWorld().getName();
			Cancel cancel = new Cancel();

			Vector to = new Vector(entity.getLocation());
			plugin.consumeGalaxys(
					registery -> makeCodingGreatAgain((a, b) -> new EntityRegionChangeEvent(cancel, entity, a, b),
							worldname, finalFrom, to, registery));
			if (cancel.isCancelled()) {
				event.setCancelled(true);
				if (event.getDamager() instanceof Projectile) {
					Projectile projectile = (Projectile) event.getDamager();
					projectile.remove();
				}
			}
		}
		plugin.fire(event, event.getEntity().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void hangingBreakByEntityEvent(HangingBreakByEntityEvent event) {
		if (plugin.isLoaded()) {
			
			Vector from = null;
			if (event.getRemover() instanceof LivingEntity) {
				from = new Vector(((LivingEntity) event.getRemover()).getLocation());
			} else if (event.getRemover() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getRemover();
				if (projectile.getShooter() instanceof Entity) {
					from = new Vector(((Entity) projectile.getShooter()).getLocation());
				} else if (projectile.getShooter() instanceof BlockProjectileSource) {
					from = new Vector(((BlockProjectileSource) projectile.getShooter()).getBlock());
				}
			} else {
				from = new Vector(event.getRemover().getLocation());
			}

			final Vector finalFrom = from;
			Entity entity = event.getEntity();
			String worldname = entity.getWorld().getName();
			Cancel cancel = new Cancel();

			Vector to = new Vector(entity.getLocation());
			plugin.consumeGalaxys(
					registery -> makeCodingGreatAgain((a, b) -> new EntityRegionChangeEvent(cancel, entity, a, b),
							worldname, finalFrom, to, registery));
			if (cancel.isCancelled()) {
				event.setCancelled(true);
				if (event.getRemover() instanceof Projectile) {
					Projectile projectile = (Projectile) event.getRemover();
					projectile.remove();
				}
			}
		}
		plugin.fire(event, event.getEntity().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void potionSplashEvent(PotionSplashEvent event) {
		if (plugin.isLoaded()) {

			Vector from = null;
			if (event.getPotion().getShooter() instanceof BlockProjectileSource) {
				from = new Vector(((BlockProjectileSource) event.getPotion().getShooter()).getBlock());
			} else if (event.getPotion().getShooter() instanceof Entity) {
				from = new Vector(((Entity) event.getPotion().getShooter()).getLocation());
			} else {
				from = new Vector(event.getPotion().getLocation());
			}

			final Vector finalFrom = from;
			Entity entity = event.getPotion();
			String worldname = entity.getWorld().getName();
			Cancel cancel = new Cancel();

			if (event.getAffectedEntities().isEmpty()) {
				Vector to = new Vector(entity.getLocation());
				plugin.consumeGalaxys(
						registery -> makeCodingGreatAgain((a, b) -> new EntityRegionChangeEvent(cancel, entity, a, b),
								worldname, finalFrom, to, registery));
				
				if (cancel.isCancelled()) {
					event.setCancelled(true);
				}
				
			} else {
				
				PotionSplashEvent e2 = new PotionSplashEvent(event.getPotion(), new HashMap<>());
				Iterator<LivingEntity> list = event.getAffectedEntities().iterator();
				
				while(list.hasNext()) {
					
					LivingEntity livingEntity = list.next();
					Vector to = new Vector(livingEntity.getLocation());
					plugin.consumeGalaxys(
							registery -> makeCodingGreatAgain((a, b) -> new EntityRegionChangeEvent(cancel, livingEntity, a, b), worldname, finalFrom, to,
									registery));
					
					plugin.fire(e2, livingEntity.getLocation());
					
					if(cancel.isCancelled() || e2.isCancelled()) {
						event.setIntensity(livingEntity, -1);
						cancel.setCancelled(false);
						e2.setCancelled(false);
					}
					
				}
				
				if (event.getAffectedEntities().isEmpty()) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	//Explosion -----------------------------------------
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void entityExplodeEvent(EntityExplodeEvent event) {
		if (plugin.isLoaded()) {
			handelExplosion(event.blockList(), event, event.getLocation());
		}
		plugin.fire(event, event.getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockExplodeEvent(BlockExplodeEvent event) {
		if (plugin.isLoaded()) {
			handelExplosion(event.blockList(), event, event.getBlock().getLocation());
		}
		plugin.fire(event, event.getBlock().getLocation());
	}
	
	private void handelExplosion(List<Block> blocks, Cancellable event, Location location) {
		String worldname = location.getWorld().getName();
		Vector from = new Vector(location);
		Cancel cancel = new Cancel();
		for (int i = 0; i < blocks.size(); i++) {
			Block block = blocks.get(i);
			Vector to = new Vector(block);
			plugin.consumeGalaxys(
					registery -> makeCodingGreatAgain((a, b) -> new BlockRegionChangeEvent(cancel, a, b, Case.EXLOPSION), worldname, from, to,
							registery));
			if(cancel.isCancelled()) {
				blocks.remove(i--);
				cancel.setCancelled(false);
			}
		}
		
		if(blocks.isEmpty()) {
			event.setCancelled(true);
		}
	}
	
}
