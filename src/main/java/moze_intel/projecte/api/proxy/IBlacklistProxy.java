package moze_intel.projecte.api.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public interface IBlacklistProxy
{
    /**
     * Blacklist an Entity class from being repelled by the Interdiction Torch
     * Call this during the init phase
     * @param clazz The entity class to blacklist
     */
    void blacklistInterdiction(@Nonnull Class<? extends Entity> clazz);

    /**
     * Prevent the Watch of Flowing Time from speeding up this TileEntity.
     * Use this only to prevent things from breaking badly - leave balance to the modpacker and player
     * Call this during the init or postinit phase
     * @param clazz The TileEntity to blacklist
     */
    void blacklistTimeWatch(@Nonnull Class<? extends TileEntity> clazz);

    /**
     * Whitelist an ItemStack, allowing stacks of its kind to dupe NBT during Transmutation and Condensation
     * Call this during the init phase
     * @param stack The stack to whitelist
     */
    void whitelistNBT(@Nonnull ItemStack stack);
}
