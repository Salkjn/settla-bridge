package de.settla.utilities.local.region.space;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.RandomStringUtils;

import com.google.gson.Gson;

import de.settla.local.LocalPlugin;
import de.settla.utilities.local.Library;
import de.settla.utilities.local.region.Normal;
import de.settla.utilities.local.region.Rotatable;
import de.settla.utilities.local.region.space.jnbt.ByteArrayTag;
import de.settla.utilities.local.region.space.jnbt.CompoundTag;
import de.settla.utilities.local.region.space.jnbt.NBTInputStream;
import de.settla.utilities.local.region.space.jnbt.NBTOutputStream;
import de.settla.utilities.local.region.space.jnbt.NamedTag;
import de.settla.utilities.local.region.space.jnbt.StringTag;
import de.settla.utilities.local.region.space.jnbt.Tag;
import de.settla.utilities.storage.Storable;

public abstract class BlockList implements Storable, Rotatable {
	
	private final int size;
	
	public BlockList(int size) {
		this.size = size;
	}
	
	public abstract short[] get(int index);
	public abstract BlockList duplicate();
	
	public int size() {
		return size;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = Storable.super.serialize();
		map.put("size", size());
		return map;
	}
	
	public byte[][] bytes() {
		
		byte[] blocks = new byte[size()];
		byte[] addBlocks = null;
		byte[] blockData = new byte[size()];

		for (int index = 0; index < this.size(); index++) {
			
			short[] block = this.get(index);

			if (block == null) {
				blocks[index] = (byte) 0;
				blockData[index] = (byte) 1;
				continue;
			}

			if (block[0] > 255) {
				if (addBlocks == null) { // Lazily create section
					addBlocks = new byte[(blocks.length >> 1) + 1];
				}

				addBlocks[index >> 1] = (byte) (((index & 1) == 0)
						? addBlocks[index >> 1] & 0xF0 | (block[0] >> 8) & 0xF
						: addBlocks[index >> 1] & 0xF | ((block[0] >> 8) & 0xF) << 4);
			}

			blocks[index] = (byte) block[0];
			blockData[index] = (byte) block[1];

		}
		return new byte[][]{blocks, blockData, addBlocks};
	}
	
	private final static Library<BlockList> BLOCK_CACHE = new Library<>();
	
	@SuppressWarnings("unchecked")
	public static BlockList loadBlockList(String id) throws FileNotFoundException, DataException {
		
		if(BLOCK_CACHE.contains(id)) {
			return BLOCK_CACHE.get(id);
		}
		
		InputStream stream = new FileInputStream(file(id));
		NBTInputStream nbtStream;
		try {
			nbtStream = new NBTInputStream(new GZIPInputStream(stream));
			NamedTag rootTag = nbtStream.readNamedTag();
			nbtStream.close();
			if (!rootTag.getName().equals("RegionTemplate")) {
				throw new DataException("Tag \"RegionTemplate\" does not exist or is not first");
			}

			CompoundTag templateTag = (CompoundTag) rootTag.getTag();

			Map<String, Tag> template = templateTag.getValue();
			if (!template.containsKey("blocks")) {
				throw new DataException("Schematic file is missing a \"Blocks\" tag");
			}
			String blocklist = getChildTag(template, "blocklist", StringTag.class).getValue();
			byte[] blockId = getChildTag(template, "blocks", ByteArrayTag.class).getValue();
			byte[] blockData = getChildTag(template, "data", ByteArrayTag.class).getValue();
			byte[] addId = new byte[0];
			short[][] blocks = new short[blockId.length][2]; // Have to later combine IDs

			if (template.containsKey("addblocks")) {
				addId = getChildTag(template, "addblocks", ByteArrayTag.class).getValue();
			}

			for (int index = 0; index < blockId.length; index++) {
				
				short type = 0;
				byte dat = blockData[index];
				
				if ((index >> 1) >= addId.length) { // No corresponding AddBlocks
					type = (short) (blockId[index] & 0xFF);
				} else {
					if ((index & 1) == 0) {
						type = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
					} else {
						type = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
					}
				}
				// type:0 data:1 is for the form system...
				blocks[index] = (type == 0 && dat == 1) ? null : new short[]{type, dat};
			}	
			Map<String, Object> map = new Gson().fromJson(blocklist, Map.class);
			map.put("blocks", blocks);
			BlockList list = Memory.deserialize(map, BlockList.class);
			BLOCK_CACHE.put(id, list);
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public static void saveBlockList(String id, BlockList blocks) {
		
		if(BLOCK_CACHE.contains(id)) {
			BLOCK_CACHE.remove(id);
		}
		
		HashMap<String, Tag> template = new HashMap<String, Tag>();

		template.put("blocklist", new StringTag(new Gson().toJson(blocks.serialize())));
		
		byte[][] transform = blocks.bytes();

		template.put("blocks", new ByteArrayTag(transform[0]));
		template.put("data", new ByteArrayTag(transform[1]));
		
		if(transform.length >= 2) {
			if (transform[2] != null) {
				template.put("addblocks", new ByteArrayTag(transform[2]));
			}
		}

		CompoundTag templateTag = new CompoundTag(template);
		
		NBTOutputStream stream;
		
		try {
			stream = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(file(id))));
			stream.writeNamedTag("RegionTemplate", templateTag);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteBlockList(String id) {
		if(BLOCK_CACHE.contains(id)) {
			BLOCK_CACHE.remove(id);
		}
		File file = file(id);
		if(file.exists())
			file.delete();
	}
	
	public static boolean existsBlockList(String id) {
		File file = file(id);
		return file.exists();
	}
	
	public static String generateId(int lenght) {
		File root = new File(LocalPlugin.getInstance().getModule(TemplateModule.class).getDataFolder(), "/blocks");
		String[] files = root.list();
		boolean ready;
		String id = null;
		do {
			ready = true;
			id = Normal.normalize(RandomStringUtils.randomAlphabetic(lenght));
			if(files == null)
				return id;
			for (String name : files) {
				if(id.equalsIgnoreCase(name))
					ready = false;
			}
		} while(!ready);
		return id;
	}
	
	private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected)
			throws IllegalArgumentException {
		if (!items.containsKey(key)) {
			throw new IllegalArgumentException("Template file is missing a \"" + key + "\" tag");
		}
		
		
		Tag tag = items.get(key);
		if (!expected.isInstance(tag)) {
			throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
		}
		return expected.cast(tag);
	}
	
	private static File file(String id) {
		File file = new File(LocalPlugin.getInstance().getModule(TemplateModule.class).getDataFolder(), "/blocks/"+id+".blocklist");
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}
	
}