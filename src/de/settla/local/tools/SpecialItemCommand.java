package de.settla.local.tools;

import static de.settla.utilities.local.ChatConvention.spezial;
import static de.settla.utilities.local.ChatConvention.title;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.settla.local.LocalPlugin;
import de.settla.local.tools.tools.MoneyItem;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.Description;
import de.settla.utilities.local.commands.OverviewCommand;
import de.settla.utilities.local.commands.Perm;
import de.settla.utilities.local.commands.PlayerCommand;
import de.settla.utilities.local.commands.Usage;

@Perm("tools")
public class SpecialItemCommand extends OverviewCommand {

	public SpecialItemCommand(String name, String... aliases) {
		super(name, aliases);
		addSubCommand(new GetCommand("get"));
		addSubCommand(new ListCommand("list"));
		addSubCommand(new MoneyPaperCommand("moneypaper"));
	}

	@Perm("get")
	@Description(description = "Erstellt Werkzeuge!")
	@Usage(usage = "<tool>")
	private class GetCommand extends PlayerCommand {

		public GetCommand(String name, String... aliases) {
			super(name, aliases);
		}
		
		@Override
		protected void execute(Player player, ArgumentParser ap) {
			if(player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
				if(ap.hasExactly(1)) {
					String itemId = ap.get(1);
					
					SpecialItem<?> tool = LocalPlugin.getInstance().getModule(SpecialItemModule.class).getSpecialItem(itemId);
					
					if(tool == null) {
						player.sendMessage(title("Tools")+"Du hast keine validen namen eingegeben!");
					} else {
						player.sendMessage(title("Tools")+"Du hast erfolgreich das Tool "+spezial(tool.getItemId()) + " bekommen!");
						player.getInventory().addItem(tool.getItemStack(player.getItemInHand().clone()));
					}
				} else {
					player.sendMessage(title("Tools")+"Du musst ein Tool Namen angeben!");
				}
			} else {
				player.sendMessage(title("Tools")+"Du brauchst ein Item in der Hand!");
			}
		}
	}
	
	@Perm("list")
	@Description(description = "Liste aller Werkzeuge.")
	@Usage(usage = "")
	private class ListCommand extends PlayerCommand {

		public ListCommand(String name, String... aliases) {
			super(name, aliases);
		}
		
		@Override
		protected void execute(Player player, ArgumentParser ap) {
			player.sendMessage(title("Tools")+LocalPlugin.getInstance().getModule(SpecialItemModule.class).getToolNames());
		}
	}
	
	@Perm("money")
	@Description(description = "Erstellt ein Geldschein.")
	@Usage(usage = "<price>")
	private class MoneyPaperCommand extends PlayerCommand {

		public MoneyPaperCommand(String name, String... aliases) {
			super(name, aliases);
		}
		
		@Override
		protected void execute(Player player, ArgumentParser ap) {
			if (ap.hasExactly(1)) {
				
				Integer price = ap.getInt(1);
				
				if (price == null) {
					player.sendMessage(title("Tools")+"Du musst ein validen Preis angeben.");
				} else {
					SpecialItem<?> tool = LocalPlugin.getInstance().getModule(SpecialItemModule.class).getSpecialItem("money");
					
					if (tool == null) {
						player.sendMessage(title("Tools")+"Fehler frag Salkin!");
					} else {
						player.sendMessage(title("Tools")+"Du hast erfolgreich das Tool "+spezial(tool.getItemId()) + " bekommen!");
						player.getInventory().addItem(MoneyItem.getMoneyPaper(price));
					}
				}
				
			} else {
				player.sendMessage(title("Tools")+"Du musst ein validen Preis angeben.");
			}
		}
	}
}
