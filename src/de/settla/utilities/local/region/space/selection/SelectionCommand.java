package de.settla.utilities.local.region.space.selection;

import java.io.FileNotFoundException;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.settla.local.LocalPlugin;
import de.settla.local.tools.SpecialItemModule;
import de.settla.local.tools.tools.SelectionTool;
import de.settla.local.tools.tools.SelectionUser;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.OverviewCommand;
import de.settla.utilities.local.commands.Perm;
import de.settla.utilities.local.commands.PlayerCommand;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.local.region.World;
import de.settla.utilities.local.region.form.BlockCuboid;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.BlockList;
import de.settla.utilities.local.region.space.Clipboard;
import de.settla.utilities.local.region.space.DataException;
import de.settla.utilities.local.region.space.Room;
import de.settla.utilities.local.region.space.Template;
import de.settla.utilities.local.region.space.TemplateModule;
import de.settla.utilities.local.region.space.generation.GenerationModule;
import de.settla.utilities.local.region.space.generation.GenerationPaster;
import de.settla.utilities.storage.StorageException;

@Perm("selection")
public class SelectionCommand extends OverviewCommand {
	
	public SelectionCommand() {
		super("selection");
		addSubCommand(new Copy("copy"));
		addSubCommand(new Paste("paste"));
		addSubCommand(new Save("save"));
		addSubCommand(new Load("load"));
		addSubCommand(new Rotate("rotate"));
	}

	private void calculateClipboard(Vector offset, Form form, World world, Consumer<Clipboard> consumer) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Vector origin = form.minimum();
				Vector size = origin.subtract(form.maximum()).positive();
				Room room = new Room((int)size.getX()+1, (int)size.getY()+1, (int)size.getZ()+1);
				BlockList blocks = new FormSelector(form, room, origin).select(world.getBukkitWorld());
				consumer.accept(new Clipboard(blocks, room, form, offset, origin));
			}
		}.runTaskAsynchronously(LocalPlugin.getInstance());
	}
	
	private boolean alreadyGeneration(Form form, String world) {
		return !LocalPlugin.getInstance().getModule(GenerationModule.class)
		.getGenerations(world, form).isEmpty();
	}
	
