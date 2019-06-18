package moze_intel.projecte.utils;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class DummyIStorage<T> implements Capability.IStorage<T>
{
    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side)
    {
        return null;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {}
}
