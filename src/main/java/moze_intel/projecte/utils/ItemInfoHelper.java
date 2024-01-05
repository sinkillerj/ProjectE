package moze_intel.projecte.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

public class ItemInfoHelper {

	/**
	 * Based on {@link net.minecraft.world.item.enchantment.EnchantmentHelper#getEnchantments(ItemStack)} except calculates it from an ItemInfo with a bit of extra error
	 * catching
	 */
	public static Map<Enchantment, Integer> getEnchantments(ItemInfo info) {
		CompoundTag tag = info.getNBT();
		if (tag == null) {
			return Collections.emptyMap();
		}
		String location = getEnchantTagLocation(info);
		if (!tag.contains(location, Tag.TAG_LIST)) {
			return Collections.emptyMap();
		}
		Map<Enchantment, Integer> map = new LinkedHashMap<>();
		ListTag enchantments = tag.getList(location, Tag.TAG_COMPOUND);
		for (int i = 0; i < enchantments.size(); i++) {
			CompoundTag enchantNBT = enchantments.getCompound(i);
			ResourceLocation enchantmentID = ResourceLocation.tryParse(enchantNBT.getString("id"));
			if (enchantmentID != null) {
				Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(enchantmentID);
				if (enchantment != null) {
					map.put(enchantment, enchantNBT.getInt("lvl"));
				}
			}
		}
		return map;
	}

	public static String getEnchantTagLocation(@NotNull ItemInfo info) {
		return info.getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments";
	}

	/**
	 * Based on {@link net.minecraft.world.item.alchemy.PotionUtils#setPotion(ItemStack, Potion)} except without requiring boxing ItemInfo into an out of an ItemStack
	 */
	public static ItemInfo makeWithPotion(ItemInfo info, Potion potion) {
		CompoundTag nbt = info.getNBT();
		if (potion == Potions.EMPTY) {
			if (nbt != null && nbt.contains("Potion")) {
				nbt.remove("Potion");
				if (nbt.isEmpty()) {
					nbt = null;
				}
			}
		} else {
			if (nbt == null) {
				nbt = new CompoundTag();
			}
			nbt.putString("Potion", BuiltInRegistries.POTION.getKey(potion).toString());
		}
		return ItemInfo.fromItem(info.getItem(), nbt);
	}
}