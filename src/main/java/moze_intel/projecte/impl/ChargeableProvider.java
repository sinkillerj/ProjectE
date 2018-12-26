package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IItemCharge;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargeableProvider implements ICapabilityProvider {
    private final IItemCharge impl;

    public ChargeableProvider(IItemCharge impl) {
        this.impl = impl;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PECore.CHARGEABLE_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == PECore.CHARGEABLE_CAP ? PECore.CHARGEABLE_CAP.cast(impl) : null;
    }
}
