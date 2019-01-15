package moze_intel.projecte.api.proxy;

import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public interface IBlacklistProxy
{
    /**
     * Blacklist an Entity from being repelled by the Interdiction Torch
     * Call this during any of the main loading phases
     * This method can be called during parallel mod loading
     * @param type The entity type to blacklist
     */
    void blacklistInterdiction(@Nonnull EntityType<?> type);

    /**
     * Blacklist an Entity from being repelled by the SWRG's repel mode
     * Call this during any of the main loading phases
     * This method can be called during parallel mod loading
     * @param type The entity type to blacklist
     */
    void blacklistSwiftwolf(@Nonnull EntityType<?> type);

    /**
     * Prevent the Watch of Flowing Time from speeding up this TileEntity
     * Modders: Use this only to prevent things from breaking badly - leave balance to the modpacker and player
     * Call this during any of the main loading phases
     * This method can be called during parallel mod loading
     * @param type The TileEntity type to blacklist
     */
    void blacklistTimeWatch(@Nonnull TileEntityType<?> type);
}
