package moze_intel.projecte.api.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

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
     * @return Whether the registration succeeded. It may fail if transmutations already exist for block origin
     */
    boolean registerWorldTransmutation(Block origin, int originMeta, Block result1, int result1Meta, @Nullable Block result2, int result2meta);

    /**
     * Queries the knowledge of the provided player
     * Can be called on both sides, only if the client player exists or the server is started
     * If called on the client side, playerUUID is ignored and the client player is used instead
     * @param playerUUID The Player to query
     * @param stack The ItemStack to query
     * @return Whether the player has knowledge for this ItemStack, false if player is not found
     */
    boolean hasKnowledgeFor(UUID playerUUID, ItemStack stack);

    /**
     * Queries all the knowledge of the provided player
     * Can be called on both sides, only if the client player exists or the server is started
     * If called on the client side, playerUUID is ignored and the client player is used instead
     * @param playerUUID The Player to query
     * @return List<ItemStack> List of ItemStacks the player has transmutation knowledge of.
     */
    List<ItemStack> getKnowledge(UUID playerUUID);
    
    /**
     * Queries the knowledge of the provided player
     * Can be called on both sides, only if the client player exists or the server is started
     * If called on the client side, playerUUID is ignored and the client player is used instead
     * @param playerUUID The Player to query
     * @return Whether the player has full/override knowledge from the Tome, false if player is not found
     * @deprecated as of API version 8, use {@link #hasKnowledgeFor(UUID, ItemStack)}
     */
    @Deprecated
    boolean hasFullKnowledge(UUID playerUUID);

    /**
     * Adds to the knowledge of the provided player. Only works if player is online
     * Calls may only be issued on the server side, and if the server is running
     * @param playerUUID The Player to modify
     * @param stack The ItemStack to add
     */
    void addKnowledge(UUID playerUUID, ItemStack stack);

    /**
     * Removes from the knowledge of the provided player. Only works if player is online
     * Calls may only be issued on the server side, and if the server is running
     * @param playerUUID The Player to modify
     * @param stack The ItemStack to remove
     */
    void removeKnowledge(UUID playerUUID, ItemStack stack);

    /**
     * Sets the player's personal transmutation emc to that provided. Only works if player is online
     * Calls may only be issued on the server side, and if the server is running
     * @param playerUUID The Player to modify
     * @param emc The value to set
     */
    void setEMC(UUID playerUUID, double emc);

    /**
     * Gets the player's personal transmutation emc
     * Can be called on both sides, only if the client player exists or the server is started
     * If called on the client side, playerUUID is ignored and the client player is used instead
     * @param playerUUID The Player to modify
     * @return The emc, or NaN if player is not found
     */
    double getEMC(UUID playerUUID);
}
