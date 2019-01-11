package moze_intel.projecte.api.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public interface IBlacklistProxy
{
    /**
     * Blacklist an Entity from being repelled by the Interdiction Torch
     * Call this during the postinit phase
     * @param type The entity type to blacklist
     */
    void blacklistInterdiction(@Nonnull EntityType<?> type);

    /**
     * Blacklist an Entity from being repelled by the SWRG's repel mode
     * Call this during the postinit phase
     * @param type The entity type to blacklist
     */
    void blacklistSwiftwolf(@Nonnull EntityType<?> type);

    /**
     * Prevent the Watch of Flowing Time from speeding up this TileEntity
     * Modders: Use this only to prevent things from breaking badly - leave balance to the modpacker and player
     * Call this during the postinit phase
     * @param type The TileEntity type to blacklist
     */
    void blacklistTimeWatch(@Nonnull TileEntityType<?> type);

    /**
     * Whitelist an ItemStack, allowing stacks of its kind to dupe NBT during Transmutation and Condensation
     * Call this during the postinit phase
     * @param stack The stack to whitelist
     */
    void whitelistNBT(@Nonnull ItemStack stack);
}
