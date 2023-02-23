package de.settla.spigot.universe;

import java.io.File;
import java.nio.file.Files;

import de.settla.local.LocalPlugin;
import de.settla.spigot.universe.form.Form;
import de.settla.memory.MemoryException;
import de.settla.memory.MemoryName;
import de.settla.memory.MemoryStorable;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.World;

@MemoryName("Schematic")
public class Schematic implements MemoryStorable<Schematic> {

    public enum Direction {
        
    	NONE(0), NORTH(0), EAST(90), SOUTH(180), WEST(270);
    	
    	private final int angle;
    	private Direction(int angle) {
    		this.angle = angle;
    	}
        
    }
    
    private final static String SCHEMATIC_SUFFIX = ".schematic";

    private final Form form;
    private final Direction direction;
    private final String destination;

    public Schematic(Form form, Direction direction, String destination) {
        this.form = form;
    	this.direction = direction;
        this.destination = destination;
    }

    public Schematic(JsonObject json) {
        this.form = deserialize(json.get("form").getAsJsonObject(), Form.class);
        this.direction = Direction.valueOf(json.get("direction").getAsString());
        this.destination = json.get("destination").getAsString();
    }
    
    @Override
    public JsonObject serialize() throws MemoryException {
        JsonObject json = MemoryStorable.super.serialize();
        json.add("form", form.serialize());
        json.addProperty("direction", direction.name());
        json.addProperty("destination", destination);
        return json;
    }

    public File getDestinationFile() {
        return new File(LocalPlugin.getInstance().getDataFolder(), destination + SCHEMATIC_SUFFIX);
    }

    public void save(org.bukkit.World w) {
        World world = new BukkitWorld(w);
        Vector fmin = form.minimum();
        Vector fmax = form.maximum();

        BlockVector3 min = BlockVector3.at(fmin.getBlockX(), fmin.getBlockY(), fmin.getBlockZ());
        BlockVector3 max = BlockVector3.at(fmax.getBlockX(), fmax.getBlockY(), fmax.getBlockZ());
        CuboidRegion region = new CuboidRegion(world, min, max);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        try {
            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
                        region.getMinimumPoint());
                // configure here
                forwardExtentCopy.setCopyingEntities(true);
                Operations.complete(forwardExtentCopy);
            }

            File file = getDestinationFile();

            try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC
                    .getWriter(Files.newOutputStream(file.toPath()))) {
                writer.write(clipboard);
            }
        } catch (Exception e) {

        }
    }

    public void paste(org.bukkit.World w, Form form, Direction direction) {

    	Vector fmin = form.minimum();
        BlockVector3 min = BlockVector3.at(fmin.getBlockX(), fmin.getBlockY(), fmin.getBlockZ());
    	
        World world = new BukkitWorld(w);
        File file = getDestinationFile();

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try {
            assert format != null;
            try (ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()))) {
                Clipboard clipboard = reader.read();

                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {

                    ClipboardHolder holder = new ClipboardHolder(clipboard);

                    holder.setTransform(new AffineTransform().rotateY(direction.angle));

                    PasteBuilder builder = holder.createPaste(editSession);

                    builder.to(BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ()));
                    builder.ignoreAirBlocks(false);

                    builder.maskSource(new Mask() {
                        @Override
                        public Mask2D toMask2D() {
                            return null;
                        }

                        @Override
                        public boolean test(BlockVector3 vector) {
                            return form.overlaps(new Vector(vector.getX(), vector.getY(), vector.getZ()));
                        }
                    });

                    Operation operation = builder.build();

                    Operations.complete(operation);
                }

            }
        } catch (Exception e) {

        }

    }

}