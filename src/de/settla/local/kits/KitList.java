package de.settla.local.kits;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import de.settla.global.kits.KitMeta;
import de.settla.global.kits.KitType;
import de.settla.utilities.local.guis.ItemBuilder;

public class KitList {

	private final Map<KitType, Kit> kits = new HashMap<>();
	
	public KitList() {
		registerKits();
	}

	private void addKit(Kit kit) {
		kits.put(kit.getMeta().getType(), kit);
	}
	
	public Kit getKit(KitType type) {
		return kits.get(type);
	}
	
	private void registerKits() {
		
		addKit(new Kit(KitMeta.getKitMeta(KitType.ZOMBIE), new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 1).build()));
		addKit(new Kit(KitMeta.getKitMeta(KitType.ENDERMAN), new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 1).build()));
		addKit(new Kit(KitMeta.getKitMeta(KitType.SKELETON), new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 1).build()));
		addKit(new Kit(KitMeta.getKitMeta(KitType.CREEPER), new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 1).build()));
		
		
		addKit(new Kit(KitMeta.getKitMeta(KitType.KNIGHT), 
				new ItemBuilder(Material.CHAINMAIL_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
				new ItemBuilder(Material.CHAINMAIL_LEGGINGS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
				new ItemBuilder(Material.CHAINMAIL_HELMET).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
				new ItemBuilder(Material.IRON_CHESTPLATE).build(),
				new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 1).addEnchantment(Enchantment.DAMAGE_ALL, 1).build(),
				new ItemBuilder(Material.EXP_BOTTLE, 10).build(),
				new ItemBuilder(Material.COOKED_BEEF, 10).build()
				));
		
		addKit(new Kit(KitMeta.getKitMeta(KitType.FARMER), 
				new ItemBuilder(Material.LEATHER_BOOTS).addEnchantment(Enchantment.PROTECTION_FIRE, 2).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
				new ItemBuilder(Material.LEATHER_CHESTPLATE).addEnchantment(Enchantment.PROTECTION_FIRE, 2).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
				new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.PROTECTION_FIRE, 2).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
				new ItemBuilder(Material.LEATHER_LEGGINGS).addEnchantment(Enchantment.PROTECTION_FIRE, 2).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
				new ItemBuilder(Material.STONE_SWORD).addEnchantment(Enchantment.DURABILITY, 1).addEnchantment(Enchantment.DAMAGE_ALL, 2).build(),
				new ItemBuilder(Material.IRON_HOE, 1).build(),
				new ItemBuilder(Material.WHEAT, 12).build(),
				new ItemBuilder(Material.POTATO_ITEM, 12).build(),
				new ItemBuilder(Material.CARROT_ITEM, 12).build(),
				new ItemBuilder(Material.EXP_BOTTLE, 6).build(),
				new ItemBuilder(Material.COOKED_BEEF, 10).build()
				));
		
		addKit(new Kit(KitMeta.getKitMeta(KitType.ARCHER), 
				new ItemBuilder(Material.CHAINMAIL_BOOTS).addEnchantment(Enchantment.PROTECTION_FALL, 1).addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).build(),
				new ItemBuilder(Material.CHAINMAIL_LEGGINGS).addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).build(),
				new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1).build(),
				new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).build(),
				new ItemBuilder(Material.IRON_AXE).addEnchantment(Enchantment.DURABILITY, 1).addEnchantment(Enchantment.DIG_SPEED, 1).build(),
				new ItemBuilder(Material.EXP_BOTTLE, 7).build(),
				new ItemBuilder(Material.COOKED_CHICKEN, 10).build(),
				new ItemBuilder(Material.BOW).addEnchantment(Enchantment.ARROW_DAMAGE, 1).addEnchantment(Enchantment.ARROW_KNOCKBACK, 1).build(),
				new ItemBuilder(Material.ARROW, 32).build()
				));
		
		addKit(new Kit(KitMeta.getKitMeta(KitType.WITCH), new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 1).build()));
		
		
	}
	
}
