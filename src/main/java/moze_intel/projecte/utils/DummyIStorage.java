package moze_intel.projecte.utils;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class DummyIStorage<T> implements Capability.IStorage<T>
{
    @Override
    public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side)
    {
        return null;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {}
}
