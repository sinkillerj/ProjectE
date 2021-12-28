package moze_intel.projecte.capability;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IItemCapabilitySerializable extends INBTSerializable<Tag> {

	String getStorageKey();
}