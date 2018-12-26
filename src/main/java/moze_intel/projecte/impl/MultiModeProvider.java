package moze_intel.projecte.impl;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.item.IModeChanger;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MultiModeProvider implements ICapabilityProvider {
    private final IModeChanger impl;

    public MultiModeProvider(IModeChanger impl) {
        this.impl = impl;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PECore.MULTIMODE_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == PECore.MULTIMODE_CAP ? PECore.MULTIMODE_CAP.cast(impl) : null;
    }
}
