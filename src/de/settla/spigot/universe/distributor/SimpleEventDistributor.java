/*
 *
 *     Copyright (C) 2019  Salkin (mc.salkin@gmail.com)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.settla.spigot.universe.distributor;

import java.util.List;
import java.util.function.BiFunction;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Directional;
import org.bukkit.projectiles.BlockProjectileSource;
import de.settla.spigot.universe.Galaxy;
import de.settla.spigot.universe.Region;
import de.settla.spigot.universe.WildernessRegion;
import de.settla.spigot.universe.event.GalaxyEventDistributor;
import de.settla.spigot.universe.event.Position;

public class SimpleEventDistributor extends GalaxyEventDistributor implements Listener {

    public SimpleEventDistributor(Galaxy... galaxy) {
        super(galaxy);
    }

    public void fireExternalEvent(Position from, Position to,
            BiFunction<List<Region>, List<Region>, ? extends RegionChangeEvent> event) {
        forEachGalaxy(galaxy -> {

            List<Region> fromRegions = from.regions(galaxy);
            List<Region> toRegions = to.regions(galaxy);

            if (fromRegions.isEmpty() && toRegions.isEmpty())
                return;

            WildernessRegion fromWilderness = from.wilderness(galaxy);
            WildernessRegion toWilderness = to.wilderness(galaxy);

            if (fromRegions.size() == toRegions.size()) {

                for (Region region : toRegions) {
                    if (!fromRegions.contains(region)) {
                        if (toWilderness != null && fromWilderness != null) {
                            if (toWilderness.equals(fromWilderness)) {
                                fireEvent(event.apply(fromRegions, toRegions), fromWilderness);
                            } else {
                                fireEvent(event.apply(fromRegions, toRegions), fromWilderness);
                                fireEvent(event.apply(fromRegions, toRegions), toWilderness);
                            }
                        } else {
                            if (fromWilderness != null)
                                fireEvent(event.apply(fromRegions, toRegions), fromWilderness);
                            if (toWilderness != null)
                                fireEvent(event.apply(fromRegions, toRegions), toWilderness);
                        }
                        return;
                    }
                }
                for (Region region : fromRegions) {
                    if (!toRegions.contains(region)) {
                        if (toWilderness != null && fromWilderness != null) {
                            if (toWilderness.equals(fromWilderness)) {
                                fireEvent(event.apply(fromRegions, toRegions), fromWilderness);
                            } else {
                                fireEvent(event.apply(fromRegions, toRegions), fromWilderness);
                                fireEvent(event.apply(fromRegions, toRegions), toWilderness);
                            }
                        } else {
                            if (fromWilderness != null)
                                fireEvent(event.apply(fromRegions, toRegions), fromWilderness);
                            if (toWilderness != null)
                                fireEvent(event.apply(fromRegions, toRegions), toWilderness);
                        }
                        return;
                    }
                }
            } else {
                if (toWilderness != null && fromWilderness != null) {
                    if (toWilderness.equals(fromWilderness)) {
                        fireEvent(event.apply(fromRegions, toRegions), fromWilderness);
                    } else {
                        fireEvent(event.apply(fromRegions, toRegions), fromWilderness);
                        fireEvent(event.apply(fromRegions, toRegions), toWilderness);
                    }
                } else {
                    if (fromWilderness != null)
                        fireEvent(event.apply(fromRegions, toRegions), fromWilderness);
                    if (toWilderness != null)
                        fireEvent(event.apply(fromRegions, toRegions), toWilderness);
                }
            }
            toRegions.clear();
            fromRegions.clear();
        });
    }

    @EventHandler
    public void e(PlayerMoveEvent event) {
        // fires the event into the distributor...
        if (event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())
                && event.getFrom().distanceSquared(event.getTo()) <= 0.0) {
            return;
        }
        this.fireExternalEvent(new Position(event.getFrom()), new Position(event.getTo()),
                (from, to) -> new PlayerRegionChangeEvent(from, to, event, event.getPlayer()));

        // this.fireEvent(event, event.getTo());

    }

    @EventHandler
    public void e(PlayerTeleportEvent event) {
        // fires the event into the distributor...
        this.fireExternalEvent(new Position(event.getFrom()), new Position(event.getTo()),
                (from, to) -> new PlayerRegionChangeEvent(from, to, event, event.getPlayer()));
    }

    @EventHandler
    public void e(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        this.fireExternalEvent(new Position(event.getFrom()), new Position(event.getTo()),
                (from, to) -> new EntityRegionChangeEvent(from, to, event, entity));
    }

    @EventHandler
    public void e(VehicleMoveEvent event) {
        Entity entity = event.getVehicle();
        Cancel cancel = new Cancel();
        this.fireExternalEvent(new Position(event.getFrom()), new Position(event.getTo()),
                (from, to) -> new EntityRegionChangeEvent(from, to, cancel, entity));
        if (cancel.isCancelled()) {
            // do something
        }
    }

    @EventHandler
    public void e(EntityTargetLivingEntityEvent event) {
        Entity entity = event.getTarget();
        if (entity != null)
            this.fireExternalEvent(new Position(event.getEntity().getLocation()), new Position(entity.getLocation()),
                    (f, t) -> new EntityRegionChangeEvent(f, t, event, entity));
    }

    @EventHandler
    public void e(EntityDamageByEntityEvent event) {
        Position from = null;
        if (event.getDamager() != null && event.getDamager() instanceof LivingEntity) {
            from = new Position(event.getDamager().getLocation());
        } else if (event.getDamager() != null && event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Entity) {
                from = new Position(((Entity) projectile.getShooter()).getLocation());
            } else if (projectile.getShooter() instanceof BlockProjectileSource) {
                from = new Position(((BlockProjectileSource) projectile.getShooter()).getBlock().getLocation());
            } else {
                from = new Position(event.getDamager().getLocation());
            }
        } else if (event.getDamager() != null) {
            from = new Position(event.getDamager().getLocation());
        }
        Entity entity = event.getEntity();
        Cancel cancel = new Cancel();
        Position to = new Position(entity.getLocation());
        this.fireExternalEvent(from, to, (f, t) -> new EntityRegionChangeEvent(f, t, cancel, entity));
        if (cancel.isCancelled()) {
            event.setCancelled(true);
            if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getDamager();
                projectile.remove();
            }
            return;
        }
        this.fireEvent(event, entity.getLocation());
    }

    @EventHandler
    public void e(HangingBreakByEntityEvent event) {
        Position from = null;
        if (event.getRemover() instanceof LivingEntity) {
            from = new Position(event.getRemover().getLocation());
        } else if (event.getRemover() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getRemover();
            if (projectile.getShooter() instanceof Entity) {
                from = new Position(((Entity) projectile.getShooter()).getLocation());
            } else if (projectile.getShooter() instanceof BlockProjectileSource) {
                from = new Position(((BlockProjectileSource) projectile.getShooter()).getBlock().getLocation());
            }
        } else {
            from = new Position(event.getRemover().getLocation());
        }
        Entity entity = event.getEntity();
        Cancel cancel = new Cancel();
        Position to = new Position(entity.getLocation());
        this.fireExternalEvent(from, to, (f, t) -> new EntityRegionChangeEvent(f, t, cancel, entity));
        if (cancel.isCancelled()) {
            event.setCancelled(true);
            if (event.getRemover() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getRemover();
                projectile.remove();
            }
            return;
        }
        this.fireEvent(event, entity.getLocation());
    }

    @EventHandler
    public void e(PotionSplashEvent event) {
        Position from;
        if (event.getPotion().getShooter() instanceof BlockProjectileSource) {
            from = new Position(((BlockProjectileSource) event.getPotion().getShooter()).getBlock().getLocation());
        } else if (event.getPotion().getShooter() instanceof Entity) {
            from = new Position(((Entity) event.getPotion().getShooter()).getLocation());
        } else {
            from = new Position(event.getPotion().getLocation());
        }
        Entity entity = event.getPotion();
        Cancel cancel = new Cancel();
        if (event.getAffectedEntities().isEmpty()) {
            Position to = new Position(entity.getLocation());
            this.fireExternalEvent(from, to, (f, t) -> new EntityRegionChangeEvent(f, t, cancel, entity));
            if (cancel.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            this.fireEvent(event, entity.getLocation());
        } else {
            for (LivingEntity livingEntity : event.getAffectedEntities()) {
                Position to = new Position(livingEntity.getLocation());
                this.fireExternalEvent(from, to, (f, t) -> new EntityRegionChangeEvent(f, t, cancel, livingEntity));
                if (cancel.isCancelled()) {
                    event.setIntensity(livingEntity, -1);
                    cancel.setCancelled(false);
                }
            }
            if (event.getAffectedEntities().isEmpty()) {
                event.setCancelled(true);
                return;
            }
            this.fireEvent(event, entity.getLocation());
        }
    }

    @EventHandler
    public void e(EntityExplodeEvent event) {
        handelExplosion(event.blockList(), event, event.getLocation());
    }

    @EventHandler
    public void e(BlockExplodeEvent event) {
        handelExplosion(event.blockList(), event, event.getBlock().getLocation());
    }

    private void handelExplosion(List<Block> blocks, Cancellable event, Location location) {
        Position from = new Position(location);
        Cancel cancel = new Cancel();
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            Position to = new Position(block.getLocation());
            this.fireExternalEvent(from, to,
                    (f, t) -> new BlockRegionChangeEvent(f, t, cancel, BlockRegionChangeEvent.Case.EXPLOSION));
            if (cancel.isCancelled()) {
                blocks.remove(i--);
                cancel.setCancelled(false);
            }
        }
        if (blocks.isEmpty()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void e(PlayerInteractEvent event) {
        // fires the event into the distributor...
        if (event.hasBlock()) {
            this.fireEvent(event, event.getClickedBlock().getLocation());
        } else {
            this.fireEvent(event, event.getPlayer().getLocation());
        }
    }

    // @EventHandler
    // public void e(BlockBreakEvent event) {
    // // fires the event into the distributor...
    // this.fireEvent(event, event.getBlock().getLocation());
    // }
    //
    // @EventHandler
    // public void e(BlockPlaceEvent event) {
    // // fires the event into the distributor...
    // this.fireEvent(event, event.getBlock().getLocation());
    // }

    @EventHandler
    public void e(PlayerJoinEvent event) {
        // fires the event into the distributor...
        this.fireEvent(event, event.getPlayer().getLocation());
    }
    //
    // @EventHandler
    // public void e(BlockRedstoneEvent event) {
    // // fires the event into the distributor...
    // this.fireEvent(event, event.getBlock().getLocation());
    // }

    @EventHandler
    public void e(BlockMultiPlaceEvent event) {
        // fires the event into the distributor...
        this.fireEvent(event, event.getBlock().getLocation());
    }

    @EventHandler
    public void e(CreatureSpawnEvent event) {
        // fires the event into the distributor...
        this.fireEvent(event, event.getEntity().getLocation());
    }

    // 1.14.4 block events

    // BlockBreakEvent
    // Called when a block is broken by a player.
    @EventHandler
    public void block(BlockBreakEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockBurnEvent
    // Called when a block is destroyed as a result of being burnt by fire.
    @EventHandler
    public void block(BlockBurnEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockCanBuildEvent
    // Called when we try to place a block, to see if we can build it here or not.
    @EventHandler
    public void block(BlockCanBuildEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockDamageEvent
    // Called when a block is damaged by a player.
    @EventHandler
    public void block(BlockDamageEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockDispenseArmorEvent
    // Called when an equippable item is dispensed from a block and equipped on a
    // nearby entity.
    @EventHandler
    public void block(BlockDispenseArmorEvent event) {
        Entity entity = event.getTargetEntity();
        if (entity != null) {
            this.fireExternalEvent(new Position(event.getBlock().getLocation()), new Position(entity.getLocation()),
                    (f, t) -> new EntityRegionChangeEvent(f, t, event, entity));
        } else {
            this.fireEvent(event, event.getBlock().getLocation());
        }
    }

    // BlockDispenseEvent
    // Called when an item is dispensed from a block.
    // BlockDropItemEvent Deprecated.
    // draft API
    @EventHandler
    public void block(BlockDispenseEvent event) {
        this.fireExternalEvent(new Position(event.getBlock().getLocation()),
                new Position(event.getBlock()
                        .getRelative(((Directional) event.getBlock().getState().getData()).getFacing()).getLocation()),
                (from, to) -> new BlockRegionChangeEvent(from, to, event, BlockRegionChangeEvent.Case.DISPENSER));
    }

    // BlockExpEvent
    // An event that's called when a block yields experience.
    @EventHandler
    public void block(BlockExpEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockExplodeEvent
    // Called when a block explodes
    @EventHandler
    public void block(BlockExplodeEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockFadeEvent
    // Called when a block fades, melts or disappears based on world conditions
    @EventHandler
    public void block(BlockFadeEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockFertilizeEvent
    // Called with the block changes resulting from a player fertilizing a given
    // block with bonemeal.
    @EventHandler
    public void block(BlockFertilizeEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockFormEvent
    // Called when a block is formed or spreads based on world conditions.
    @EventHandler
    public void block(BlockFormEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockFromToEvent
    // Represents events with a source block and a destination block, currently only
    // applies to liquid (lava and water) and teleporting dragon eggs.
    @EventHandler
    public void block(BlockFromToEvent event) {
        this.fireExternalEvent(new Position(event.getBlock().getLocation()),
                new Position(event.getToBlock().getLocation()),
                (f, t) -> new BlockRegionChangeEvent(f, t, event, BlockRegionChangeEvent.Case.FROMTO));
    }

    // BlockGrowEvent
    // Called when a block grows naturally in the world.
    @EventHandler
    public void block(BlockGrowEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockIgniteEvent
    // Called when a block is ignited.
    @EventHandler
    public void block(BlockIgniteEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockMultiPlaceEvent
    // Fired when a single block placement action of a player triggers the creation
    // of multiple blocks(e.g.
    @EventHandler
    public void block(BlockMultiPlaceEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockPhysicsEvent
    // Thrown when a block physics check is called.
    @EventHandler
    public void block(BlockPhysicsEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockPistonExtendEvent
    // Called when a piston extends
    @EventHandler
    public void block(BlockPistonExtendEvent event) {
        Position from = new Position(event.getBlock().getLocation());
        for (Block block : event.getBlocks()) {
            if (event.isCancelled())
                return;
            {
                Position to = new Position(block.getLocation());
                this.fireExternalEvent(from, to,
                        (f, t) -> new BlockRegionChangeEvent(f, t, event, BlockRegionChangeEvent.Case.PISTON));
            }
            {
                block = block.getRelative(event.getDirection());
                Position to = new Position(block.getLocation());
                this.fireExternalEvent(from, to,
                        (f, t) -> new BlockRegionChangeEvent(f, t, event, BlockRegionChangeEvent.Case.PISTON));
            }
        }
    }

    // BlockPistonRetractEvent
    // Called when a piston retracts
    @EventHandler
    public void block(BlockPistonRetractEvent event) {
        Position from = new Position(event.getBlock().getLocation());
        BlockFace face = event.getDirection().getOppositeFace();
        for (Block block : event.getBlocks()) {
            if (event.isCancelled())
                return;
            {
                Position to = new Position(block.getLocation());
                this.fireExternalEvent(from, to,
                        (f, t) -> new BlockRegionChangeEvent(f, t, event, BlockRegionChangeEvent.Case.PISTON));
            }
            {
                block = block.getRelative(face);
                Position to = new Position(block.getLocation());
                this.fireExternalEvent(from, to,
                        (f, t) -> new BlockRegionChangeEvent(f, t, event, BlockRegionChangeEvent.Case.PISTON));
            }
        }
    }

    // BlockPlaceEvent
    // Called when a block is placed by a player.
    @EventHandler
    public void block(BlockPlaceEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    @EventHandler
    public void block(BlockModifyEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // BlockRedstoneEvent
    // Called when a redstone current changes
    @EventHandler
    public void block(BlockRedstoneEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    //    BlockSpreadEvent	
    //    Called when a block spreads based on world conditions.
    @EventHandler
    public void block(BlockSpreadEvent event) {
        this.fireExternalEvent(new Position(event.getSource().getLocation()), new Position(event.getBlock().getLocation()), (f, t) -> new BlockRegionChangeEvent(f, t, event, BlockRegionChangeEvent.Case.FROMTO));
    }

    // CauldronLevelChangeEvent
    @EventHandler
    public void block(CauldronLevelChangeEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // EntityBlockFormEvent
    // Called when a block is formed by entities.
    @EventHandler
    public void block(EntityBlockFormEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // LeavesDecayEvent
    // Called when leaves are decaying naturally.
    @EventHandler
    public void block(LeavesDecayEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // MoistureChangeEvent
    // Called when the moisture level of a soil block changes.
    @EventHandler
    public void block(MoistureChangeEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // NotePlayEvent
    // Called when a note block is being played through player interaction or a
    // redstone current.
    @EventHandler
    public void block(NotePlayEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // SignChangeEvent
    // Called when a sign is changed by a player.
    @EventHandler
    public void block(SignChangeEvent event) {
        this.fireEvent(event, event.getBlock().getLocation());
    }

    // SpongeAbsorbEvent
    // Called when a sponge absorbs water from the world.
    @EventHandler
    public void block(SpongeAbsorbEvent event) {
        Cancel cancel = new Cancel();
        for (int i = 0; i < event.getBlocks().size(); i++) {
            BlockState block = event.getBlocks().get(i);
            this.fireExternalEvent(new Position(event.getBlock().getLocation()), new Position(block.getLocation()),
                    (from, to) -> new BlockRegionChangeEvent(from, to, cancel, BlockRegionChangeEvent.Case.GROW));
            if (cancel.isCancelled()) {
                event.getBlocks().remove(i--);
                cancel.setCancelled(false);
            }
        }
        if (event.getBlocks().isEmpty()) {
            event.setCancelled(true);
        }
    }

    // world events

    // PortalCreateEvent
    // Called when a portal is created
    @EventHandler
    public void world(PortalCreateEvent event) {
        // Cancel cancel = new Cancel();
        // for (int i = 0; i < event.getBlocks().size(); i++) {
        // BlockState block = event.getBlocks().get(i);
        // this.fireExternalEvent(new Position(event.get.getLocation()), new
        // Position(block.getLocation()), (from, to) -> new BlockRegionChangeEvent(from,
        // to, cancel, BlockRegionChangeEvent.Case.PORTAL));
        // if (cancel.isCancelled()) {
        // event.getBlocks().remove(i--);
        // cancel.setCancelled(false);
        // }
        // }
        // if (event.getBlocks().isEmpty()) {
        // event.setCancelled(true);
        // }
    }

    // SpawnChangeEvent
    // An event that is called when a world's spawn changes.
    @EventHandler
    public void world(SpawnChangeEvent event) {
        this.fireEvent(event, event.getPreviousLocation());
    }

    // StructureGrowEvent
    // Event that is called when an organic structure attempts to grow (Sapling ->
    // Tree), (Mushroom -> Huge Mushroom), naturally or using bonemeal.
    @EventHandler
    public void world(StructureGrowEvent event) {
        Cancel cancel = new Cancel();
        for (int i = 0; i < event.getBlocks().size(); i++) {
            BlockState block = event.getBlocks().get(i);
            this.fireExternalEvent(new Position(event.getLocation()), new Position(block.getLocation()),
                    (from, to) -> new BlockRegionChangeEvent(from, to, cancel, BlockRegionChangeEvent.Case.GROW));
            if (cancel.isCancelled()) {
                event.getBlocks().remove(i--);
                cancel.setCancelled(false);
            }
        }
        if (event.getBlocks().isEmpty()) {
            event.setCancelled(true);
        }
        // for (int i = 0; i < event.getBlocks().size(); i++) {
        // this.fireEvent(event, entity.getLocation());
        // }
    }

    // new stuff

    @EventHandler
    public void e(PlayerArmorStandManipulateEvent event) {
        this.fireEvent(event, event.getRightClicked().getLocation());
    }

    @EventHandler
    public void e(PlayerBucketEmptyEvent event) {
        this.fireEvent(event, event.getBlockClicked().getLocation());
    }

    @EventHandler
    public void e(PlayerBucketFillEvent event) {
        this.fireEvent(event, event.getBlockClicked().getLocation());
    }

    @EventHandler
    public void e(PlayerBedEnterEvent event) {
        this.fireEvent(event, event.getBed().getLocation());
    }

    @EventHandler
    public void e(PlayerLeashEntityEvent event) {
        this.fireEvent(event, event.getEntity().getLocation());
    }

    @EventHandler
    public void e(PlayerShearEntityEvent event) {
        this.fireEvent(event, event.getEntity().getLocation());
    }

    @EventHandler
    public void e(PlayerUnleashEntityEvent event) {
        this.fireEvent(event, event.getEntity().getLocation());
    }

    @EventHandler
    public void e(PlayerInteractEntityEvent event) {
        this.fireEvent(event, event.getRightClicked().getLocation());
    }

    @EventHandler
    public void e(VehicleEnterEvent event) {
        this.fireEvent(event, event.getVehicle().getLocation());
    }

    @EventHandler
    public void e(VehicleDamageEvent event) {
        this.fireEvent(event, event.getVehicle().getLocation());
    }

    @EventHandler
    public void e(VehicleDestroyEvent event) {
        this.fireEvent(event, event.getVehicle().getLocation());
    }

    @EventHandler
    public void e(VehicleEntityCollisionEvent event) {
        this.fireEvent(event, event.getVehicle().getLocation());
    }

    @EventHandler
    public void e(PlayerEggThrowEvent event) {
        this.fireEvent(event, event.getPlayer().getLocation());
    }

}
