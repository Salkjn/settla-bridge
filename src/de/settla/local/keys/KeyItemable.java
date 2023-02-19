package de.settla.local.keys;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public interface KeyItemable extends ConfigurationSerializable {

    ItemStack getItemStack();

    double getChance();
}
