package moze_intel.projecte.emc.nbt.processor;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.nbt.INBTProcessor;
import moze_intel.projecte.api.nbt.NBTProcessor;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NBTProcessor
public class DecoratedPotProcessor implements INBTProcessor {

	@Override
	public String getName() {
		return "DecoratedPotProcessor";
	}

	@Override
	public String getDescription() {
		return "Takes the different sherds into account for each decorated pot.";
	}

	@Override
	public boolean hasPersistentNBT() {
		return true;
	}

	@Override
	public long recalculateEMC(@NotNull ItemInfo info, long currentEMC) throws ArithmeticException {
		if (info.getItem() == Items.DECORATED_POT) {
			CompoundTag tag = info.getNBT();
			if (tag != null && tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
				CompoundTag beTag = tag.getCompound("BlockEntityTag");
				DecoratedPotBlockEntity.Decorations decorations = DecoratedPotBlockEntity.Decorations.load(beTag);
				if (!decorations.equals(DecoratedPotBlockEntity.Decorations.EMPTY)) {
					long decorationEmc = decorations.sorted()
							.mapToLong(EMCHelper::getEmcValue)
							//Like: sum but using addExact instead
							.reduce(0, Math::addExact);
					//Calculate base decorated pot (four bricks) emc to subtract from our current values
					return Math.addExact(currentEMC - EMCHelper.getEmcValue(Items.DECORATED_POT), decorationEmc);
				}
			}
		}
		return currentEMC;
	}

	@Nullable
	@Override
	public CompoundTag getPersistentNBT(@NotNull ItemInfo info) {
		if (info.getItem() == Items.DECORATED_POT) {
			CompoundTag tag = info.getNBT();
			if (tag != null && tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
				CompoundTag beTag = tag.getCompound("BlockEntityTag");
				if (beTag.contains("sherds", Tag.TAG_LIST)) {
					CompoundTag toReturnIntermediary = new CompoundTag();
					//Note: We don't bother verifying all the entries in the tag are valid as that seems like it is probably unnecessary processing
					toReturnIntermediary.put("sherds", beTag.getList("sherds", Tag.TAG_STRING));
					CompoundTag toReturn = new CompoundTag();
					toReturn.put("BlockEntityTag", toReturnIntermediary);
					return toReturn;
				}
			}
		}
		return null;
	}
}
