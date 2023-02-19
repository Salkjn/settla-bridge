package de.settla.local.keys;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.Command;
import de.settla.utilities.local.commands.Description;
import de.settla.utilities.local.commands.OverviewCommand;
import de.settla.utilities.local.commands.Perm;
import de.settla.utilities.local.commands.PlayerCommand;
import de.settla.utilities.local.commands.Usage;

@Perm("key")
public class KeyCommand extends OverviewCommand {

	public KeyCommand(String name, String... aliases) {
		super(name, aliases);
		addSubCommand(new CreateCommand("create"));
		addSubCommand(new KnockbackCommand("knockback"));
		addSubCommand(new PermissionCommand("permission", "perm"));
		addSubCommand(new ItemCommand("item"));
		addSubCommand(new GiveCommand("give"));
		addSubCommand(new DeleteCommand("delete"));
		addSubCommand(new LocationCommand("location"));
		addSubCommand(new TeleportCommand("teleport"));
		addSubCommand(new OpenCommand("open"));
		addSubCommand(new OpenAllCommand("open-all"));
		addSubCommand(new GiveAllCommand("give-all"));
		addSubCommand(new ReloadCommand("reload"));

	}

	@Perm("create")
	@Description(description = "Erstellt ein neuen Key (Dieser muss im Gui System registriert sein)")
	@Usage(usage = "<id> <name>")
	private class CreateCommand extends Command {

