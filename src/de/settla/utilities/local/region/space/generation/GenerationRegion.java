package de.settla.utilities.local.region.space.generation;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Openable;
import org.bukkit.material.Redstone;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.region.Buildable;
import de.settla.utilities.local.region.Region;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.local.region.World;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.BlockList;
import de.settla.utilities.local.region.space.DataException;
import de.settla.utilities.local.region.space.Room;
import de.settla.utilities.local.region.space.selection.SelectionException;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

@SerializableAs("GenerationRegion")
public class GenerationRegion extends Region implements GenerationListener, Buildable {

	private static final int ID_LENGHT = 20;

	private World world;
	private final Object lock = new Object();
	private GenerationPaster generationPaster;
	double percent = -1;
	
	public GenerationRegion(Form form, Room room, Vector origin, World world, BlockList blocks, String blockListId, boolean delete, boolean ignoreAir) {
		super(form, 10, false, generateId());
		this.world = world;
		try {
			generationPaster = new GenerationPaster(this, form, room, origin, world, blocks, blockListId, delete, ignoreAir);
		} catch (SelectionException e) {
			e.printStackTrace();
		}
	}

	public GenerationRegion(Map<String, Object> map1) {
		super(map1);

		LocalPlugin.getInstance().getModule(Universe.class).addToWaitingActionList(() -> {
			try {

				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) map1.get("paster");

				String blockListId = (String) map.get("blockListId");
				BlockList blocks = BlockList.loadBlockList(blockListId);
				@SuppressWarnings("unchecked")
				Form form = Form.deserialize((Map<String, Object>) map.get("form"));
				@SuppressWarnings("unchecked")
				Room room = Memory.deserialize((Map<String, Object>) map.get("room"), Room.class);
				Vector origin = deserialize(map.get("origin"), Vector.class);

				int index = ((Double) map.get("index")).intValue();
				int blocksPerPeriod = ((Double) map.get("blocksPerPeriod")).intValue();
				int period = ((Double) map.get("period")).intValue();
				
				boolean delete = (Boolean)map.get("delete");
				boolean ignoreAir = (Boolean)map.get("air");

				try {
					generationPaster = new GenerationPaster(this, form, room, origin, getWorld(), blocks, blockListId,
							index, blocksPerPeriod, period, delete, ignoreAir);
					generationPaster.paste();
				} catch (SelectionException e) {
					e.printStackTrace();
				}

			} catch (FileNotFoundException | DataException e) {
				e.printStackTrace();
			}
		});
	}

	public GenerationPaster getGenerationPaster() {
		return generationPaster;
	}

	private static String generateId() {
		if (LocalPlugin.getInstance().getModule(Universe.class).isLoaded()) {
			return LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME)
					.generateId(ID_LENGHT);
		}
		return RandomStringUtils.randomAlphabetic(ID_LENGHT);
	}

	@Override
	public Map<String, Object> serialize() {
		synchronized (lock) {
			Map<String, Object> map = super.serialize();
			if (generationPaster != null)
				map.put("paster", generationPaster.serialize());
			return map;
		}
	}

	@Nullable
	public World getWorld() {
		synchronized (lock) {
			if (world != null) {
				return world;
			} else {
				return getRegionRegistery().getWorld();
//				Set<World> registeredWorlds = getRegionRegistery().getWorlds();
//				if (registeredWorlds.size() == 1) {
//					for (World world : registeredWorlds) {
//						if (world.isLoaded()) {
//							this.world = world;
//						}
//					}
//				} else {
//					Notification.warn(Type.CONSOLE, "the claim " + id() + " has more worlds of registery!");
//				}
//				return world;
			}
		}
	}

	@Override
	public boolean isDirty() {
		return true;
	}
	
	@Override
	public void changeState(GenerationState from, GenerationState to) {
//		notify("Generation: "+getGenerationPaster().size(), "State: "+getGenerationPaster().getState(), "Index: "+getGenerationPaster().getIndex());
	}

	@Override
	public boolean canBuild(UUID uuid) {
//		return VaultHelper.checkPerm(uuid, "settla.generation.build");
		return false;
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
	
	@Override
	public void runningGeneration(GenerationPaster paster) {
		
		double percent = Math.floor(paster.getPercent() * 100D) / 100D;
		if(this.percent == percent)
			return;
		
		this.percent = percent;
		for (Player player : getWorld().getBukkitWorld().getBukkitWorld().getPlayers()) {
			if(getForm().overlaps(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())) {
				sendLoadingSignal(player, "", "", 100, percent);
			}
		}
	}
	
	public void sendLoadingSignal(Player player, String prefix, String suffix, double lenght, double percent) {
		
		StringBuilder sb = new StringBuilder();
		
		int count = (int)Math.floor(percent * lenght);
		
		String green = ChatColor.GREEN+"▏";
		String red = ChatColor.RED+"▏";
		
		for (int j = 0; j < lenght; j++) {
			if(j <= count) {
				sb.append(green);
			} else {
				sb.append(red);
			}
		}
		
		sendActionbarMessage(player, prefix + ChatColor.RESET + sb.toString() + ChatColor.RESET + suffix);
	}
	
	public void sendActionbarMessage(Player player, String message){
		IChatBaseComponent actionbartext = ChatSerializer.a("{\"text\": \""+message+"\"}");
		PacketPlayOutChat actionbar = new PacketPlayOutChat(actionbartext, (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionbar);
	}
	
	@EventHandler
	public void a(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (canBuild(player.getUniqueId()))
			return;
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
			if (block.getState() instanceof InventoryHolder || block.getState().getData() instanceof Redstone
					|| block.getState().getData() instanceof Openable) {
				event.setCancelled(true);
				return;
			}
		}
		if (event.getAction() == Action.PHYSICAL) {
			event.setCancelled(true);
			return;
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
	public void a(PlayerPickupItemEvent event) {
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

		if (event.getDamager() instanceof Projectile
				&& ((Projectile) event.getDamager()).getShooter() instanceof Player) {
			if (!canBuild(((Player) ((Projectile) event.getDamager()).getShooter()).getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			return;
		}
		event.setCancelled(true);
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
		SpawnReason reason = event.getSpawnReason();
		// if(reason == SpawnReason.BUILD_WITHER) {
		// event.setCancelled(true);
		// }
		if (reason == SpawnReason.SPAWNER_EGG || reason == SpawnReason.BUILD_WITHER
				|| reason == SpawnReason.DISPENSE_EGG || reason == SpawnReason.EGG) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void a(PlayerEggThrowEvent event) {
		Player player = event.getPlayer();
		if (!canBuild(player.getUniqueId()))
			event.setHatching(false);
	}

	@EventHandler
	public void a(PotionSplashEvent event) {
		if (event.getPotion().getShooter() instanceof Player) {
			if (!canBuild(((Player) event.getPotion().getShooter()).getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			return;
		}
	}
}
