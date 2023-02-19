package de.settla.local.npc;

import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.Description;
import de.settla.utilities.local.commands.OverviewCommand;
import de.settla.utilities.local.commands.Perm;
import de.settla.utilities.local.commands.PlayerCommand;
import de.settla.utilities.local.commands.Usage;

@Perm("npc")
public class NpcCommand extends OverviewCommand {

	public NpcCommand(String name) {
		super(name);
		addSubCommand(new ListCommand("list"));
		addSubCommand(new ListModelsCommand("models"));
		addSubCommand(new SpawnCommand("spawn"));
		addSubCommand(new DestroyCommand("destroy"));
	}

	@Perm("models")
	@Usage(usage = "")
	@Description(description = "Listet alle Models auf.")
	class ListModelsCommand extends PlayerCommand {

		public ListModelsCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			StringBuilder sb = new StringBuilder();
			AtomicBoolean first = new AtomicBoolean(true);
			LocalPlugin.getInstance().getModule(NpcModule.class).forEachModel(npc -> {
				if (first.get()) {
					first.set(false);
				} else {
					sb.append(", ");
				}
				sb.append(npc.getModelName());
			});
			player.sendMessage(ChatConvention.title("NPC") + sb.toString());
		}
	}

	@Perm("list")
	@Usage(usage = "")
	@Description(description = "Listet alle NPCs auf.")
	class ListCommand extends PlayerCommand {

		public ListCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			StringBuilder sb = new StringBuilder();
			AtomicBoolean first = new AtomicBoolean(true);
			LocalPlugin.getInstance().getModule(NpcModule.class).throughNpcs(npc -> {
				if (first.get()) {
					sb.append("(");
					sb.append(npc.getNpcEntity().getType() + ": " + npc.getName());
					sb.append(")");
				} else {
					sb.append(", (");
					sb.append(npc.getNpcEntity().getType() + ": " + npc.getName());
					sb.append(")");
				}
				first.set(false);
			});
			player.sendMessage(ChatConvention.title("NPC") + sb.toString());
		}
	}

	@Perm("spawn")
	@Usage(usage = "<type> <model> <name>")
	@Description(description = "Erstellt ein neuen NPC.")
	class SpawnCommand extends PlayerCommand {

		public SpawnCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {

			if (ap.hasAtLeast(3)) {
				
				EntityType type = null;
				
				for (EntityType t : EntityType.values()) {
					if (t.name().equalsIgnoreCase(ap.get(1))) {
						type = t;
					}
				}
				
				if (type == null) {
					player.sendMessage(ChatConvention.title("NPC") + "Gebe einen validen Entitytyp an.");
				} else {

					NpcModule module = LocalPlugin.getInstance().getModule(NpcModule.class);
					NpcModel model = module.getModel(ap.get(2));
					String name = ap.get(3);
					Npc npc = module.getNpc(name);
					NpcData data = module.getNpcDatas().getNpcData(name);

					if (npc != null) {
						player.sendMessage(
								ChatConvention.title("NPC") + "Es gibt bereits ein NPC mit diesem Namen.");
					} else {

						if (model == null) {
							player.sendMessage(ChatConvention.title("NPC") + "Gebe ein valide Vorlage an!");
						} else {

							if (data != null) {
								player.sendMessage(
										ChatConvention.title("NPC") + "Fehler... nochmaliges registrieren...");
								module.registerNpc(data);
							} else {

								data = new NpcData(model.getModelName(), name, player.getLocation(), type);
								module.getNpcDatas().addNpcData(data);
								npc = module.registerNpc(data);

								player.sendMessage(ChatConvention.title("NPC") + "Du hast ein neuen NPC "
										+ ChatConvention.spezial(data.getName()) + " erstellt.");
								// npc.getNpcEntity().show(player);
							}
						}
					}
					
//					if (type.isAlive()) {
//
//						NpcModule module = LocalPlugin.getInstance().getModule(NpcModule.class);
//						NpcModel model = module.getModel(ap.get(2));
//						String name = ap.get(3);
//						Npc npc = module.getNpc(name);
//						NpcData data = module.getNpcDatas().getNpcData(name);
//
//						if (npc != null) {
//							player.sendMessage(
//									ChatConvention.title("NPC") + "Es gibt bereits ein NPC mit diesem Namen.");
//						} else {
//
//							if (model == null) {
//								player.sendMessage(ChatConvention.title("NPC") + "Gebe ein valide Vorlage an!");
//							} else {
//
//								if (data != null) {
//									player.sendMessage(
//											ChatConvention.title("NPC") + "Fehler... nochmaliges registrieren...");
//									module.registerNpc(data);
//								} else {
//
//									data = new NpcData(model.getModelName(), name, player.getLocation(), type);
//									module.getNpcDatas().addNpcData(data);
//									npc = module.registerNpc(data);
//
//									player.sendMessage(ChatConvention.title("NPC") + "Du hast ein neuen NPC "
//											+ ChatConvention.spezial(data.getName()) + " erstellt.");
//									// npc.getNpcEntity().show(player);
//								}
//							}
//						}
//					} else {
//						player.sendMessage(ChatConvention.title("NPC") + "Dieser Typ ist nicht 'Lebendig'");
//					}
				}
			}
		}
	}

	@Perm("destroy")
	@Usage(usage = "<name>")
	@Description(description = "Löscht den NPC.")
	class DestroyCommand extends PlayerCommand {

		public DestroyCommand(String name, String... aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			if (ap.hasAtLeast(1)) {
				String name = ap.get(1);
				NpcModule module = LocalPlugin.getInstance().getModule(NpcModule.class);
				NpcData data = module.getNpcDatas().getNpcData(name);

				Npc npc = module.getNpc(name);

				if (npc != null) {
					module.unregisterNpc(name);
					npc.getNpcEntity().destroy();
				}

				if (data != null) {

					module.getNpcDatas().removeNpcData(data);

					player.sendMessage(ChatConvention.title("NPC") + "Du hast den NPC "
							+ ChatConvention.spezial(data.getName()) + " entfernt.");

				} else {
					player.sendMessage(ChatConvention.title("NPC") + "Du musst einen validen NPC-Namen eingeben!");
				}
			} else {
				player.sendMessage(ChatConvention.title("NPC") + "Du musst einen validen NPC-Namen eingeben!");
			}
		}
	}

}
