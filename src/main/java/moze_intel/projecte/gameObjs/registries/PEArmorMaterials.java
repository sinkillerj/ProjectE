package moze_intel.projecte.gameObjs.registries;

import java.util.Map;
import moze_intel.projecte.PECore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class PEArmorMaterials {

	private PEArmorMaterials() {
	}

	private static Map<ArmorType, Integer> diamondDefenses() {
		return Map.of(
				ArmorType.BOOTS, 3,
				ArmorType.LEGGINGS, 6,
				ArmorType.CHESTPLATE, 8,
				ArmorType.HELMET, 3,
				ArmorType.BODY, 11
		);
	}

	private static final TagKey<Item> NO_REPAIR = TagKey.create(Registries.ITEM, PECore.rl("no_repair"));

	private static ResourceKey<EquipmentAsset> asset(String path) {
		return ResourceKey.create(EquipmentAssets.ROOT_ID, PECore.rl(path));
	}

	public static final ArmorMaterial DARK_MATTER = new ArmorMaterial(
			37, diamondDefenses(), 1, SoundEvents.ARMOR_EQUIP_NETHERITE,
			2.0F, 0.1F, NO_REPAIR, asset("dark_matter")
	);
	public static final ArmorMaterial RED_MATTER = new ArmorMaterial(
			37, diamondDefenses(), 1, SoundEvents.ARMOR_EQUIP_NETHERITE,
			2.0F, 0.2F, NO_REPAIR, asset("red_matter")
	);
	public static final ArmorMaterial GEM_ARMOR = new ArmorMaterial(
			37, diamondDefenses(), 1, SoundEvents.ARMOR_EQUIP_NETHERITE,
			2.0F, 0.25F, NO_REPAIR, asset("gem_armor")
	);
}
