package de.settla.utilities.local.region.space;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.region.Rotatable;
import de.settla.utilities.local.region.Universe;
import de.settla.utilities.local.region.World;
import de.settla.utilities.local.region.form.Form;
import de.settla.utilities.local.region.form.Vector;
import de.settla.utilities.local.region.space.generation.GenerationModule;
import de.settla.utilities.local.region.space.generation.GenerationPaster;
import de.settla.utilities.local.region.space.generation.GenerationRegion;
import de.settla.utilities.local.region.space.selection.FormPaster;

public class Clipboard implements Rotatable {

	private BlockList blocks;
	private Form form;
	private Vector offset;
	private Vector origin;
	private Room room;
	private final Object lock = new Object();

	public Clipboard(BlockList blocks, Room room, Form form, Vector offset, Vector origin) {
		this.blocks = blocks;
		this.form = form;
		this.offset = offset;
		this.origin = origin;
		this.room = room;
	}

	public BlockList getBlocks() {
		return blocks;
	}

	public Form getForm() {
		synchronized (lock) {
			return form;
		}
	}

	public void setForm(Form form) {
		synchronized (lock) {
			this.form = form;
		}
	}

	public Vector getOffset() {
		synchronized (lock) {
			return offset;
		}
	}

	public Vector getOrigin() {
		synchronized (lock) {
			return origin;
		}
	}

	public void setOrigin(Vector origin) {
		synchronized (lock) {
			this.origin = origin;
		}
	}

	public void move(Vector move) {
		synchronized (lock) {
			this.form = form.move(move);
			this.offset = offset.add(move);
			this.origin = origin.add(move);
		}
	}

	public Room getRoom() {
		synchronized (lock) {
			return room;
		}
	}

	@Override
	public Clipboard rotate90(Vector origin) {
		this.form = (Form) form.rotate90(origin);
		this.room = room.rotate90(origin);
		this.offset = ((this.offset.subtract(origin)).rotate90()).add(origin);
		this.origin = ((this.origin.subtract(origin)).rotate90()).add(origin);
		this.blocks.rotate90(origin);
		return this;
	}

	public FormPaster createFormPaster(World world) {
		synchronized (lock) {
			return new FormPaster(form, new Room(room), origin, world, blocks);
		}
	}

	public GenerationPaster createGeneration(String worldname, String blockListId, boolean delete, boolean ignoreAir) {
		synchronized (lock) {
			World world = LocalPlugin.getInstance().getModule(Universe.class).getGalaxy(GenerationModule.GALAXY_NAME)
					.getOrCreateWorld(worldname, GenerationModule.WILDNESS);
			GenerationRegion region = new GenerationRegion(form, new Room(room), origin, world, blocks, blockListId,
					delete, ignoreAir);
			return region.getGenerationPaster();
		}
	}

}
