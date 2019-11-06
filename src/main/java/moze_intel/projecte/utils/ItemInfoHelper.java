package moze_intel.projecte.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInfoHelper {

	/**
	 * Based on {@link net.minecraft.enchantment.EnchantmentHelper#getEnchantments(ItemStack)} except calculates it from an ItemInfo with a bit of extra error catching
	 */
	public static Map<Enchantment, Integer> getEnchantments(ItemInfo info) {
		CompoundNBT tag = info.getNBT();
		if (tag == null) {
			return Collections.emptyMap();
		}
		String location = getEnchantTagLocation(info);
		if (!tag.contains(location, NBT.TAG_LIST)) {
			return Collections.emptyMap();
		}
		Map<Enchantment, Integer> map = new LinkedHashMap<>();
		ListNBT enchantments = tag.getList(location, NBT.TAG_COMPOUND);
		for (int i = 0; i < enchantments.size(); i++) {
			CompoundNBT enchantNBT = enchantments.getCompound(i);
			ResourceLocation enchantmentID = ResourceLocation.tryCreate(enchantNBT.getString("id"));
			if (enchantmentID != null) {
				Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentID);
				if (enchantment != null) {
					map.put(enchantment, enchantNBT.getInt("lvl"));
				}
			}
		}
		return map;
	}

	public static String getEnchantTagLocation(@Nonnull ItemInfo info) {
		return info.getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments";
	}

	/**
	 * Based on {@link net.minecraft.potion.PotionUtils#addPotionToItemStack(ItemStack, Potion)} except without requiring boxing ItemInfo into an out of an ItemStack
	 */
	public static ItemInfo makeWithPotion(ItemInfo info, Potion potion) {
		CompoundNBT nbt = info.getNBT();
		if (potion == Potions.EMPTY) {
			if (nbt != null && nbt.contains("Potion")) {
				nbt.remove("Potion");
				if (nbt.isEmpty()) {
					nbt = null;
				}
			}
		} else {
			if (nbt == null) {
				nbt = new CompoundNBT();
			}
			nbt.putString("Potion", potion.getRegistryName().toString());
		}
		return ItemInfo.fromItem(info.getItem(), nbt);
	}
}