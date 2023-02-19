package de.settla.local.portals;

import org.bukkit.entity.Player;

import de.settla.local.LocalPlugin;
import de.settla.local.tools.SpecialItemModule;
import de.settla.local.tools.tools.SelectionTool;
import de.settla.local.tools.tools.SelectionUser;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.OverviewCommand;
import de.settla.utilities.local.commands.Perm;
import de.settla.utilities.local.commands.PlayerCommand;
import de.settla.utilities.local.commands.Usage;
import de.settla.utilities.local.region.Galaxy;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.local.region.World;
import de.settla.utilities.local.region.form.BlockCuboid;

@Perm("portals")
public class PortalCommand extends OverviewCommand {

	public PortalCommand(String name) {
		super(name);
		addSubCommand(new Define("define"));
		addSubCommand(new Delete("delete"));
		addSubCommand(new List("list"));
	}

	@Perm("define")
	@Usage(usage = "<name> <warp>")
	class Define extends PlayerCommand {

		public Define(String name) {
			super(name);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			
			if (ap.hasExactly(2)) {
				
				String name = ap.get(1);
				String warp = ap.get(2);
				
				SelectionTool i = (SelectionTool) LocalPlugin.getInstance().getModule(SpecialItemModule.class).getSpecialItem("selection");
				SelectionUser user = i.getSpecialItemUser(player.getUniqueId());
				
				if(user == null) {
					user = new SelectionUser(player);
					i.addSpecialItemUser(player.getUniqueId(), user);
				}
				
				final SelectionUser finalUser = user;
				
				if(finalUser.checkPositions()) {

					Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(LocalPortalsModule.GALAXY_NAME);
					World world = galaxy.getWorld(player.getWorld().getName());
					
					if (world == null) {
						player.sendMessage(ChatConvention.title("Portals")+"Der Welt-Name ist nicht registriert.");
					} else {
						if (galaxy.getRegions(name).isEmpty()) {
							BlockCuboid cuboid = new BlockCuboid(user.getPos1(), user.getPos2());
							PortalRegion region = new PortalRegion(cuboid, name);
							region.setWarp(warp);
							world.register(region);
							player.sendMessage(ChatConvention.title("Portals")+"Das Portal wurde erzeugt.");
						} else {
							player.sendMessage(ChatConvention.title("Portals")+"Es existiert bereits ein Portal mit diesem Namen.");
						}
					}
					
				} else {
					player.sendMessage(ChatConvention.title("Portals")+"Du musst zwei Punkte auswählen!");
				}
				
			} else {
				player.sendMessage(ChatConvention.title("Portals")+"/portals define <name> <warp>");
			}
			
		}
	}
	
	@Perm("delete")
	@Usage(usage = "<name>")
	class Delete extends PlayerCommand {

		public Delete(String name) {
			super(name);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			if (ap.hasExactly(1)) {
				
				String name = ap.get(1);
			
				Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(LocalPortalsModule.GALAXY_NAME);
				
				galaxy.getRegions(name).forEach(region -> {
					region.getRegionRegistery().getWorld().delete(region);
					player.sendMessage(ChatConvention.title("Portals") + "Region: " + region.id() + " wurde gelöscht.");
				});
				
			} else {
				player.sendMessage(ChatConvention.title("Portals")+"/portals delete <name>");
			}
		}
	}
	
	@Perm("list")
	class List extends PlayerCommand {

		public List(String name) {
			super(name);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(LocalPortalsModule.GALAXY_NAME);
			
			StringBuilder sb = new StringBuilder();
			
			galaxy.throughWorlds(w -> w.getRegionIndex().throughRegions(r -> {
				sb.append(r.id());
				sb.append(" ");
			}));
			
			String str = sb.toString();
			
			
			
			player.sendMessage(ChatConvention.title("Portals")+"Liste: [" + (str.length() > 0 ? str.substring(0, str.length() - 1) : str) + "]");
			
		}
	}
	
}
