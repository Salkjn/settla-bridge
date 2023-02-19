package de.settla.local.economysigns;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.Description;
import de.settla.utilities.local.commands.OverviewCommand;
import de.settla.utilities.local.commands.Perm;
import de.settla.utilities.local.commands.PlayerCommand;
import de.settla.utilities.local.commands.Usage;

public class EconomySignCommand extends OverviewCommand {
	
	public EconomySignCommand(String...top) {
		super("economysign", "ecosign");
		
		for (String name : top) {
			this.addSubCommand(new EconomySignTopCommand(name));
		}
		
	}

	class EconomySignTopCommand extends PlayerCommand {

		public EconomySignTopCommand(String name) {
			super(name);
			this.addSubCommand(new EconomyAddSignTopCommand("add", name));
			this.addSubCommand(new EconomyRemoveSignTopCommand("remove", name));
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			
		}
		
	}
	
	@Usage(usage = "<rank>")
	@Description(description = "Setzt Schild für Top 10.")
	@Perm("economysign.setsign")
	class EconomyAddSignTopCommand extends PlayerCommand {
		
		private final String name;
		
		public EconomyAddSignTopCommand(String cmd, String name) {
			super(cmd);
			this.name = name;
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			if (ap.hasExactly(1)) {
				Integer rank = ap.getInt(1);
				@SuppressWarnings("deprecation")
				Location signLocation = player.getTargetBlock((HashSet<Byte>) null, 10).getLocation();
				if (rank == null || rank < 1 || rank > 10) {
					player.sendMessage(ChatConvention.title(name) + "Rang 1-10 angeben.");
				} else {
					if (signLocation == null) {
						player.sendMessage(ChatConvention.title(name) + "Du schaust auf keinen Block.");
					} else {
						LocalPlugin.getInstance().getModule(EconomySignTopModule.class).modules(m ->  {
							if(m.getName().equalsIgnoreCase(name)) {
								if(m instanceof EconomySignTop<?>) {
									EconomySignTop<?> e = (EconomySignTop<?>)m;
									e.consumeSigns(list -> {
										boolean noneMatch = list.stream()
												.noneMatch(sign -> sign.getLocation().getWorld().equals(signLocation.getWorld())
														&& sign.getLocation().distanceSquared(signLocation) == 0);
										if (noneMatch) {
											list.add(new EconomySign(rank, signLocation));
											player.sendMessage(ChatConvention.title(name) + "Du hast das Schild für Rang "
													+ ChatConvention.spezial(rank) + " hinzugefügt.");
											e.setDirty(true);
										} else {
											player.sendMessage(
													ChatConvention.title(name) + "Dort existiert bereits ein Schild.");
										}
									});
								}
							}
						});
					}
				}
			}
		}
	}
	
	@Description(description = "Löscht Schild der Top 10.")
	@Perm("economysign.removesign")
	class EconomyRemoveSignTopCommand extends PlayerCommand {
		
		private final String name;
		
		public EconomyRemoveSignTopCommand(String cmd, String name) {
			super(cmd);
			this.name = name;
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			@SuppressWarnings("deprecation")
			Location signLocation = player.getTargetBlock((HashSet<Byte>) null, 10).getLocation();

			if (signLocation == null) {
				player.sendMessage(ChatConvention.title(name) + "Du schaust auf keinen Block.");
			} else {
				
				LocalPlugin.getInstance().getModule(EconomySignTopModule.class).modules(m ->  {
					if(m.getName().equalsIgnoreCase(name)) {
						if(m instanceof EconomySignTop<?>) {
							EconomySignTop<?> e = (EconomySignTop<?>)m;
							e.consumeSigns(list -> {
								List<EconomySign> signs = list.stream()
										.filter(sign -> !(sign.getLocation().getWorld().equals(signLocation.getWorld())
												&& sign.getLocation().distanceSquared(signLocation) == 0))
										.collect(Collectors.toList());
								list.clear();
								list.addAll(signs);
								e.setDirty(true);
								player.sendMessage(ChatConvention.title(name) + "Schild entfernt.");
							});
						}
					}
				});
			}
		}
	}
	
}
