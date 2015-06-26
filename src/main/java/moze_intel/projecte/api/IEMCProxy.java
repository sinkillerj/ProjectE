package moze_intel.projecte.api;

import net.minecraft.item.ItemStack;

public interface IEMCProxy
{
    void registerCustomEmc(ItemStack stack, int value);

    boolean hasValue(Object obj);

    int getValue(Object obj);
}
