package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.api.item.IModeChanger;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargeableMultiModeProvider implements ICapabilityProvider {
    private final IItemCharge chargeImpl;
    private final IModeChanger modeImpl;

    public ChargeableMultiModeProvider(IItemCharge chargeImpl, IModeChanger modeImpl) {
        this.chargeImpl = chargeImpl;
        this.modeImpl = modeImpl;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PECore.CHARGEABLE_CAP || capability == PECore.MULTIMODE_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == PECore.CHARGEABLE_CAP) {
            return PECore.CHARGEABLE_CAP.cast(chargeImpl);
        } else if (capability == PECore.MULTIMODE_CAP) {
            return PECore.MULTIMODE_CAP.cast(modeImpl);
        } else {
            return null;
        }
    }
}
