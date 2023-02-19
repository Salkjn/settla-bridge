package de.settla.global.guilds;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import de.settla.economy.Transfer;
import de.settla.economy.accounts.GuildAccountHandler;
import de.settla.economy.accounts.PurseHandler;
import de.settla.economy.accounts.ServerAccountHandler;
import de.settla.global.GlobalPlugin;
import de.settla.utilities.global.TextBuilder;
import de.settla.utilities.global.command.ArgumentParser;
import de.settla.utilities.global.command.OverviewCommand;
import de.settla.utilities.global.command.PlayerCommand;
import de.settla.utilities.global.command.annotations.Description;
import de.settla.utilities.global.command.annotations.Usage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GuildCommand extends OverviewCommand {

	private final GuildGlobalModule module;

	public GuildCommand(GuildGlobalModule module, String name, String... aliases) {
		super(name, aliases);
		this.module = module;
		addSubCommand(new CreateGuildCommand("create"));
		addSubCommand(new DeleteGuildCommand("delete"));
		addSubCommand(new BalanceGuildCommand("balance"));
		addSubCommand(new WithdrawGuildCommand("withdraw"));
		addSubCommand(new DepositGuildCommand("deposit"));
		addSubCommand(new InviteGuildCommand("invite"));
		addSubCommand(new InfoGuildCommand("info"));
		addSubCommand(new JoinGuildCommand("join"));
		addSubCommand(new DeclineGuildCommand("decline"));
		addSubCommand(new LeaveGuildCommand("leave"));
		addSubCommand(new KickGuildCommand("kick"));
		addSubCommand(new PromoteGuildCommand("promote"));
		addSubCommand(new DemoteGuildCommand("demote"));
	}

	private CachedGlobalGuildList getGlobalGuildList() {
		return module.getGuildList().getCache();
	}

	@Description(description = "Erstellt eine Gilde. (Kosten 1000$)")
	@Usage(usage = "<longname(5-14)> <shortname(2-4)>")
	class CreateGuildCommand extends PlayerCommand {

		public CreateGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild == null) {
				if (ap.hasExactly(2)) {

					String longName = ap.get(1);
					String shortName = ap.get(2);

					// longName [5, 14]
					if (longName.length() > 4 && longName.length() < 15) {

						// shortName [2, 4]
						if (shortName.length() > 1 && shortName.length() < 5) {

							// matches TODO

							boolean failLongName = longName.contains("[a-zA-Z0-9]+");
							boolean failShortName = shortName.contains("[a-zA-Z0-9]+");

							if (failLongName) {
								new TextBuilder().title("Gilde")
										.text("Bitte gebe einen Clannamen an, der zwischen 5-14 Zeichen lang ist.")
										.send(player);
							} else if (failShortName) {
								new TextBuilder().title("Gilde")
										.text("Bitte gebe einen Clan-Kürzel an, der zwischen 2-4 Zeichen lang ist.")
										.send(player);
							} else {

								if (getGlobalGuildList().getGuildByLongName(longName) == null) {
									if (getGlobalGuildList().getGuildByShortName(shortName) == null) {

										Transfer transfer = GlobalPlugin.getInstance().getEconomy().transfer(
												PurseHandler.class, player.getUniqueId(), UUID.class,
												ServerAccountHandler.class, "guild", String.class,
												GlobalPlugin.getInstance().getEconomy().getWrapper().forward(1000.0));

										if (transfer.isSuccess()) {
											guild = module.getGuildList().createNewGuild();
											guild.getName().setLongName(ap.get(1));
											guild.getName().setShortName(ap.get(2));
											guild.getOwner().add(player.getUniqueId());
											new TextBuilder().title("Gilde")
													.text("Du hast die Gilde erstellt. Die Gründungskosten belaufen sich auf 1000$.")
													.send(player);

											long balance = GlobalPlugin.getInstance().getEconomy()
													.getBalance(GuildAccountHandler.class, guild.id(), UUID.class);
											if (balance != 0)
												GlobalPlugin.getInstance().getEconomy().transfer(
														GuildAccountHandler.class, guild.id(), UUID.class,
														ServerAccountHandler.class, "guild", String.class, balance);
										} else {
											new TextBuilder().title("Gilde")
													.text("Du hast nicht genügend Geld, um eine Gilde gründen zu können.")
													.send(player);
										}

									} else {
										new TextBuilder().title("Gilde").text("Dieses Clan-Kürzel existiert bereits!")
												.send(player);
									}
								} else {
									new TextBuilder().title("Gilde").text("Dieser Clanname existiert bereits!")
											.send(player);
								}
							}

						} else {
							new TextBuilder().title("Gilde")
									.text("Bitte gebe einen Clan-Kürzel an, der zwischen 2-4 Zeichen lang ist.")
									.send(player);
						}

					} else {
						new TextBuilder().title("Gilde")
								.text("Bitte gebe einen Clannamen an, der zwischen 5-14 Zeichen lang ist.")
								.send(player);
					}

				} else {
					new TextBuilder().title("Gilde").text("Verwendung: /guild create [longname] [shortname]")
							.send(player);
				}
			} else {
				new TextBuilder().title("Gilde").text("Du bist bereits in einer Gilde.").send(player);
			}
		}
	}

	@Description(description = "Löscht eine Gilde.")
	class DeleteGuildCommand extends PlayerCommand {

		public DeleteGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild != null) {

				if (guild.getOwner().contains(player.getUniqueId())) {

					GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(guild,
							new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY).append(player.getName())
									.color(ChatColor.GREEN)
									.append(" hat die Gilde aufgelöst!\n")
									.color(ChatColor.GRAY).create());
					
					module.getGuildList().removeGuild(guild);

					long balance = GlobalPlugin.getInstance().getEconomy().getBalance(GuildAccountHandler.class,
							guild.id(), UUID.class);

					if (balance > 0) {
						Transfer transfer = GlobalPlugin.getInstance().getEconomy().fill(GuildAccountHandler.class,
								guild.id(), UUID.class, PurseHandler.class, player.getUniqueId(), UUID.class, balance);

						new TextBuilder().title("Gilde")
								.text("Aufgrund der Auflösung deiner Gilde bekommst du " + GlobalPlugin.getInstance()
										.getEconomy().getWrapper().backward(transfer.change())
										+ "$ von dem Gilden-Konto.")
								.send(player);

						balance = transfer.endFrom();
						if (balance > 0) {
							GlobalPlugin.getInstance().getEconomy().transfer(GuildAccountHandler.class, guild.id(),
									UUID.class, ServerAccountHandler.class, "guild", String.class, balance);
						}

					}

					GlobalPlugin.getInstance().getEconomy().delete(GuildAccountHandler.class, guild.id(), UUID.class);

//					new TextBuilder().title("Gilde").text("Du hast die Gilde aufgelöst!").send(player);
				} else {
					new TextBuilder().title("Gilde").text("Du bist nicht der Inhaber der Gilde.").send(player);
				}

			} else {
				new TextBuilder().title("Gilde").text("Du bist in keiner Gilde.").send(player);
			}
		}
	}

	@Description(description = "Hebt Geld vom Gilden-Konto ab.")
	@Usage(usage = "<balance>")
	class WithdrawGuildCommand extends PlayerCommand {

		public WithdrawGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild != null) {

				if (guild.getOwner().contains(player.getUniqueId())
						|| guild.getHelper().contains(player.getUniqueId())) {
					if (ap.hasExactly(1)) {

						Integer balance = ap.getInt(1);

						if (balance != null && balance > 0) {

							Transfer transfer = GlobalPlugin.getInstance().getEconomy().transfer(
									GuildAccountHandler.class, guild.id(), UUID.class, PurseHandler.class,
									player.getUniqueId(), UUID.class, GlobalPlugin.getInstance().getEconomy()
											.getWrapper().forward(balance.doubleValue()));

							if (transfer.isSuccess()) {
								new TextBuilder().title("Gilde")
										.text("Du hast " + balance + "$ vom Gilden-Konto abgehoben.").send(player);
							} else {
								new TextBuilder().title("Gilde")
										.text("Es ist ein Fehler bei der Überweisung aufgetreten.").send(player);
							}

						} else {
							new TextBuilder().title("Gilde").text("Du musst eine gültige Zahl angeben.").send(player);
						}
					} else {
						new TextBuilder().title("Gilde").text("Beispiel-Verwendung: /guild withdraw 100").send(player);
					}
				} else {
					new TextBuilder().title("Gilde").text("Du musst Owner oder Helfer sein.").send(player);
				}
			} else {
				new TextBuilder().title("Gilde").text("Du bist in keiner Gilde.").send(player);
			}
		}
	}

	@Description(description = "Deponiert Geld auf Gilden-Konto.")
	@Usage(usage = "<balance>")
	class DepositGuildCommand extends PlayerCommand {

		public DepositGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild != null) {

				if (ap.hasExactly(1)) {

					Integer balance = ap.getInt(1);

					if (balance != null && balance > 0) {

						Transfer transfer = GlobalPlugin.getInstance().getEconomy().transfer(PurseHandler.class,
								player.getUniqueId(), UUID.class, GuildAccountHandler.class, guild.id(), UUID.class,
								GlobalPlugin.getInstance().getEconomy().getWrapper().forward(balance.doubleValue()));

						if (transfer.isSuccess()) {

							// new TextBuilder().title("Gilde")
							// .text("Du hast " + balance + "$ auf das
							// Gilden-Konto überwiesen.").send(player);

							GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(guild,
									new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY).append(player.getName())
											.color(ChatColor.GREEN)
											.append(" hat " + balance + "$ auf das Gilden-Konto überwiesen.\n")
											.color(ChatColor.GRAY).create());

						} else {
							new TextBuilder().title("Gilde").text("Es ist ein Fehler bei der Überweisung aufgetreten.")
									.send(player);
						}

					} else {
						new TextBuilder().title("Gilde").text("Du musst eine gültige Zahl angeben.").send(player);
					}
				} else {
					new TextBuilder().title("Gilde").text("Beispiel-Verwendung: /guild desposit 100").send(player);
				}

			} else {
				new TextBuilder().title("Gilde").text("Du bist in keiner Gilde.").send(player);
			}
		}
	}

	@Description(description = "Lädt Spieler zur Gilde ein.")
	@Usage(usage = "<player>")
	class InviteGuildCommand extends PlayerCommand {

		public InviteGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild != null) {

				if (guild.getOwner().contains(player.getUniqueId())
						|| guild.getHelper().contains(player.getUniqueId())) {
					if (ap.hasExactly(1)) {

						ProxiedPlayer target = ProxyServer.getInstance().getPlayer(ap.get(1));

						if (target != null) {

							Guild targetGuild = getGlobalGuildList().getGuildByPlayer(target.getUniqueId());

							if (targetGuild == null) {

								
								//TODO
								if (guild.getMemberSize() >= 10) {
									new TextBuilder().title("Gilde").text("Die Gilde darf nur maximal ").spezial(10).text(" Mitglieder haben.")
									.send(player);
									return; 
								}
								
								
								
								if (guild.getInvites().add(target.getUniqueId())) {

									GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(guild,
											new ComponentBuilder("\n ➤ " + player.getName() + " hat ")
													.color(ChatColor.GRAY).append(target.getName())
													.color(ChatColor.GREEN).append(" eingeladen.\n")
													.color(ChatColor.GRAY).create());

									new TextBuilder().title("Gilde").text(
											"Du wurdest eingeladen in die Gilde " + guild.getName().getShortName())
											.send(target);

									BaseComponent[] request = new ComponentBuilder("\n").append(" ➤ Die Gilde ")
											.color(ChatColor.GRAY).append(guild.getName().getLongName())
											.color(ChatColor.GREEN)
											.append(" hat dich eingeladen.")
											.color(ChatColor.GRAY).append("\n\n      [").color(ChatColor.DARK_GRAY)
											.append("").reset().append(" Beitreten ")
											.event(new ClickEvent(Action.RUN_COMMAND,
													"/guild join " + guild.getName().getLongName()))
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
													new ComponentBuilder("Beitreten").color(ChatColor.GREEN).create()))
											.color(ChatColor.GREEN).append("").reset().append("]   [")
											.color(ChatColor.DARK_GRAY).append(" Ablehnen ")
											.event(new ClickEvent(Action.RUN_COMMAND,
													"/guild decline " + guild.getName().getLongName()))
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
													new ComponentBuilder("Ablehnen").color(ChatColor.RED).create()))
											.color(ChatColor.RED).append("").reset().append("]\n")
											.color(ChatColor.DARK_GRAY).create();

									target.sendMessage(request);

								} else {
									new TextBuilder().title("Gilde").text("Dieser Spieler wurde bereits eingeladen.")
											.send(player);
								}

							} else {
								new TextBuilder().title("Gilde").text("Dieser Spieler ist derzeit in der Gilde "
										+ targetGuild.getName().getLongName() + ".").send(player);
							}

						} else {
							new TextBuilder().title("Gilde").text("Dieser Spieler ist derzeit nicht online.")
									.send(player);
						}
					} else {
						new TextBuilder().title("Gilde").text("Gebe bitte einen Namen an.").send(player);
					}
				} else {
					new TextBuilder().title("Gilde").text("Du musst Owner oder Helfer sein.").send(player);
				}
			} else {
				new TextBuilder().title("Gilde").text("Du bist in keiner Gilde.").send(player);
			}
		}

		@Override
		protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {

			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer) sender;

				Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

				if (guild != null && (guild.getOwner().contains(player.getUniqueId())
						|| guild.getHelper().contains(player.getUniqueId()))) {
					if (ap.hasExactly(1)) {
						String val = ap.get(ap.size()).toLowerCase();
						List<String> sub = ProxyServer.getInstance().getPlayers().stream()
								.filter(p -> getGlobalGuildList().getGuildByPlayer(p.getUniqueId()) == null
										&& !guild.getInvites().contains(p.getUniqueId()))
								.map(ProxiedPlayer::getName).filter(name -> name.toLowerCase().startsWith(val)
										&& !name.equalsIgnoreCase(sender.getName()))
								.collect(Collectors.toList());
						return sub;
					}
				}
			}
			return new ArrayList<>();
		}

	}

	@Description(description = "Tritt einer Gilde bei.")
	@Usage(usage = "<gilde>")
	class JoinGuildCommand extends PlayerCommand {

		public JoinGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild == null) {

				if (ap.hasExactly(1)) {

					Guild join = getGlobalGuildList().getGuildByShortName(ap.get(1));
					if (join == null)
						join = getGlobalGuildList().getGuildByLongName(ap.get(1));

					if (join != null) {
						if (join.getInvites().remove(player.getUniqueId())) {
							join.getMember().add(player.getUniqueId());
							// MESSAGE AN DIE GILDE JOIN TODO
							// GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(join,
							// new TextBuilder().text("Der Spieler
							// ").spezial(player.getName())
							// .text(" ist der Gilde beigetreten.").build());
							// new TextBuilder().title("Gilde").text("Du bist
							// der Gilde beigetreten.").send(player);

							GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(join,
									new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY).append(player.getName())
											.color(ChatColor.GREEN).append(" ist der Gilde beigetreten.\n")
											.color(ChatColor.GRAY).create());

						} else {
							new TextBuilder().title("Gilde").text("Du wurdest in diese Gilde nicht eingeladen!")
									.send(player);
						}
					} else {
						new TextBuilder().title("Gilde").text("Diese Gilde existiert nicht.").send(player);
					}

				} else {
					new TextBuilder().title("Gilde").text("Gebe entweder den Gilden-Kürzel oder den Gildennamen an.")
							.send(player);
				}

			} else {
				new TextBuilder().title("Gilde").text("Du bist bereits in einer Gilde!").send(player);
			}
		}

		@Override
		protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {

			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer) sender;

				Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

				if (guild == null) {
					if (ap.hasExactly(1)) {
						String val = ap.get(ap.size()).toLowerCase();
						Set<String> sub = ProxyServer.getInstance().getPlayers().stream()
								.map(p -> getGlobalGuildList().getGuildByPlayer(p.getUniqueId())).filter(g -> g != null)
								.filter(g -> g.getInvites().contains(player.getUniqueId()))
								.map(g -> g.getName().getShortName()).filter(name -> name.toLowerCase().startsWith(val))
								.collect(Collectors.toSet());
						return new ArrayList<>(sub);
					}
				}
			}
			return new ArrayList<>();
		}

	}

	@Description(description = "Lehnt eine Gilden-Anfrage ab.")
	@Usage(usage = "<gilde>")
	class DeclineGuildCommand extends PlayerCommand {

		public DeclineGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild == null) {

				if (ap.hasExactly(1)) {

					Guild join = getGlobalGuildList().getGuildByShortName(ap.get(1));
					if (join == null)
						join = getGlobalGuildList().getGuildByLongName(ap.get(1));

					if (join != null) {
						if (join.getInvites().remove(player.getUniqueId())) {
							GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(join,
									new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY).append(player.getName())
											.color(ChatColor.GREEN).append(" hat die Anfrage abgelehnt.\n")
											.color(ChatColor.GRAY).create());
							new TextBuilder().title("Gilde").text("Du hast die Anfrage abgelehnt.")
							.send(player);
						} else {
							new TextBuilder().title("Gilde").text("Du wurdest in diese Gilde nicht eingeladen!")
									.send(player);
						}
					} else {
						new TextBuilder().title("Gilde").text("Diese Gilde existiert nicht.").send(player);
					}

				} else {
					new TextBuilder().title("Gilde").text("Gebe entweder den Gilden-Kürzel oder den Gildennamen an.")
							.send(player);
				}

			} else {
				new TextBuilder().title("Gilde").text("Du bist bereits in einer Gilde!").send(player);
			}
		}

		@Override
		protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {

			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer) sender;

				Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

				if (guild == null) {
					if (ap.hasExactly(1)) {
						String val = ap.get(ap.size()).toLowerCase();
						Set<String> sub = ProxyServer.getInstance().getPlayers().stream()
								.map(p -> getGlobalGuildList().getGuildByPlayer(p.getUniqueId())).filter(g -> g != null)
								.filter(g -> g.getInvites().contains(player.getUniqueId()))
								.map(g -> g.getName().getShortName()).filter(name -> name.toLowerCase().startsWith(val))
								.collect(Collectors.toSet());
						return new ArrayList<>(sub);
					}
				}
			}
			return new ArrayList<>();
		}
	}

	@Description(description = "Verlässt eine Gilde.")
	class LeaveGuildCommand extends PlayerCommand {

		public LeaveGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild != null) {

				if (guild.getOwner().contains(player.getUniqueId())) {
					new TextBuilder().title("Gilde")
							.text("Du kannst die Gilde nicht verlassen als Owner. Übergebe zuerst die Besitzerrechte an ein anderes Mitglied.")
							.send(player);
				} else {
					guild.getMember().remove(player.getUniqueId());
					guild.getHelper().remove(player.getUniqueId());
					// MESSAGE AN DIE GILDE JOIN TODO

					GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(guild,
							new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY).append(player.getName())
									.color(ChatColor.GREEN).append(" hat die Gilde verlassen.\n").color(ChatColor.GRAY)
									.create());

					new TextBuilder().title("Gilde").text("Du hast die Gilde verlassen.").send(player);
				}

			} else {
				new TextBuilder().title("Gilde").text("Du bist in keiner Gilde.").send(player);
			}
		}
	}

	@Description(description = "Kickt einen Spieler aus einer Gilde.")
	@Usage(usage = "<player>")
	class KickGuildCommand extends PlayerCommand {

		public KickGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild != null) {

				if (guild.getOwner().contains(player.getUniqueId())) {
					if (ap.hasExactly(1)) {

						ProxiedPlayer target = ProxyServer.getInstance().getPlayer(ap.get(1));

						if (target != null) {

							if (guild.isMemberOfGuild(target.getUniqueId())) {
								if (!guild.getOwner().contains(target.getUniqueId())) {

									if (guild.getMember().remove(target.getUniqueId())
											| guild.getHelper().remove(target.getUniqueId())) {

										GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(guild,
												new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY)
														.append(target.getName()).color(ChatColor.GREEN)
														.append(" wurde aus der Gilde gekickt.\n").color(ChatColor.GRAY)
														.create());

										new TextBuilder().title("Gilde").text("Du wurdest aus der Gilde gekickt.")
												.send(target);

									} else {
										new TextBuilder().title("Gilde")
												.text("Dieser Spieler ist nicht in deiner Gilde!").send(player);
									}

								} else {
									new TextBuilder().title("Gilde").text("Dieser Spieler kann nicht gekickt werden.")
											.send(player);
								}
							} else {
								new TextBuilder().title("Gilde").text("Dieser Spieler ist nicht in deiner Gilde!")
										.send(player);
							}
						} else {
							GlobalPlugin.getInstance().getUuid(uuid -> {

								if (uuid != null && GlobalPlugin.getInstance().getGlobalPlayers().contains(uuid)) {
									if (guild.isMemberOfGuild(uuid)) {
										if (!guild.getOwner().contains(uuid)) {

											if (guild.getMember().remove(uuid) | guild.getHelper().remove(uuid)) {

												// TODO MESSAGE KICK

												GlobalPlugin.getInstance().getModule(GuildGlobalModule.class)
														.sendMessage(guild,
																new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY)
																		.append(ap.get(1)).color(ChatColor.GREEN)
																		.append(" wurde aus der Gilde gekickt.\n")
																		.color(ChatColor.GRAY).create());

												//
												// GlobalPlugin.getInstance().getModule(GuildGlobalModule.class)
												// .sendMessage(guild,
												// new TextBuilder().text("Der
												// Spieler ")
												// .spezial(ap.get(1))
												// .text(" wurde aus der Gilde
												// gekickt.").build());
												// new
												// TextBuilder().title("Gilde")
												// .text("Du hast den Spieler
												// aus der Gilde gekickt.")
												// .send(player);

											} else {
												new TextBuilder().title("Gilde")
														.text("Dieser Spieler ist nicht in deiner Gilde!").send(player);
											}

										} else {
											new TextBuilder().title("Gilde")
													.text("Dieser Spieler kann nicht gekickt werden.").send(player);
										}
									} else {
										new TextBuilder().title("Gilde")
												.text("Dieser Spieler ist nicht in deiner Gilde!").send(player);
									}
								} else {
									new TextBuilder().title("Gilde").text("Dieser Spieler ist uns nicht bekannt.")
											.send(player);
								}

							}, ap.get(1));
						}
					} else {
						new TextBuilder().title("Gilde").text("Gib einen Namen an.").send(player);
					}
				} else {
					new TextBuilder().title("Gilde").text("Du musst Owner sein.").send(player);
				}
			} else {
				new TextBuilder().title("Gilde").text("Du bist in keiner Gilde.").send(player);
			}
		}

		@Override
		protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {

			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer) sender;

				Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

				if (guild != null && guild.getOwner().contains(player.getUniqueId())) {
					if (ap.hasExactly(1)) {
						String val = ap.get(ap.size()).toLowerCase();
						List<String> sub1 = guild.getMember().stream()
								.map(uuid -> GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid))
								.filter(p -> p != null).map(p -> p.name())
								.filter(name -> name != null && name.toLowerCase().startsWith(val)
										&& !name.equalsIgnoreCase(sender.getName()))
								.collect(Collectors.toList());
						List<String> sub2 = guild.getHelper().stream()
								.map(uuid -> GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid))
								.filter(p -> p != null).map(p -> p.name())
								.filter(name -> name != null && name.toLowerCase().startsWith(val)
										&& !name.equalsIgnoreCase(sender.getName()))
								.collect(Collectors.toList());
						sub1.addAll(sub2);
						return sub1;
					}
				}
			}
			return new ArrayList<>();
		}

	}

	@Description(description = "Befördert einen Spieler der Gilde.")
	@Usage(usage = "<player>")
	class PromoteGuildCommand extends PlayerCommand {

		public PromoteGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild != null) {

				if (guild.getOwner().contains(player.getUniqueId())) {
					if (ap.hasExactly(1)) {

						ProxiedPlayer target = ProxyServer.getInstance().getPlayer(ap.get(1));

						if (target != null) {

							if (guild.isMemberOfGuild(target.getUniqueId())) {
								if (guild.getMember().contains(target.getUniqueId())) {

									if (guild.getHelper().add(target.getUniqueId())
											& guild.getMember().remove(target.getUniqueId())) {
										new TextBuilder().title("Gilde").text("Der Spieler wurde befördert.")
												.send(player);
										new TextBuilder().title("Gilde").text("Du wurdest befördert.").send(target);
										// TODO MESSAGE PROMOte
										// GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(guild,
										// new TextBuilder().text("Der Spieler
										// ").spezial(target.getName())
										// .text(" wurde befördert").build());

										GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(guild,
												new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY)
														.append(target.getName()).color(ChatColor.GREEN)
														.append(" wurde befördert.\n").color(ChatColor.GRAY).create());

									} else {
										new TextBuilder().title("Gilde").text("Ein Fehler ist aufgetreten.")
												.send(player);
									}

								} else {
									new TextBuilder().title("Gilde").text("Dieser Spieler kann nicht befördert werden.")
											.send(player);
								}
							} else {
								new TextBuilder().title("Gilde").text("Dieser Spieler ist nicht in deiner Gilde.")
										.send(player);
							}
						} else {
							GlobalPlugin.getInstance().getUuid(uuid -> {

								if (uuid != null && GlobalPlugin.getInstance().getGlobalPlayers().contains(uuid)) {

									if (guild.isMemberOfGuild(uuid)) {
										if (guild.getMember().contains(uuid)) {

											if (guild.getHelper().add(uuid) & guild.getMember().remove(uuid)) {
												new TextBuilder().title("Gilde").text("Der Spieler wurde befördert.")
														.send(player);
												// TODO MESSAGE PROMOte
												// GlobalPlugin.getInstance().getModule(GuildGlobalModule.class)
												// .sendMessage(guild, new
												// TextBuilder().text("Der
												// Spieler ")
												// .spezial(ap.get(1)).text("
												// wurde befördert").build());

												GlobalPlugin.getInstance().getModule(GuildGlobalModule.class)
														.sendMessage(guild,
																new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY)
																		.append(ap.get(1)).color(ChatColor.GREEN)
																		.append(" wurde befördert.\n")
																		.color(ChatColor.GRAY).create());

											} else {
												new TextBuilder().title("Gilde").text("Ein Fehler ist aufgetreten.")
														.send(player);
											}

										} else {
											new TextBuilder().title("Gilde")
													.text("Dieser Spieler kann nicht befördert werden.").send(player);
										}
									} else {
										new TextBuilder().title("Gilde")
												.text("Dieser Spieler ist nicht in deiner Gilde.").send(player);
									}

								} else {
									new TextBuilder().title("Gilde").text("Dieser Spieler ist uns nicht bekannt.")
											.send(player);
								}

							}, ap.get(1));
						}
					} else {
						new TextBuilder().title("Gilde").text("Gib einen Namen an.").send(player);
					}
				} else {
					new TextBuilder().title("Gilde").text("Du musst Owner sein.").send(player);
				}
			} else {
				new TextBuilder().title("Gilde").text("Du bist in keiner Gilde.").send(player);
			}
		}

		@Override
		protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {

			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer) sender;

				Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

				if (guild != null && guild.getOwner().contains(player.getUniqueId())) {
					if (ap.hasExactly(1)) {
						String val = ap.get(ap.size()).toLowerCase();
						List<String> sub = guild.getMember().stream()
								.map(uuid -> GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid))
								.filter(p -> p != null).map(p -> p.name())
								.filter(name -> name != null && name.toLowerCase().startsWith(val)
										&& !name.equalsIgnoreCase(sender.getName()))
								.collect(Collectors.toList());
						return sub;
					}
				}
			}
			return new ArrayList<>();
		}

	}

	@Description(description = "Stuft einen Spieler der Gilde herunter.")
	@Usage(usage = "<player>")
	class DemoteGuildCommand extends PlayerCommand {

		public DemoteGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

			if (guild != null) {

				if (guild.getOwner().contains(player.getUniqueId())) {
					if (ap.hasExactly(1)) {

						ProxiedPlayer target = ProxyServer.getInstance().getPlayer(ap.get(1));

						if (target != null) {

							if (guild.isMemberOfGuild(target.getUniqueId())) {
								if (guild.getHelper().contains(target.getUniqueId())) {

									if (guild.getMember().add(target.getUniqueId())
											& guild.getHelper().remove(target.getUniqueId())) {
										new TextBuilder().title("Gilde").text("Der Spieler wurde heruntergestuft.")
												.send(player);
										new TextBuilder().title("Gilde").text("Du wurdest heruntergestuft.")
												.send(target);
										// TODO MESSAGE PROMOte
										// GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(guild,
										// new TextBuilder().text("Der Spieler
										// ").spezial(target.getName())
										// .text(" wurde degradiert").build());

										GlobalPlugin.getInstance().getModule(GuildGlobalModule.class).sendMessage(guild,
												new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY)
														.append(target.getName()).color(ChatColor.GREEN)
														.append(" wurde degradiert.\n").color(ChatColor.GRAY).create());

									} else {
										new TextBuilder().title("Gilde").text("Ein Fehler ist aufgetreten.")
												.send(player);
									}

								} else {
									new TextBuilder().title("Gilde")
											.text("Dieser Spieler kann nicht heruntergestuft werden.").send(player);
								}
							} else {
								new TextBuilder().title("Gilde").text("Dieser Spieler ist nicht in deiner Gilde.")
										.send(player);
							}
						} else {
							GlobalPlugin.getInstance().getUuid(uuid -> {

								if (uuid != null && GlobalPlugin.getInstance().getGlobalPlayers().contains(uuid)) {

									if (guild.isMemberOfGuild(uuid)) {
										if (guild.getHelper().contains(uuid)) {

											if (guild.getMember().add(uuid) & guild.getHelper().remove(uuid)) {
												new TextBuilder().title("Gilde")
														.text("Der Spieler wurde heruntergestuft.").send(player);
												// TODO MESSAGE PROMOte
												// GlobalPlugin.getInstance().getModule(GuildGlobalModule.class)
												// .sendMessage(guild, new
												// TextBuilder().text("Der
												// Spieler ")
												// .spezial(ap.get(1)).text("
												// wurde degradiert").build());

												GlobalPlugin.getInstance().getModule(GuildGlobalModule.class)
														.sendMessage(guild,
																new ComponentBuilder("\n ➤ ").color(ChatColor.GRAY)
																		.append(ap.get(1)).color(ChatColor.GREEN)
																		.append(" wurde degradiert.\n")
																		.color(ChatColor.GRAY).create());

											} else {
												new TextBuilder().title("Gilde").text("Ein Fehler ist aufgetreten.")
														.send(player);
											}

										} else {
											new TextBuilder().title("Gilde")
													.text("Dieser Spieler kann nicht heruntergestuft werden.")
													.send(player);
										}
									} else {
										new TextBuilder().title("Gilde")
												.text("Dieser Spieler ist nicht in deiner Gilde.").send(player);
									}

								} else {
									new TextBuilder().title("Gilde").text("Dieser Spieler ist uns nicht bekannt.")
											.send(player);
								}

							}, ap.get(1));
						}
					} else {
						new TextBuilder().title("Gilde").text("Gib bitte einen Namen an.").send(player);
					}
				} else {
					new TextBuilder().title("Gilde").text("Du musst Owner sein.").send(player);
				}
			} else {
				new TextBuilder().title("Gilde").text("Du bist in keiner Gilde.").send(player);
			}
		}

		@Override
		protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {

			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer) sender;

				Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

				if (guild != null && guild.getOwner().contains(player.getUniqueId())) {
					if (ap.hasExactly(1)) {
						String val = ap.get(ap.size()).toLowerCase();
						List<String> sub = guild.getHelper().stream()
								.map(uuid -> GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid))
								.filter(p -> p != null).map(p -> p.name())
								.filter(name -> name != null && name.toLowerCase().startsWith(val)
										&& !name.equalsIgnoreCase(sender.getName()))
								.collect(Collectors.toList());
						return sub;
					}
				}
			}
			return new ArrayList<>();
		}

	}

	@Description(description = "Gibt Informationen über das Gilden-Konto an.")
	@Usage(usage = "[gilde]")
	class BalanceGuildCommand extends PlayerCommand {
		public BalanceGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			Guild guild = null;

			if (ap.hasAtLeast(1)) {
				guild = getGlobalGuildList().getGuildByShortName(ap.get(1));
				if (guild == null)
					guild = getGlobalGuildList().getGuildByLongName(ap.get(1));
			} else {
				guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());
			}

			if (guild != null) {
				double balance = GlobalPlugin.getInstance().getEconomy().getWrapper().backward(GlobalPlugin
						.getInstance().getEconomy().getBalance(GuildAccountHandler.class, guild.id(), UUID.class));

				new TextBuilder().title("Gilde").text("Die Gilde ").spezial(guild.getName().getLongName()).text(" [")
						.spezial(guild.getName().getShortName()).text("] hat ").spezial(balance + "$").send(player);

			} else {
				new TextBuilder().title("Gilde").text("Es wurde keine Gilde gefunden.").send(player);
			}
		}

		@Override
		protected List<String> completeTab(CommandSender sender, ArgumentParser ap) {

			if (sender instanceof ProxiedPlayer) {
				ProxiedPlayer player = (ProxiedPlayer) sender;

				Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

				if (guild != null && guild.getOwner().contains(player.getUniqueId())) {
					if (ap.hasExactly(1)) {
						String val = ap.get(ap.size()).toLowerCase();
						Set<String> sub = ProxyServer.getInstance().getPlayers().stream()
								.map(p -> getGlobalGuildList().getGuildByPlayer(p.getUniqueId())).filter(g -> g != null)
								.map(g -> g.getName().getShortName()).filter(name -> name.toLowerCase().startsWith(val))
								.collect(Collectors.toSet());
						return new ArrayList<>(sub);
					}
				}
			}
			return new ArrayList<>();
		}

	}

	@Description(description = "Gibt Informationen über bestimmte Gilde.")
	@Usage(usage = "[gilde/player]")
	class InfoGuildCommand extends PlayerCommand {

		public InfoGuildCommand(String name) {
			super(name);
		}

		@Override
		protected void execute(ProxiedPlayer player, ArgumentParser ap) {

			if (ap.hasAtLeast(1)) {

				Guild guild = getGlobalGuildList().getGuildByShortName(ap.get(1));
				if (guild == null)
					guild = getGlobalGuildList().getGuildByLongName(ap.get(1));
				if (guild == null) {

					GlobalPlugin.getInstance().getUuid(uuid -> {

						if (uuid == null) {
							new TextBuilder().title("Gilde").text("Es wurde keine Gilde gefunden.").send(player);
						} else {
							Guild g = getGlobalGuildList().getGuildByPlayer(uuid);

							if (g == null) {
								new TextBuilder().title("Gilde").text("Der Spieler " + ap.get(1) + " hat keine Gilde.")
										.send(player);
							} else {
								new TextBuilder().title("Gilde").text("Information zur Gilde: ")
										.spezial(g.getName().getLongName()).text(" [")
										.spezial(g.getName().getShortName()).text("]").send(player);

								String owners = "";
								String helpers = "";
								String members = "";

								AtomicBoolean first = new AtomicBoolean(true);
								StringBuilder sb = new StringBuilder();

								g.getOwner().forEach(u -> {
									if (first.get()) {
										first.set(false);
									} else {
										sb.append(", ");
									}
									sb.append(GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(u).name());
								});

								owners = sb.toString();

								first.set(true);
								sb.setLength(0);

								g.getHelper().forEach(u -> {
									if (first.get()) {
										first.set(false);
									} else {
										sb.append(", ");
									}
									sb.append(GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(u).name());
								});

								helpers = sb.toString();

								first.set(true);
								sb.setLength(0);

								g.getMember().forEach(u -> {
									if (first.get()) {
										first.set(false);
									} else {
										sb.append(", ");
									}
									sb.append(GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(u).name());
								});

								members = sb.toString();

								new TextBuilder().text("Besitzer: [" + owners + "]").send(player);
								new TextBuilder().text("Helfer: [" + helpers + "]").send(player);
								new TextBuilder().text("Mitglieder: [" + members + "]").send(player);
							}

						}

					}, ap.get(1));

				} else {
					new TextBuilder().title("Gilde").text("Information zur Gilde: ")
							.spezial(guild.getName().getLongName()).text(" [").spezial(guild.getName().getShortName())
							.text("]").send(player);

					String owners = "";
					String helpers = "";
					String members = "";

					AtomicBoolean first = new AtomicBoolean(true);
					StringBuilder sb = new StringBuilder();

					guild.getOwner().forEach(uuid -> {
						if (first.get()) {
							first.set(false);
						} else {
							sb.append(", ");
						}
						sb.append(GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid).name());
					});

					owners = sb.toString();

					first.set(true);
					sb.setLength(0);

					guild.getHelper().forEach(uuid -> {
						if (first.get()) {
							first.set(false);
						} else {
							sb.append(", ");
						}
						sb.append(GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid).name());
					});

					helpers = sb.toString();

					first.set(true);
					sb.setLength(0);

					guild.getMember().forEach(uuid -> {
						if (first.get()) {
							first.set(false);
						} else {
							sb.append(", ");
						}
						sb.append(GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid).name());
					});

					members = sb.toString();

					new TextBuilder().text("Besitzer: [" + owners + "]").send(player);
					new TextBuilder().text("Helfer: [" + helpers + "]").send(player);
					new TextBuilder().text("Mitglieder: [" + members + "]").send(player);

				}
			} else {

				Guild guild = getGlobalGuildList().getGuildByPlayer(player.getUniqueId());

				if (guild != null) {
					new TextBuilder().title("Gilde").text("Du bist in der Gilde: ")
							.spezial(guild.getName().getLongName()).text(" [").spezial(guild.getName().getShortName())
							.text("]").send(player);

					String owners = "";
					String helpers = "";
					String members = "";

					AtomicBoolean first = new AtomicBoolean(true);
					StringBuilder sb = new StringBuilder();

					guild.getOwner().forEach(uuid -> {
						if (first.get()) {
							first.set(false);
						} else {
							sb.append(", ");
						}
						sb.append(GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid).name());
					});

					owners = sb.toString();

					first.set(true);
					sb.setLength(0);

					guild.getHelper().forEach(uuid -> {
						if (first.get()) {
							first.set(false);
						} else {
							sb.append(", ");
						}
						sb.append(GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid).name());
					});

					helpers = sb.toString();

					first.set(true);
					sb.setLength(0);

					guild.getMember().forEach(uuid -> {
						if (first.get()) {
							first.set(false);
						} else {
							sb.append(", ");
						}
						sb.append(GlobalPlugin.getInstance().getGlobalPlayers().getGlobalPlayer(uuid).name());
					});

					members = sb.toString();

					new TextBuilder().text("Besitzer: [" + owners + "]").send(player);
					new TextBuilder().text("Helfer: [" + helpers + "]").send(player);
					new TextBuilder().text("Mitglieder: [" + members + "]").send(player);

				} else {
					new TextBuilder().title("Gilde").text("Du bist in keiner Gilde.").send(player);
				}
			}
		}
	}

}
