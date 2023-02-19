package de.settla.local.economysigns;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.settla.economy.AccountHandler;
import de.settla.local.LocalPlugin;
import de.settla.utilities.ChangeTracked;
import de.settla.utilities.Tuple;
import de.settla.utilities.module.Module;

public class EconomySignTop<A extends AccountHandler<?, ?>> extends Module<EconomySignTopModule> implements ChangeTracked {

	private final List<EconomySign> signs = new ArrayList<>();
	private final String name;
	private final Class<A> accountHandler;
	private final Object lock = new Object();
	private boolean dirty;
	private final BiFunction<Tuple<String, Double>, EconomySign, String[]> lines; 
	
	private final Function<String, String> nameFunction;
	
	private final BukkitRunnable signUpdater = new BukkitRunnable() {
		@Override
		public void run() {
			if (signs.size() > 0) {
				calculateTop(top -> {
					new BukkitRunnable() {
						@Override
						public void run() {
							consumeSigns(list -> {
								list.forEach(sign -> {
									Block b = sign.getLocation().getBlock();
									if (b != null) {
										if (b.getState() instanceof Sign) {
											updateSkull(b.getLocation(),
													top.size() >= sign.getRank() ? top.get(sign.getRank() - 1).getX() : null);
											Bukkit.getOnlinePlayers().stream()
													.filter(p -> p.getLocation().getWorld().equals(b.getWorld())
															&& p.getLocation().distanceSquared(b.getLocation()) <= 400)
													.forEach(player -> {
														updateSign(b.getLocation(), player,
																top.size() >= sign.getRank() ? top.get(sign.getRank() - 1) : null,
																sign);
													});
										}
									}
								});
							});
						}
					}.runTask(LocalPlugin.getInstance());
				});
			}
		}
	};

	public EconomySignTop(EconomySignTopModule moduleManager, String name, Class<A> accountHandler, BiFunction<Tuple<String, Double>, EconomySign, String[]> lines, Function<String, String> nameFunction) {
		super(moduleManager);
		this.name = name;
		this.nameFunction = nameFunction;
		this.accountHandler = accountHandler;
		this.lines = lines;
	}

	public String getName() {
		return name;
	}

	public Class<A> getAccountHandler() {
		return accountHandler;
	}

	private void updateSign(Location loc, Player p, Tuple<String, Double> tuple, EconomySign sign) {
		String[] lines = this.lines.apply(tuple, sign);
//		HeadHunterSignChangeEvent event = new HeadHunterSignChangeEvent(p, lines, sign);
//		Bukkit.getPluginManager().callEvent(event);
//		if (!event.isCancelled()) {
//			p.sendSignChange(loc, event.getLines());
//		}
		p.sendSignChange(loc, lines);
	}

	private void updateSkull(Location loc, String name) {
		if (name == null)
			return;
		Block b = loc.getBlock().getRelative(BlockFace.UP);
		if (b.getState() instanceof Skull) {
			Skull s = (Skull) b.getState();
			s.setOwner(name);
			s.update(true);
		}
	}

	@Override
	public void onEnable() {
		new BukkitRunnable() {
			@Override
			public void run() {
				initConfig();
				signUpdater.runTaskTimerAsynchronously(getModuleManager().getModuleManager(), 3 * 20L, 10L * 20L);
			}
		}.runTaskAsynchronously(getModuleManager().getModuleManager());

	}

	@Override
	public void onDisable() {
		if (isDirty()) {
			YamlConfiguration conf = getModuleManager().getModuleManager().loadConfig(name+".yml");
			conf.set("signs", signs);
			getModuleManager().getModuleManager().saveConfig(conf,name+".yml");
		}
	}
	
	public void initConfig() {
		YamlConfiguration conf = getModuleManager().getModuleManager().loadConfig(name+".yml");
		if (conf.isSet("signs")) {
			conf.getList("signs").stream().filter(a -> a instanceof EconomySign)
					.forEach(sign -> signs.add((EconomySign) sign));
		}
		setDirty(false);
	}

	public void consumeSigns(Consumer<List<EconomySign>> consumer) {
		synchronized (lock) {
			consumer.accept(this.signs);
		}
	}

	public void calculateTop(Consumer<List<Tuple<String, Double>>> consumer) {
		getModuleManager().getModuleManager().getEconomy().getTop(accountHandler, 10, answer -> {
			consumer.accept(answer.stream()
					.map(t -> new Tuple<>(nameFunction.apply(t.getX()), t.getY()))
					.collect(Collectors.toList()));

		});
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
