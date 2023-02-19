package de.settla.utilities.local.region.space.generation;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.Utils;
import de.settla.utilities.local.region.Galaxy;
import de.settla.utilities.local.region.Region;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.local.region.World;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.space.selection.SelectionCommand;
import de.settla.utilities.module.Module;
import de.settla.utilities.storage.Storable.Memory;

public class GenerationModule extends Module<LocalPlugin> {

	public static final String GALAXY_NAME = "generation_galaxy";
	public static final GenerationWildness WILDNESS = new GenerationWildness();

	public GenerationModule(LocalPlugin plugin) {
		super(plugin);
	}

	private void register() {
		Memory.register(GenerationRegion.class, map -> new GenerationRegion(map));
		Memory.register(GenerationWildness.class, map -> new GenerationWildness(map));
	}

	@Override
	public void onEnable() {
		register();
		getModuleManager().getModule(Universe.class).registerGalaxy(GALAXY_NAME);
		getModuleManager().getModule(Universe.class)
				.addToWaitingActionList(() -> {
					getModuleManager().registerCommand(new GenerationCommand());
					getModuleManager().registerCommand(new SelectionCommand());
				});
	}

	public List<GenerationRegion> getGenerations(String worldName, Form form) {
		Galaxy galaxy = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GALAXY_NAME);
		World world = galaxy.getOrCreateWorld(worldName, WILDNESS);
		EmptyRegion test = new EmptyRegion(form, RandomStringUtils.randomAlphanumeric(20));
		test.getRegionRegistery().register(world);
		List<Region> list = test.getRegionRegistery().getIntersections();
		test.getRegionRegistery().unregister();
		return Utils.filter(list, GenerationRegion.class);
	}

}
