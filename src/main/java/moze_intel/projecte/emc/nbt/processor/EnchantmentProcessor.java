package moze_intel.projecte.emc.nbt.processor;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.nbt.INBTProcessor;
import moze_intel.projecte.api.nbt.NBTProcessor;
import moze_intel.projecte.utils.ItemInfoHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants.NBT;

@NBTProcessor
public class EnchantmentProcessor implements INBTProcessor {

	private static final long ENCH_EMC_BONUS = 5_000;

	@Override
	public String getName() {
		return "EnchantmentProcessor";
	}

	@Override
	public String getDescription() {
		return "Increases the EMC value to take into account any enchantments on an item.";
	}

	@Override
	public boolean hasPersistentNBT() {
		return true;
	}

	@Override
	public long recalculateEMC(@Nonnull ItemInfo info, long currentEMC) throws ArithmeticException {
		Map<Enchantment, Integer> enchants = ItemInfoHelper.getEnchantments(info);
		for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
			int rarityWeight = entry.getKey().getRarity().getWeight();
			if (rarityWeight > 0) {
				currentEMC = Math.addExact(currentEMC, Math.multiplyExact(ENCH_EMC_BONUS / rarityWeight, entry.getValue()));
			}
		}
		return currentEMC;
	}

	@Nullable
	@Override
	public CompoundNBT getPersistentNBT(@Nonnull ItemInfo info) {
		CompoundNBT tag = info.getNBT();
		if (tag == null) {
			return null;
		}
		String location = ItemInfoHelper.getEnchantTagLocation(info);
		if (!tag.contains(location, NBT.TAG_LIST)) {
			return null;
		}
		CompoundNBT toReturn = new CompoundNBT();
		ListNBT enchantments = tag.getList(location, NBT.TAG_COMPOUND);
		//Note: We don't bother verifying all the entries in the tag are valid as that seems like it is probably unnecessary processing
		toReturn.put(location, enchantments);
		return toReturn;
	}
}