//	private String getName(String id) {
//		return id.substring("selection_".length());
//	}
	
	private String getId(String name) {
		return "selection_"+name;
	}
	
	private class Rotate extends PlayerCommand {

		public Rotate(String name) {
			super(name);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			
			int rotation = ap.hasAtLeast(1) && ap.get(1).matches("[0-9]+") ? ap.getInt(1) : 1;
			
			SelectionTool i = (SelectionTool) LocalPlugin.getInstance().getModule(SpecialItemModule.class).getSpecialItem("selection");
			SelectionUser user = i.getSpecialItemUser(player.getUniqueId());
			
			if(user == null) {
				user = new SelectionUser(player);
				i.addSpecialItemUser(player.getUniqueId(), user);
			}
			
			final SelectionUser finalUser = user;
			
			Clipboard board = finalUser.getClipboard();
			if(board == null) {
				player.sendMessage(ChatConvention.title("Selection")+"Du hast keine Blöcke zu setzen.");
				return;
			} else {
				Vector offset = new Vector(player.getLocation()).floor();
				for (int j = 0; j < rotation; j++) {
					board.rotate90(offset);
				}
				player.sendMessage(ChatConvention.title("Selection")+"Du hast die Blöcke rotiert.");
			}
			
		}
		
	}
	
	@Perm("load")
	private class Load extends PlayerCommand {

		public Load(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			
			if(ap.hasAtLeast(1)) {
				
				String name = ap.get(1);
			
				SelectionTool i = (SelectionTool) LocalPlugin.getInstance().getModule(SpecialItemModule.class).getSpecialItem("selection");
				SelectionUser user = i.getSpecialItemUser(player.getUniqueId());
				
				if(user == null) {
					user = new SelectionUser(player);
					i.addSpecialItemUser(player.getUniqueId(), user);
				}
				
				final SelectionUser finalUser = user;
				
				String id = getId(name);
				
				if(!BlockList.existsBlockList(id)) {
					player.sendMessage(ChatConvention.title("Selection")+"Es wurde kein Template " + ChatConvention.spezial(name) + " gefunden.");
				} else {
					Template template;
					try {
						template = Template.loadTemplate(LocalPlugin.getInstance().getModule(TemplateModule.class), id);
						if(template == null) {
							player.sendMessage(ChatConvention.title("Selection")+"Es wurde kein Template " + ChatConvention.spezial(name) + " gefunden.");
						} else {
							finalUser.setClipboard(template.clipboard());
							player.sendMessage(ChatConvention.title("Selection")+"Das Template " + ChatConvention.spezial(name) + " wurde geladen.");
						}	
					} catch (FileNotFoundException | DataException e) {
						player.sendMessage(ChatConvention.title("Selection")+"gebe einen validen Template Namen an.");
					}
				}
			} else {
				player.sendMessage(ChatConvention.title("Selection")+"gebe einen validen Template Namen an.");
			}
			
		}
	}	
	
	@Perm("save")
	private class Save extends PlayerCommand {

		public Save(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			
			if(ap.hasAtLeast(1)) {
				
				String name = ap.get(1);
			
				SelectionTool i = (SelectionTool) LocalPlugin.getInstance().getModule(SpecialItemModule.class).getSpecialItem("selection");
				SelectionUser user = i.getSpecialItemUser(player.getUniqueId());
				
				if(user == null) {
					user = new SelectionUser(player);
					i.addSpecialItemUser(player.getUniqueId(), user);
				}
				
				final SelectionUser finalUser = user;
				
				Clipboard board = finalUser.getClipboard();
				if(board == null) {
					player.sendMessage(ChatConvention.title("Selection")+"Du hast keine Blöcke zu setzen.");
					return;
				} else {
					
					String id = getId(name);
					
					if(BlockList.existsBlockList(id)) {
						player.sendMessage(ChatConvention.title("Selection")+"Es wurde das Template " + ChatConvention.spezial(name) + " überschrieben und gesichert.");
					} else {
						player.sendMessage(ChatConvention.title("Selection")+"Es wurde das Template " + ChatConvention.spezial(name) + " gesichert.");
					}
					
					Vector size = board.getForm().maximum().subtract(board.getForm().minimum());
					Room room = new Room(size.getBlockX() + 1, size.getBlockY() + 1, size.getBlockZ() + 1);
					Template template = Template.createTemplate(LocalPlugin.getInstance().getModule(TemplateModule.class), id, board.getBlocks(), room, board.getForm(), board.getOffset(), board.getOrigin());
					
					try {
						template.saveTemplate();
					} catch (StorageException e) {
						e.printStackTrace();
					}
					
				}
				
			} else {
				player.sendMessage(ChatConvention.title("Selection")+"gebe einen validen Template Namen an.");
			}
			
		}
		
	}
	
	@Perm("copy")
	private class Copy extends PlayerCommand {

		public Copy(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			
			SelectionTool i = (SelectionTool) LocalPlugin.getInstance().getModule(SpecialItemModule.class).getSpecialItem("selection");
			SelectionUser user = i.getSpecialItemUser(player.getUniqueId());
			
			if(user == null) {
				user = new SelectionUser(player);
				i.addSpecialItemUser(player.getUniqueId(), user);
			}
			
			final SelectionUser finalUser = user;
			
			if(finalUser.checkPositions()) {

				BlockCuboid form = new BlockCuboid(user.getPos1(), user.getPos2());
				Vector offset = new Vector(player.getLocation()).floor();
				World world = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME).getOrCreateWorld(player.getWorld().getName(), GenerationModule.WILDNESS);
				
				player.sendMessage(ChatConvention.title("Selection")+"Es werden Blöcke kopiert...");
				
				calculateClipboard(offset, form, world, board -> {
					finalUser.setClipboard(board);
					player.sendMessage(ChatConvention.title("Selection")+"Du hast " + ChatConvention.spezial(board.getBlocks().size()) + " Blöcke kopiert.");
				});
				
			} else {
				player.sendMessage(ChatConvention.title("Selection")+"Du musst zwei Punkte auswählen!");
			}	
		}
	}
	
	@Perm("paste")
	private class Paste extends PlayerCommand {

		public Paste(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			
			SelectionTool i = (SelectionTool) LocalPlugin.getInstance().getModule(SpecialItemModule.class).getSpecialItem("selection");
			SelectionUser user = i.getSpecialItemUser(player.getUniqueId());
			
			if(user == null) {
				user = new SelectionUser(player);
				i.addSpecialItemUser(player.getUniqueId(), user);
			}
			
			final SelectionUser finalUser = user;
			
			Integer time = ap.hasAtLeast(1) ? ap.getInt(1) : 0; 
			
			Clipboard board = finalUser.getClipboard();
			if(board == null) {
				player.sendMessage(ChatConvention.title("Selection")+"Du hast keine Blöcke zu setzen.");
				return;
			} else {
				Vector offset = new Vector(player.getLocation()).floor();
				player.sendMessage(ChatConvention.title("Selection")+"Du hast " + ChatConvention.spezial(board.getBlocks().size()) + " Blöcke gesetzt.  ("+ChatConvention.spezial((time == null ? 0 : time) + "sec")+")");
				
				board.move(offset.subtract(board.getOffset()));
				
				if(!alreadyGeneration(board.getForm(), player.getWorld().getName())) {
					String blockListId = BlockList.generateId(20);
					BlockList.saveBlockList(blockListId, board.getBlocks());
					GenerationPaster paster;
					paster = board.createGeneration(player.getWorld().getName(), blockListId, true, false);
					paster.calculateTime(time == null ? 0 : time, () -> paster.paste());
				} else {
					player.sendMessage(ChatConvention.title("Selection")+"Hier wird bereits etwas generiert.");
				}
			}
		}
	}
}
