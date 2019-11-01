package moze_intel.projecte.capability;

import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IItemCapabilitySerializable extends INBTSerializable<INBT> {

	String getStorageKey();
}