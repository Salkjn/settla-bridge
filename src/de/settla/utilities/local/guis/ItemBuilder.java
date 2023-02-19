package de.settla.utilities.local.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by dennisheckmann on 28.02.17.
 */
public class ItemBuilder {

    private static final KeyValueSplitter DEFAULT_SPLITTER = new KeyValueSplitter();

    private ItemStack item;
    private ItemMeta meta;
    private KeyValueSplitter splitter = DEFAULT_SPLITTER;
    private List<String> lore;

    public ItemBuilder(ItemStack item) {
        this.item = item;
        updateMeta();
        if (meta == null)
            System.out.println("Meta is null wtf");
        if (!meta.hasLore())
            meta.setLore(new ArrayList<>());
        this.lore = meta.getLore();
    }

    public ItemBuilder(Material material) {
        this(material, 1, (short) 0);
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, (short) 0);
    }

    public ItemBuilder(Material material, int amount, short damage) {
        this(new ItemStack(material, amount, damage));
    }

    public ItemBuilder updateMeta() {
        this.meta = item.getItemMeta();
        return this;
    }

    public ItemBuilder setSplitter(KeyValueSplitter splitter) {
        if (splitter != null)
            this.splitter = splitter;
        return this;
    }

    public KeyValueSplitter getSplitter() {
        return this.splitter;
    }

    public ItemBuilder setName(String name) {
        this.meta.setDisplayName(name);
        return this;
    }

    public String getName() {
        return this.meta.getDisplayName();
    }

    public int getAmount() {
        return this.item.getAmount();
    }

    public ItemBuilder setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        this.item.setType(material);
        return this;
    }

    public Material getMaterial() {
        return this.item.getType();
    }

    public List<String> getLore() {
        return this.lore;
    }

    public ItemBuilder addLore(String line) {
        getLore().add(line);
        return this;
    }

    public ItemBuilder addLore(String... lines) {
        getLore().addAll(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder setLore(int index, String line) {
        getLore().set(index, line);
        return this;
    }

    public ItemBuilder setValue(String key, String value) {
        int index = getValueIndex(key);
        String newLine = getSplitter().getLine(key, value);
        if (index > 0) {
            setLore(index, newLine);
        } else {
            addLore(newLine);
        }
        return this;
    }

    public String getValue(String key) {
        int index = getValueIndex(key);
        String value = null;
        if (index > 0) {
            value = splitter.getValue(getLore().get(index));
        }
        return value;
    }

    public ItemBuilder removeValue(String key, String value) {
        int index = getValueIndex(key);
        if (index > 0)
            getLore().remove(index);
        return this;
    }

    private int getValueIndex(String key) {
        List<String> lore = getLore();
        KeyValueSplitter splitter = getSplitter();
        int size = lore.size();
        for (int i = 0; i < size; i++) {
            String line = lore.get(i);
            String splitterKey = splitter.getKey(line);
            if (splitterKey != null && splitterKey.equalsIgnoreCase(key)) {
                return i;
            }
        }
        return -1;
    }

    public ItemBuilder loreAction(Consumer<List<String>> action) {
        action.accept(lore);
        return this;
    }

    public ItemBuilder replaceLore(String toFind, String with) {
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, lore.get(i).replace(toFind, with));
        }
        return this;
    }

    public void setData(short val) {
        item.setDurability(val);
    }

    public ItemBuilder addEnchantment(Enchantment ench, int level) {
        return this.addEnchantment(ench, level, true);
    }

    public ItemBuilder addEnchantment(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        meta.addEnchant(ench, level, ignoreLevelRestriction);
        return this;
    }
    
	public ItemBuilder addEnchantEffect(){
		meta.addEnchant(Enchantment.DURABILITY, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return this;
	}

    public ItemStack build() {
        meta.setLore(getLore());
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack buildCloned() {
        return build().clone();
    }

}
