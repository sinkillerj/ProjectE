package moze_intel.projecte.capability.managing;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class SerializableCapabilityResolver<CAPABILITY extends INBTSerializable<CompoundTag>>
		extends BasicCapabilityResolver<CAPABILITY> implements ICapabilitySerializable<CompoundTag> {

	protected final CAPABILITY internal;

	protected SerializableCapabilityResolver(CAPABILITY internal) {
		super(internal);
		this.internal = internal;
	}

	@Override
	public CompoundTag serializeNBT() {
		return internal.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		internal.deserializeNBT(nbt);
	}
}