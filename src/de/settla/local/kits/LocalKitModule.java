package de.settla.local.kits;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.settla.global.kits.KitType;
import de.settla.local.LocalPlugin;
import de.settla.local.npc.NpcModule;
import de.settla.utilities.local.ChatConvention;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.guis.GuiModule;
import de.settla.utilities.module.Module;
import de.settla.utilities.sakko.protocol.SakkoProtocol;

public class LocalKitModule extends Module<LocalPlugin> {

	private final SakkoProtocol protocol;
	private final KitList kits;

	public LocalKitModule(LocalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
		this.kits = new KitList();
	}

	public KitList getKits() {
		return kits;
	}

	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}

	@Override
	public void onEnable() {
		initGuis();
		registerModels();
		initAnswers();
	}

	private void modelUsage(Kit kit, Player player) {
		if (kit.getMeta().getPermission() == null || player.hasPermission(kit.getMeta().getPermission())) {
			useKit(kit, player);
		} else {
			Utils.sendActionbarMessage(player, ChatColor.GRAY + "Du kannst das Kit "
					+ ChatConvention.spezial(kit.getMeta().getType().getPretty()) + " nicht nutzen.");
		}
	}

    private void initGuis() {
    	
    	getModuleManager().loadResource("kit_items.json", "guis/kit_items.json");
    	getModuleManager().loadResource("kit_inventories.json", "guis/kit_inventories.json");
    	
        File dir = new File(getModuleManager().getDataFolder(), "guis/");
        File[] files = dir.listFiles();
        for(File f : files) {
            if(f.isHidden())
                continue;
            try {
            	LocalPlugin.getInstance().getModule(GuiModule.class).getParser().read(f);
            } catch(Exception e){
                System.err.println("Could not read recipe file " + f.getName());
            }
        }
    }
	
	private void registerModel(NpcModule npcModel, KitType type) {
		Kit kit = getKits().getKit(type);
		npcModel.addModel(new KitNpcModel(kit, kit.getMeta().getNpc(), k -> p -> modelUsage(k, p),
				k -> p -> {
					new KitsShowGui(LocalPlugin.getInstance().getModule(GuiModule.class).getGuis(), p, k).main().open();
				},
				k -> p -> (k.getMeta().getPermission() == null || p.hasPermission(k.getMeta().getPermission())) ? null : "TEST",
				k -> null,
				k -> p -> "§e§lKit: §f§l" + k.getMeta().getType().getPretty(), k -> null,
				k -> p -> "§7" + Utils.timeToFancyString(k.getMeta().getDifTime()) + " verfügbar", k -> p -> {
					LocalKitData kitData = getModuleManager().getLocalPlayers().getLocalPlayer(p)
							.getData(LocalKitData.class);
					if (!kitData.hasValidTimeOfKit(k.getMeta().getType())) {
						updateKitTime(k.getMeta().getType().getName(), p.getUniqueId());
						return "§cLoading...";
					} else {
						
						if (k.getMeta().getPermission() == null || p.hasPermission(k.getMeta().getPermission())) {
							String time = Utils.timeToString(1000 + kitData.getDifTime(k.getMeta().getType()));
							boolean ready = kitData.canUseKit(k.getMeta().getType());
							return ready ? ("§a§lREADY") : ("§c§l" + time);
						} else {
							return "§cKeine Berechtigung";
						}
					}
				}, k -> null));
	}

	private void registerModels() {
		NpcModule npcModel = LocalPlugin.getInstance().getModule(NpcModule.class);
		for (KitType type : KitType.values()) {
			registerModel(npcModel, type);
		}
	}

	private void initAnswers() {
		getSakkoProtocol().answer("kit_set_time", answer -> {
			UUID player = answer.getQuestion("player", UUID.class);
			String kit = answer.getQuestion("kit", String.class);
			long time = answer.getQuestion("time", Long.class);
			KitType type = KitType.getType(kit);
			getModuleManager().getLocalPlayers().getLocalPlayer(player).getData(LocalKitData.class).setTimeOfKit(type,
					time);
			return answer.empty();
		});
	}

	public void updateKitTime(String kit, UUID player) {
		getSakkoProtocol().ask("kit_update_time",
				question -> question.put("kit", kit, String.class).put("player", player, UUID.class), answer -> {
					long time = answer.getAnswer("time", Long.class);
					KitType type = KitType.getType(kit);
					getModuleManager().getLocalPlayers().getLocalPlayer(player).getData(LocalKitData.class)
							.setTimeOfKit(type, time);
				});
	}

	public void useKit(Kit kit, Player player) {

		final UUID uuid = player.getUniqueId();
		final LocalKitData kitData = getModuleManager().getLocalPlayers().getLocalPlayer(player)
				.getData(LocalKitData.class);

		if (kitData.hasValidTimeOfKit(kit.getMeta().getType())) {
			if (kitData.canUseKit(kit.getMeta().getType())) {

				getSakkoProtocol().ask("kit_use",
						question -> question.put("kit", kit.getMeta().getType().getName(), String.class).put("player",
								player.getUniqueId(), UUID.class),
						answer -> {

							boolean success = answer.getAnswer("success", Boolean.class);
							long reset = answer.getAnswer("reset", Long.class);

							if (success) {
								Player p = Bukkit.getPlayer(uuid);
								if (p != null && p.isOnline()) {
									if (kit.enoughInventorySpace(p)) {
										kitData.useKit(kit.getMeta());
										kit.equipPlayer(p);
										p.sendMessage(ChatConvention.title("Kit") + "Du hast das Kit "
												+ ChatConvention.spezial(kit.getMeta().getType().getPretty())
												+ " erhalten.");
										getSakkoProtocol().ask("kit_success",
												question -> question
														.put("kit", kit.getMeta().getType().getName(), String.class)
														.put("player", player.getUniqueId(), UUID.class),
												a -> {
												});
										return;
									} else {
										p.sendMessage(ChatConvention.title("Kit")
												+ "Du benötigst mehr Platz in deinem Inventar!");
									}
								}
								getSakkoProtocol().ask("kit_use_reset",
										question -> question.put("kit", kit.getMeta().getType().getName(), String.class)
												.put("player", player.getUniqueId(), UUID.class)
												.put("reset", reset, Long.class),
										a -> {
										});
							} else {
								Utils.sendActionbarMessage(player,
										ChatColor.GRAY + "Du kannst das Kit "
												+ ChatConvention.spezial(kit.getMeta().getType().getPretty())
												+ " derzeit nicht verwenden!");
							}
						});
			} else {
				Utils.sendActionbarMessage(player,
						ChatColor.GRAY + "Das Kit " + ChatConvention.spezial(kit.getMeta().getType().getPretty())
								+ " ist in "
								+ ChatConvention
										.spezial(Utils.timeToString(1000 + kitData.getDifTime(kit.getMeta().getType())))
								+ " wieder verfügbar.");
			}
		} else {
			updateKitTime(kit.getMeta().getType().getName(), player.getUniqueId());
			Utils.sendActionbarMessage(player, ChatColor.GRAY + "Das Kit "
					+ ChatConvention.spezial(kit.getMeta().getType().getPretty()) + " wird derzeit heruntergeladen...");
		}
	}

}
