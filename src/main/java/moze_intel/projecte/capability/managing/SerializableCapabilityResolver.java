package moze_intel.projecte.capability.managing;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class SerializableCapabilityResolver<CAPABILITY extends INBTSerializable<CompoundNBT>>
		extends BasicCapabilityResolver<CAPABILITY> implements ICapabilitySerializable<CompoundNBT> {

	protected final CAPABILITY internal;

	protected SerializableCapabilityResolver(CAPABILITY internal) {
		super(internal);
		this.internal = internal;
	}

	@Override
	public CompoundNBT serializeNBT() {
		return internal.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		internal.deserializeNBT(nbt);
	}
}