package de.settla.global.commands;

import java.util.UUID;
import java.util.function.BiFunction;

import de.settla.economy.Account;
import de.settla.economy.AccountHandler;
import de.settla.economy.accounts.Purse;
import de.settla.economy.accounts.PurseHandler;
import de.settla.global.GlobalPlugin;
import de.settla.utilities.global.TextBuilder;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.Command;
import de.settla.utilities.global.command.OverviewCommand;
import net.md_5.bungee.api.CommandSender;

public class EconomyCommand extends OverviewCommand {

	public EconomyCommand(String name, String... aliases) {
		super(name, aliases);
		addSubCommand(new EconomyTransactionCommand<UUID, Purse, PurseHandler>("purse", PurseHandler.class, (uuid, purse) -> {
			
			return GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid).name() + " " + GlobalPlugin.getInstance().getEconomy().getWrapper().backward(purse.getWrappedBalance()) + "$";
			
		}));
	}

	class EconomyTransactionCommand<I, H extends Account<I>, A extends AccountHandler<I, H>> extends OverviewCommand {

		private final Class<A> accountHandler;
		
		private final BiFunction<I, H, String> function;
		
		public EconomyTransactionCommand(String name, Class<A> accountHandler, BiFunction<I, H, String> function) {
			super(name);
			this.accountHandler = accountHandler;
			this.function = function;
			addSubCommand(new CheckTransactionsCommand("check"));
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {
			
		}
		
		class CheckTransactionsCommand extends Command {

			public CheckTransactionsCommand(String name) {
				super(name);
			}

			@Override
			protected void execute(CommandSender sender, ArgumentParser ap) {
				if (ap.hasExactly(1)) {
					String name = ap.get(1);
					
					GlobalPlugin.getInstance().getUuid(uuid -> {
						
						if(uuid == null || !GlobalPlugin.getInstance().getGlobalPlayers().contains(uuid)) {
							new TextBuilder().title("Economy").text("Dieser Spieler ist uns nicht bekannt.").send(sender);
						} else {
							
							GlobalPlugin.getInstance().getEconomy().getAccountHandler(accountHandler).accounts().forEach((i, a) -> {
								
								
								
								new TextBuilder().text(function.apply(i, a)).send(sender);
							});
							
							
//							if (transactions.isEmpty()) {
//								sender.sendMessage(ChatConvention.title("Economy") + "Der Spieler " + op.getName()
//										+ " hat noch keine Transaktionen.");
//							} else {
//								sender.sendMessage(" ");
//								sender.sendMessage(ChatConvention.title("Economy") + "Alle " + transactions.size()
//										+ " letzten Transaktionen von: " + op.getName());
//								transactions.forEach(tran -> {
//									String affected = (tran instanceof UuidAffectedTransaction
//											? Bukkit.getOfflinePlayer((UUID) tran.getAffected()).getName()
//											: tran.getAffectedString());
//									// String affected = (tran.getAffected() == null
//									// ? null :
//									// Bukkit.getOfflinePlayer(tran.getAffected()).getName());
//									sender.sendMessage(ChatColor.GRAY + "+ " + (tran.isInput() ? "§aINPUT§7" : "§cOUTPUT§7")
//											+ "  Affected: " + affected + "  : "
//											+ EconomyModule.unwrapBalance(tran.getWrappedBalance()) + "$   Reason: "
//											+ tran.getName());
//								});
//							}
						}
						
					}, name);

				} else {
					new TextBuilder().title("Economy").text("Du musst einen validen Spieler-Namen eingeben. /eco transactions <player>").send(sender);
				}
			}
			
		}
		
	}
	
}
