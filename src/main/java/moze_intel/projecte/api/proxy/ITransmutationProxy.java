package moze_intel.projecte.api.proxy;

import com.sun.javafx.beans.annotations.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public interface ITransmutationProxy
{
    /**
     * Register a world transmutation with the Philosopher's Stone
     * Calls this during the postinit phase
     * @param origin Original block when targeting world transmutation
     * @param originMeta Original metadata
     * @param result1 First result block
     * @param result1Meta First result metadata
     * @param result2 Alternate result (when sneaking). You may pass null, in which there will be no alternate transmutation
     * @param result2meta Alternate result metadata. If result2 is null, this value is ignored
     * @return Whether the registration succeeded. It may fail if transmutations already exist for the block origin
     */
    boolean registerWorldTransmutation(@Nonnull Block origin, int originMeta, @Nonnull Block result1, int result1Meta, @Nullable Block result2, int result2meta);

    /**
     * Queries the knowledge of the provided player
     * Calls may only be issued on the server side, and if the server is running
     * @param playerUUID The Player to query
     * @param stack The ItemStack to query
     * @return Whether the player has knowledge for this ItemStack
     */
    boolean hasKnowledgeFor(@Nonnull UUID playerUUID, @NonNull ItemStack stack);

    /**
     * Queries the knowledge of the provided player
     * Calls may only be issued on the server side, and if the server is running
     * @param playerUUID The Player to query
     * @return Whether the player has full/override knowledge from the Tome
     */
    boolean hasFullKnowledge(@Nonnull UUID playerUUID);

    /**
     * Adds to the knowledge of the provided player. Only works if player is online (for now)
     * Calls may only be issued on the server side, and if the server is running
     * @param playerUUID The Player to modify
     * @param stack The ItemStack to add
     */
    void addKnowledge(@NonNull UUID playerUUID, @NonNull ItemStack stack);

    /**
     * Removes from the knowledge of the provided player. Only works if player is online (for now)
     * Calls may only be issued on the server side, and if the server is running
     * @param playerUUID The Player to modify
     * @param stack The ItemStack to remove
     */
    void removeKnowledge(@NonNull UUID playerUUID, @NonNull ItemStack stack);

    /**
     * Sets the player's personal transmutation emc to that provided. Only works if player is online (for now)
     * Calls may only be issued on the server side, and if the server is running
     * @param playerUUID The Player to modify
     * @param emc The value to set
     */
    void setEMC(@Nonnull UUID playerUUID, double emc);

    /**
     * Gets the player's personal transmutation emc to that provided. Only works if player is online (for now)
     * Calls may only be issued on the server side, and if the server is running
     * @param playerUUID The Player to modify
     * @return The emc, or NaN if player is not found
     */
    double getEMC(@Nonnull UUID playerUUID);
}