		public CreateCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender player, ArgumentParser ap) {
			if (ap.hasExactly(2)) {

				String id = ap.get(1);
				String name = ap.get(2);

				Key key = KeyModule.getInstance().getKey(id);
				if (key != null) {
					player.sendMessage(
							KeyModule.convert(KeyModule.MESSAGE_CMD_CREATE_FAIL, new String[][] { { "id", id } }));
					return;
				}
				key = new Key(id, name, null, 1, new ArrayList<>(), new ArrayList<>());
				KeyModule.getInstance().addKey(key);
				player.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_CREATE_SUCCESS,
						new String[][] { { "id", id }, { "key", key.getName() } }));
			}
		}
	}

	@Perm("delete")
	@Description(description = "Löscht einen Key.")
	@Usage(usage = "<key>")
	private class DeleteCommand extends PlayerCommand {

		public DeleteCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {

			if (ap.hasExactly(1)) {
				String id = ap.get(1);
				Key key = KeyModule.getInstance().getKey(id);
				if (key == null) {

				} else {
					KeyModule.getInstance().removeKey(key.getId());
					player.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_DELETE,
							new String[][] { { "key", key.getName() } }));
				}
			}
		}
	}

	@Perm("permission")
	@Description(description = "Stellt die Permissions des Keys ein.")
	@Usage(usage = "<key> <permission>")
	private class PermissionCommand extends Command {

		public PermissionCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {

			if (ap.hasExactly(2)) {
				String id = ap.get(1);
				Key key = KeyModule.getInstance().getKey(id);
				if (key == null) {

				} else {
					String permission = ap.get(2);
					key.setPermission(permission.equalsIgnoreCase("null") ? null : permission);
					KeyModule.getInstance().getDatabase("keys").saveToFile(true);
					sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_PERMISSION,
							new String[][] { { "key", key.getName() }, { "permission", permission } }));
				}
			}
		}
	}

	@Perm("knockback")
	@Description(description = "Stellt den Knockback des Keys ein.")
	@Usage(usage = "<key> <0-10>")
	private class KnockbackCommand extends Command {

		public KnockbackCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {

			if (ap.hasExactly(2)) {
				String id = ap.get(1);
				Key key = KeyModule.getInstance().getKey(id);
				if (key == null) {

				} else {
					int knockback = ap.getInt(2);
					if (0 <= knockback && knockback <= 10) {
						key.setKnockback(knockback);
						KeyModule.getInstance().getDatabase("keys").saveToFile(true);
						sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_KNOCKBACK,
								new String[][] { { "key", key.getName() }, { "knockback", knockback + "" } }));
					} else {

					}
				}
			}
		}
	}

	@Perm("location")
	@Description(description = "Location Verwaltung.")
	@Usage(usage = "")
	private class LocationCommand extends OverviewCommand {

		public LocationCommand(String name, String... aliases) {
			super(name, aliases);
			addSubCommand(new AddCommand("add"));
			addSubCommand(new RemoveCommand("remove"));
		}

		@Perm("add")
		@Description(description = "Fügt diese Position hinzu.")
		@Usage(usage = "<key>")
		private class AddCommand extends PlayerCommand {

			public AddCommand(String name, String... aliases) {
				super(name, aliases);
			}

			@Override
			protected void execute(Player sender, ArgumentParser ap) {
				if (ap.hasExactly(1)) {
					String id = ap.get(1);
					Key key = KeyModule.getInstance().getKey(id);
					if (key == null) {

					} else {
						Location loc = sender.getTargetBlock((Set<Material>) null, 10).getLocation();
						if (loc == null) {
							sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_LOCATION_REMOVE_NO_BLOCK,
									new String[][] { { "key", key.getName() } }));
							return;
						}
						key.getRedeemLocations().add(loc);
						KeyModule.getInstance().getDatabase("keys").saveToFile(true);
						sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_LOCATION_ADD_SUCCESS,
								new String[][] { { "key", key.getName() } }));
					}
				}
			}
		}

		@Perm("remove")
		@Description(description = "Löscht diese Position raus.")
		@Usage(usage = "<key>")
		private class RemoveCommand extends PlayerCommand {

			public RemoveCommand(String name, String... aliases) {
				super(name, aliases);
			}

			@Override
			protected void execute(Player sender, ArgumentParser ap) {
				Location loc = sender.getTargetBlock((Set<Material>) null, 10).getLocation();
				if (loc == null) {
					sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_LOCATION_REMOVE_NO_BLOCK, null));
					return;
				}
				Key key = KeyModule.getInstance().getKey(loc);
				if (key == null) {
					sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_LOCATION_REMOVE_FAIL, null));
					return;
				}
				List<Location> locations = new ArrayList<>();
				for (Location location : key.getRedeemLocations()) {
					if (!(loc.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())
							&& loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY()
							&& loc.getBlockZ() == location.getBlockZ())) {
						locations.add(location);
					}
				}
				key.setRedeemLocations(locations);
				KeyModule.getInstance().getDatabase("keys").saveToFile(true);
				sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_LOCATION_REMOVE_SUCCESS,
						new String[][] { { "key", key.getName() } }));
			}
		}
	}

	@Perm("item")
	@Description(description = "Item Verwaltung.")
	@Usage(usage = "")
	private class ItemCommand extends OverviewCommand {

		public ItemCommand(String name, String... aliases) {
			super(name, aliases);
			addSubCommand(new AddCommand("add"));
			addSubCommand(new RemoveCommand("remove"));
		}

		@Perm("add")
		@Description(description = "Fügt das Item in der Hand dem Key hinzu.")
		@Usage(usage = "<key> <%>")
		private class AddCommand extends PlayerCommand {

			public AddCommand(String name, String... aliases) {
				super(name, aliases);
			}

			@Override
			protected void execute(Player sender, ArgumentParser ap) {

				if (ap.hasExactly(2)) {
					String id = ap.get(1);
					int chance = ap.getInt(2);
					Key key = KeyModule.getInstance().getKey(id);
					if (key == null) {

					} else {
						ItemStack item = sender.getInventory().getItemInHand();
						if (item == null || item.getType() == Material.AIR) {
							sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_ITEM_ADD_NO_ITEM,
									new String[][] { { "key", key.getName() }, { "chance", chance + "" } }));
							return;
						}
						boolean similar = false;
						for (KeyItemable i : key.getItemList()) {
							if (i.getItemStack().isSimilar(item))
								similar = true;
						}
						if (similar) {
							sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_ITEM_ADD_SIMILAR,
									new String[][] { { "key", key.getName() }, { "chance", chance + "" } }));
							return;
						}
						key.getItemList().add(new KeyItem(item, chance));
						KeyModule.getInstance().getDatabase("keys").saveToFile(true);
						sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_ITEM_ADD_SUCESS,
								new String[][] { { "key", key.getName() }, { "chance", chance + "" } }));
					}
				}
			}
		}

		@Perm("remove")
		@Description(description = "Entfernt das Item.")
		@Usage(usage = "<key> <slot>")
		private class RemoveCommand extends PlayerCommand {

			public RemoveCommand(String name, String... aliases) {
				super(name, aliases);
			}

			@Override
			protected void execute(Player sender, ArgumentParser ap) {
				if (ap.hasExactly(2)) {
					String id = ap.get(1);
					int slot = ap.getInt(2);
					Key key = KeyModule.getInstance().getKey(id);
					if (key == null) {

					} else {
						if (slot >= key.getItemList().size()) {
							sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_ITEM_REMOVE_FAIL,
									new String[][] { { "key", key.getName() }, { "slot", slot + "" } }));
							return;
						}
						key.getItemList().remove(slot);
						KeyModule.getInstance().getDatabase("keys").saveToFile(true);
						sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_ITEM_REMOVE_SUCCESS,
								new String[][] { { "key", key.getName() }, { "slot", slot + "" } }));
					}
				}
			}
		}
	}

	@Perm("give")
	@Description(description = "Gibt dem Spieler Keys.")
	@Usage(usage = "<key> <player> [amount]")
	private class GiveCommand extends Command {

		public GiveCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {

			if (ap.hasAtLeast(2)) {

				String id = ap.get(1);
				String name = ap.get(2);
				int amount = ap.hasAtLeast(3) ? ap.getInt(3) : 1;

				if (ap.isValid()) {
					Key key = KeyModule.getInstance().getKey(id);

					Player player = Bukkit.getPlayer(name);
					if (key != null) {
						if (player != null) {

							for (int i = 0; i < amount; i++) {
								KeyModule.getInstance().giveItemStack(player, key.getKeyItemStack());
							}
							sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_GIVE_PLAYER,
									new String[][] { { "key", key.getName() }, { "player", player.getName() },
											{ "amount", amount + "" } }));

						} else {

						}
					} else {

					}
				} else {

				}
			}
		}
	}

	@Perm("give-all")
	@Description(description = "Gibt allen Spielern Keys.")
	@Usage(usage = "<key> <amount> [world]")
	private class GiveAllCommand extends Command {

		public GiveAllCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {

			if (ap.hasAtLeast(1)) {

				String id = ap.get(1);
				Key key = KeyModule.getInstance().getKey(id);
				if (key == null) {

				} else {
					if(ap.hasAtLeast(2)) {
						
						Integer i = ap.getInt(2);
						
						if(i == null) {
							
						} else {
							 
							if(ap.hasAtLeast(3)) {
								
								World world = Bukkit.getWorld(ap.get(3));
								
								if(world == null) {
									
								} else {
									 KeyModule.getInstance().giveKeys(key, i, Bukkit.getOnlinePlayers(),
											 world.getName());
								}
								
								
							} else {
								 KeyModule.getInstance().giveKeys(key, i, Bukkit.getOnlinePlayers());
							}
							
						}
						
					} else {
						
					}
				}
			} else {

			}

		}

	}

	@Perm("open")
	@Description(description = "Öffnet dem Spieler den Key.")
	@Usage(usage = "<key> <player>")
	private class OpenCommand extends Command {

		public OpenCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {
			if (ap.hasAtLeast(1)) {

				String id = ap.get(1);
				Key key = KeyModule.getInstance().getKey(id);
				if (key == null) {

				} else {

					if(ap.hasAtLeast(2)) {
						
						Player player = Bukkit.getPlayer(ap.get(2));
						
						if(player == null) {
							
						} else {
							 key.openKey(player);
							sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_OPEN_PLAYER,
									new String[][] { { "key", key.getName() }, { "player", player.getName() } }));
						}
						
					} else {
						
					}
					
				}
			} else {

			}
		}

	}

	@Perm("open-all")
	@Description(description = "Öffnet jedem Spieler den Key.")
	@Usage(usage = "<key> [world]")
	private class OpenAllCommand extends Command {

		public OpenAllCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {
			if (ap.hasAtLeast(1)) {

				String id = ap.get(1);
				Key key = KeyModule.getInstance().getKey(id);
				if (key == null) {

				} else {
					
					if(ap.hasAtLeast(2)) {
						
						World world = Bukkit.getWorld(ap.get(2));
						
						if(world == null) {
							
						} else {
							for (Player player : Bukkit.getOnlinePlayers()) {
								if (world == null || player.getWorld().getName().equalsIgnoreCase(world.getName())) {
									key.openKey(player);
								}
							}
						}
						
					} else {
						for (Player player : Bukkit.getOnlinePlayers()) {
							key.openKey(player);
						}
					}
					
				}
			} else {

			}
		}

	}
	
	@Perm("teleport")
	@Description(description = "Teleportiert dich zu dem Key.")
	@Usage(usage = "<key> <index>")
	private class TeleportCommand extends PlayerCommand {

		public TeleportCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			if (ap.hasAtLeast(1)) {

				String id = ap.get(1);
				Key key = KeyModule.getInstance().getKey(id);
				if (key == null) {

				} else {

					if(ap.hasAtLeast(2)) {
						
						Integer i = ap.getInt(2);
						
						if(i == null) {
							
						} else {
							 Location loc = i < key.getRedeemLocations().size() ?
							 key.getRedeemLocations().get(i) : null;
							 if (loc == null) {
								 player.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_TP_FAIL,
							 new String[][] { { "key", key.getName() }, { "id", i + "" } }));
							 return;
							 }
							 player.teleport(loc);
							 player.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_TP_SUCCESS,
							 new String[][] { { "key", key.getName() }, { "id", i + "" } }));
						}
						
					} else {
						
					}
					
				}
			} else {

			}
		}

	}

	@Perm("reload")
	@Description(description = "Reloaded die Config.")
	@Usage(usage = "")
	private class ReloadCommand extends Command {

		public ReloadCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {
			KeyModule.getInstance().reloadConfig();
			sender.sendMessage(KeyModule.convert(KeyModule.MESSAGE_CMD_RELOAD, null));
		}

	}

}
