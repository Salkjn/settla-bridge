package de.settla.utilities.local.region.space.generation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.commands.ArgumentParser;
import de.settla.utilities.local.commands.Command;
import de.settla.utilities.local.commands.Description;
import de.settla.utilities.local.commands.OverviewCommand;
import de.settla.utilities.local.commands.Perm;
import de.settla.utilities.local.commands.PlayerCommand;
import de.settla.utilities.local.commands.Usage;
import de.settla.utilities.local.region.Galaxy;
import de.settla.utilities.local.region.Region;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.local.region.World;
import de.settla.utilities.local.region.form.Vector;

@Perm("generation")
public class GenerationCommand extends OverviewCommand {

	public GenerationCommand() {
		super("generation", "gen");
		addSubCommand(new ListCommand("list"));
		addSubCommand(new InfoCommand("info"));
		addSubCommand(new StopCommand("stop"));
		addSubCommand(new TerminanteCommand("terminate"));
		addSubCommand(new RestartCommand("restart"));
		addSubCommand(new StopAllCommand("stop-all"));
		addSubCommand(new TerminanteAllCommand("terminate-all"));
		addSubCommand(new RestartAllCommand("restart-all"));
	}
	
	@Perm("list")
	@Description(description = "Listet dir alle Generierungen auf.")
	@Usage(usage = "")
	private class ListCommand extends Command {

		public ListCommand(String name, String...aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {
			Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME);
			
			Counter totalCounter = new Counter(0);
			
			galaxy.throughWorlds(world -> {
				Counter counter = new Counter(0);
				world.getRegionIndex().throughRegions(region -> {
					counter.up();
					totalCounter.up();
				});
				sender.sendMessage("-> World "+world.getName()+": "+counter.count());
			});
			
			sender.sendMessage("Sum#"+totalCounter.count());
		}
		
	}
	
	@Perm("info")
	@Description(description = "Gibt dir alle Informationen an.")
	@Usage(usage = "")
	private class InfoCommand extends PlayerCommand {

		public InfoCommand(String name, String...aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME);
			World world = galaxy.getWorld(player.getWorld().getName());
			Location loc = player.getLocation();
			List<GenerationRegion> regions = world.getChunkManager().getRegions(new Vector(loc), GenerationRegion.class);
			String str = Utils.toString(regions, ",", region -> "(" + region.id() + "=" + region.getGenerationPaster().getState() + ")");
			player.sendMessage("Generations: ["+str+"]");
		}
		
	}
	
	@Perm("stop")
	@Description(description = "Stopt die lokale Generierung.")
	@Usage(usage = "")
	private class StopCommand extends PlayerCommand {

		public StopCommand(String name, String...aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME);
			World world = galaxy.getWorld(player.getWorld().getName());
			Location loc = player.getLocation();
			List<GenerationRegion> regions = world.getChunkManager().getRegions(new Vector(loc), GenerationRegion.class);
			regions.forEach(region -> region.getGenerationPaster().stop());
			String str = Utils.toString(regions, ",", region -> "(" + region.id() + "=" + region.getGenerationPaster().getState() + ")");
			player.sendMessage("Stopped-Generations: ["+str+"]");
		}
		
	}
	
	@Perm("restart")
	@Description(description = "Startet die lokale Generierung neu.")
	@Usage(usage = "")
	private class RestartCommand extends PlayerCommand {

		public RestartCommand(String name, String...aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME);
			World world = galaxy.getWorld(player.getWorld().getName());
			Location loc = player.getLocation();
			List<GenerationRegion> regions = world.getChunkManager().getRegions(new Vector(loc), GenerationRegion.class);
			regions.forEach(region -> region.getGenerationPaster().restart());
			String str = Utils.toString(regions, ",", region -> "(" + region.id() + "=" + region.getGenerationPaster().getState() + ")");
			player.sendMessage("Restart-Generations: ["+str+"]");
		}
		
	}
	
	@Perm("terminante")
	@Description(description = "Beendet die lokale Generierung.")
	@Usage(usage = "")
	private class TerminanteCommand extends PlayerCommand {

		public TerminanteCommand(String name, String...aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(Player player, ArgumentParser ap) {
			Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME);
			World world = galaxy.getWorld(player.getWorld().getName());
			Location loc = player.getLocation();
			List<GenerationRegion> regions = world.getChunkManager().getRegions(new Vector(loc), GenerationRegion.class);
			regions.forEach(region -> region.getGenerationPaster().terminate());
			String str = Utils.toString(regions, ",", region -> "(" + region.id() + "=" + region.getGenerationPaster().getState() + ")");
			player.sendMessage("Terminated-Generations: ["+str+"]");
		}
		
	}
	
	@Perm("stop.all")
	@Description(description = "Stopt alle Generierungen.")
	@Usage(usage = "")
	private class StopAllCommand extends Command {

		public StopAllCommand(String name, String...aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {
			Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME);
			List<Region> regions = new ArrayList<>();
			galaxy.throughWorlds(world -> world.getRegionIndex().throughRegions(region -> regions.add(region)));
			List<GenerationRegion> generations = Utils.filter(regions, GenerationRegion.class, (a -> a.getGenerationPaster().getState() == GenerationState.RUNNING));
			generations.forEach(gen -> gen.getGenerationPaster().stop());
			String str = Utils.toString(generations, ",", region -> "(" + region.id() + "=" + region.getGenerationPaster().getState() + ")");
			sender.sendMessage("Stop-Generations: ["+str+"]");
		}
		
	}
	
	@Perm("restart.all")
	@Description(description = "Startet alle Generierungen neu.")
	@Usage(usage = "")
	private class RestartAllCommand extends Command {

		public RestartAllCommand(String name, String...aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {
			Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME);
			List<Region> regions = new ArrayList<>();
			galaxy.throughWorlds(world -> world.getRegionIndex().throughRegions(region -> regions.add(region)));
			List<GenerationRegion> generations = Utils.filter(regions, GenerationRegion.class, (a -> a.getGenerationPaster().getState() == GenerationState.END));
			generations.forEach(gen -> gen.getGenerationPaster().restart());
			String str = Utils.toString(generations, ",", region -> "(" + region.id() + "=" + region.getGenerationPaster().getState() + ")");
			sender.sendMessage("Restart-Generations: ["+str+"]");
		}
		
	}
	
	@Perm("terminate.all")
	@Description(description = "Beendet alle Generierungen.")
	@Usage(usage = "")
	private class TerminanteAllCommand extends Command {

		public TerminanteAllCommand(String name, String...aliases) {
			super(name, aliases);
		}

		@Override
		protected void execute(CommandSender sender, ArgumentParser ap) {
			Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME);
			List<Region> regions = new ArrayList<>();
			galaxy.throughWorlds(world -> world.getRegionIndex().throughRegions(region -> regions.add(region)));
			List<GenerationRegion> generations = Utils.filter(regions, GenerationRegion.class, (a -> a.getGenerationPaster().getState() == GenerationState.RUNNING));
			generations.forEach(gen -> gen.getGenerationPaster().terminate());
			String str = Utils.toString(generations, ",", region -> "(" + region.id() + "=" + region.getGenerationPaster().getState() + ")");
			sender.sendMessage("Terminated-Generations: ["+str+"]");
		}
		
	}

}
