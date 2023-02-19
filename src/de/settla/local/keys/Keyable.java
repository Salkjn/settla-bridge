package de.settla.local.keys;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Keyable extends ConfigurationSerializable, Iterable<KeyItemable> {

    String getId();

    String getName();

    String getPermission();

    void setPermission(String permission);

    boolean hasPermission(Player player);

    List<Location> getRedeemLocations();

    void setRedeemLocations(List<Location> locations);

    List<KeyItemable> getItemList();

    KeyItemable getRandomItem(double luck);

    double getChance(KeyItemable item);

    void setKnockback(double knockback);

    double getKnockback();

    ItemStack getKeyItemStack();

    void update();

    void openKey(Player player);

    boolean overlaps(Location location);

    KeyItemable getRandomItem();
}
