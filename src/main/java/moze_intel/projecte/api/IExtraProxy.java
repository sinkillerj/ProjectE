package moze_intel.projecte.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface IExtraProxy
{
    void registerInterdictionBlacklist(Class<? extends Entity> clazz);

    void whitelistNBT(ItemStack stack);
}
