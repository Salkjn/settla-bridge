package de.settla.local.keys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import de.settla.local.LocalPlugin;
import de.settla.local.VaultHelper;
import de.settla.utilities.local.guis.GuiModule;

public class Key implements Keyable {

	private String id;
	private String name;
	private String permission;
	private double knockback;
	private List<Location> locations;
	private List<KeyItemable> items;

	public Key(String id, String name, String permission, double knockback, List<Location> locations,
			List<KeyItemable> items) {
		super();
		this.id = id;
		this.name = name;
		this.permission = permission;
		this.knockback = knockback;
		this.locations = locations;
		this.items = items;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPermission() {
		return permission;
	}

	@Override
	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public boolean hasPermission(Player player) {
		if (getPermission() == null || getPermission().equalsIgnoreCase("")) {
			return true;
		} else {
			return VaultHelper.checkPerm(player, getPermission());
		}
	}

	@Override
	public List<Location> getRedeemLocations() {
		return locations;
	}

	@Override
	public void setRedeemLocations(List<Location> locations) {
		this.locations = locations;
	}

	@Override
	public List<KeyItemable> getItemList() {
		return items;
	}

	@Override
	public KeyItemable getRandomItem(double luck) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < getItemList().size(); i++) {
			KeyItemable item = getItemList().get(i);
			// TODO: 06.03.17 Luck!
			for (int j = 0; j < item.getChance(); j++) {
				list.add(i);
			}
		}
		double random = Math.random();
		int keyItemId = list.get((int) Math.floor((random * (double) list.size())));
		return getItemList().get(keyItemId);
	}

	@Override
	public double getChance(KeyItemable item) {
		double all = 0.0;
		for (KeyItemable i : this) {
			all += i.getChance();
		}
		return item.getChance() / all;
	}

	@Override
	public void setKnockback(double knockback) {
		this.knockback = knockback;
	}

	@Override
	public double getKnockback() {
		return knockback;
	}

	@Override
	public ItemStack getKeyItemStack() {
		return LocalPlugin.getInstance().getModule(GuiModule.class).getParser().getItem(getId()).build().getItemStack();
	}

	@Override
	public void update() {

		double x, y, z = 0;
		double r = Math.random() * 0.5 + 1;
		double phi = Math.random() * 2 * Math.PI;
		double theta = Math.random() * Math.PI;
		x = r * Math.cos(phi) * Math.sin(theta);
		y = r * Math.sin(phi) * Math.sin(theta);
		z = r * Math.cos(theta);
		Vector vec = new Vector(x, y, z);

		for (Location loc : getRedeemLocations()) {
			Location newLoc = loc.clone().add(vec);
			newLoc.add(0.5, 0.5, 0.5);
			newLoc.getWorld().playEffect(newLoc, Effect.MOBSPAWNER_FLAMES, 1, 20);
		}

	}

	@Override
	public void openKey(Player player) {

		new KeyGui(LocalPlugin.getInstance().getModule(GuiModule.class).getGuis(), player, this).main().open();
	}

	@Override
	public boolean overlaps(Location location) {
		for (Location loc : getRedeemLocations()) {
			if (loc.getWorld().getName().equalsIgnoreCase(location.getWorld().getName())
					&& loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY()
					&& loc.getBlockZ() == location.getBlockZ()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public KeyItemable getRandomItem() {
		return getRandomItem(1);
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", getId());
		map.put("name", getName());
		map.put("permission", getPermission());
		map.put("knockback", getKnockback());
		map.put("locations", getRedeemLocations());
		map.put("items", getItemList());
		return map;
	}

	@SuppressWarnings("unchecked")
	public static Key deserialize(Map<String, Object> args) {
		return new Key((String) args.get("id"), (String) args.get("name"), (String) args.get("permission"),
				(Double) args.get("knockback"), (List<Location>) args.get("locations"),
				(List<KeyItemable>) args.get("items"));
	}

	@Override
	public Iterator<KeyItemable> iterator() {
		return getItemList().iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Key that = (Key) o;

		if (Double.compare(that.knockback, knockback) != 0)
			return false;
		if (id != null ? !id.equals(that.id) : that.id != null)
			return false;
		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;
		return permission != null ? permission.equals(that.permission) : that.permission == null;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (permission != null ? permission.hashCode() : 0);
		temp = Double.doubleToLongBits(knockback);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

}
