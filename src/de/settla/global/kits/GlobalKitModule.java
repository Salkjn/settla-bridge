package de.settla.global.kits;

import java.util.UUID;

import de.settla.global.GlobalPlugin;
import de.settla.utilities.module.Module;
import de.settla.utilities.sakko.protocol.SakkoProtocol;

public class GlobalKitModule extends Module<GlobalPlugin> {

	private final SakkoProtocol protocol;
	
	public GlobalKitModule(GlobalPlugin moduleManager, SakkoProtocol protocol) {
		super(moduleManager);
		this.protocol = protocol;
		initAnswers();
	}

	public SakkoProtocol getSakkoProtocol() {
		return protocol;
	}
	
	public void updatePlayerOnServer(String kit, UUID player, long time) {
		getSakkoProtocol().ask("kit_set_time", question -> question.put("player", player, UUID.class).put("kit", kit, String.class).put("time", time, Long.class), answer -> {});
	}
	
	private void initAnswers() {
		
		getSakkoProtocol().answer("kit_update_time", answer -> {
			UUID player = answer.getQuestion("player", UUID.class);
			String kit = answer.getQuestion("kit", String.class);
			GlobalKitData playerData = getModuleManager().getGlobalPlayers().getGlobalPlayer(player).getData(GlobalKitData.class);
			return answer.answer().put("time", playerData.getTimeOfKit(KitType.getType(kit)), Long.class);
		});
		
		getSakkoProtocol().answer("kit_use", answer -> {
			
			String name = answer.getQuestion("kit", String.class);
			UUID player = answer.getQuestion("player", UUID.class);
			
			boolean success = false;
			
			GlobalKitData playerData = getModuleManager().getGlobalPlayers().getGlobalPlayer(player).getData(GlobalKitData.class);
			
			KitMeta meta = KitMeta.getKitMeta(KitType.getType(name));
			
			long reset = playerData.getTimeOfKit(meta.getType());
			
			if (meta != null) {
				if (playerData.canUseKit(meta.getType())) {
					playerData.useKit(meta);
					success = true;
				}
			}
			return answer.answer().put("success", success, Boolean.class).put("reset", reset, Long.class);
		});
		
		getSakkoProtocol().answer("kit_use_reset", answer -> {
			String name = answer.getQuestion("kit", String.class);
			UUID player = answer.getQuestion("player", UUID.class);
			long reset = answer.getQuestion("reset", Long.class);
			GlobalKitData playerData = getModuleManager().getGlobalPlayers().getGlobalPlayer(player).getData(GlobalKitData.class);
			playerData.setTimeOfKit(KitType.getType(name), reset);
			return answer.empty();
		});

		getSakkoProtocol().answer("kit_success", answer -> {
			String name = answer.getQuestion("kit", String.class);
			UUID player = answer.getQuestion("player", UUID.class);
			GlobalKitData playerData = getModuleManager().getGlobalPlayers().getGlobalPlayer(player).getData(GlobalKitData.class);
			long time = playerData.getTimeOfKit(KitType.getType(name));
			updatePlayerOnServer(name, player, time);
			return answer.empty();
		});
		
	}
	
	
}
