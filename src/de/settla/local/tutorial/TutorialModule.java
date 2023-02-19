package de.settla.local.tutorial;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import de.settla.local.LocalPlugin;
import de.settla.local.npc.NpcModel;
import de.settla.local.npc.NpcModule;
import de.settla.utilities.ChangeTracked;
import de.settla.utilities.module.Module;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

public class TutorialModule extends Module<LocalPlugin> implements ChangeTracked {

	private final Object lock = new Object();
	private boolean dirty;
	private final List<TutorialBook> tutorialBooks = new ArrayList<TutorialBook>();

	public TutorialModule(LocalPlugin moduleManager) {
		super(moduleManager);
	}
	
	@Override
	public void onPreEnable() {
		ConfigurationSerialization.registerClass(TutorialBook.class);
	}

	@Override
	public void onEnable() {
		
		NpcModule npcModel = LocalPlugin.getInstance().getModule(NpcModule.class);
		
		npcModel.addModel(new NpcModel("keys", 
				p -> {
					LocalPlugin.getInstance().getModule(TutorialModule.class).consumeTutorials(c -> {
						TutorialBook book = c.stream().filter(tutorial -> tutorial.getName().equalsIgnoreCase("key"))
								.findFirst().orElse(null);
						if (book != null) {
							LocalPlugin.getInstance().getModule(TutorialModule.class).openBook(p, book.getTitle(),
									book.getAuthor(), book.getPages());
						}
					});
				}, 
				p -> {},
				p -> "§e✪§f§lKeys§e✪", 
				null, 
				p -> "§e➤ §f§lLöse hier deine gesammelten §eKeys§f ein!",
				null));
		
		npcModel.addModel(new NpcModel("rang_vip", 
				p -> {
					LocalPlugin.getInstance().getModule(TutorialModule.class).consumeTutorials(c -> {
						TutorialBook book = c.stream().filter(tutorial -> tutorial.getName().equalsIgnoreCase("vip"))
								.findFirst().orElse(null);
						if (book != null) {
							LocalPlugin.getInstance().getModule(TutorialModule.class).openBook(p, book.getTitle(),
									book.getAuthor(), book.getPages());
						}
					});
				}, 
				p -> {},
				
				p -> "§e✪§f§lRang Premium§e✪", 
				null, 
				p -> "§e➤ §f§l10 §fHome-Punkte setzen", 
				p -> "§e➤ §fFarbig schreiben + Prefix im Chat",
				p -> "§e➤ §fAuf vollen Server joinen",
				p -> "§e➤ §fZusätzliche Befehle:",
				p -> "§f/nick, /fly, /god, /craft, /workbench",
				p -> "§f/ec, /repair, /skull, /hat, /time",
				p -> "§f/sun, /day, /night",
				null,
				p -> "§e§lMonatlich: 10€",
				p -> "§e§lLifetime: 50€",
				null));
		
		new BukkitRunnable() {

			@Override
			public void run() {
				getModuleManager().registerCommand(new TutorialCommand("tutorial"));
				getModuleManager().registerCommand(new TutorialReload("tutorialreload"));
				initConfig();
			}
		}.runTaskAsynchronously(getModuleManager());

	}

	@Override
	public void onDisable() {
		if (isDirty()) {
			YamlConfiguration conf = getModuleManager().loadConfig("tutorials.yml");
			conf.set("tutorials", tutorialBooks);
			getModuleManager().saveConfig(conf, "tutorials.yml");
		}
	}

	public void initConfig() {

		tutorialBooks.clear();
		YamlConfiguration conf = getModuleManager().loadConfig("tutorials.yml");
		if (conf.isSet("tutorials")) {
			conf.getList("tutorials").stream().filter(a -> a instanceof TutorialBook)
					.forEach(tutorialBook -> tutorialBooks.add((TutorialBook) tutorialBook));
			setDirty(false);
		} else {
			List<String> pages = new ArrayList<String>();
			ComponentBuilder cb = new ComponentBuilder("Kopfgeld").color(ChatColor.BLACK).bold(true).append("\n\n")
					.bold(false).append("Man kann auf jeden Spieler").color(ChatColor.GRAY).append("mit dem Command ")
					.color(ChatColor.GRAY).append("/kopfgeld set <spieler> <price>").color(ChatColor.RED)
					.append("Kopfgeld setzen.").color(ChatColor.GRAY);
			pages.add(ComponentSerializer.toString(cb.create()));
			tutorialBooks.add(new TutorialBook(pages, "Riderstorm", "Leben und Tod", "test"));
			setDirty(true);
		}

	}

	public void openBook(Player player, String title, String author, List<String> pages) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.setTitle(title);
		meta.setAuthor(author);
		BookUtil.setPages(meta, pages);
		book.setItemMeta(meta);
		BookUtil.openBook(book, player);
	}

	public void consumeTutorials(Consumer<List<TutorialBook>> consumer) {
		synchronized (lock) {
			consumer.accept(this.tutorialBooks);
		}
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
