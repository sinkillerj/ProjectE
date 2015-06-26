package moze_intel.projecte.api.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface IExtraProxy
{
    /**
     * Blacklist an Entity class from being repelled by the Interdiction Torch
     * @param clazz The entity class to blacklist
     */
    void registerInterdictionBlacklist(Class<? extends Entity> clazz);

    /**
     * Whitelist an ItemStack, allowing stacks of its kind to dupe NBT during Transmutation and Condensation
     * @param stack The stack to whitelist
     */
    void whitelistNBT(ItemStack stack);
}
