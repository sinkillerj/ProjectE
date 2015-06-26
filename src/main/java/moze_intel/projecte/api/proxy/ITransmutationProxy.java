package moze_intel.projecte.api.proxy;

import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface ITransmutationProxy
{
    /**
     * Queries the knowledge of the provided player
     * @param playerUUID The Player to query
     * @param stack The ItemStack to query
     * @return Whether the player has knowledge for this ItemStack
     */
    boolean hasKnowledgeFor(UUID playerUUID, ItemStack stack);

    /**
     * Adds to the knowledge of the provided player
     * @param playerUUID The Player to modify
     * @param stack The ItemStack to add
     */
    void addKnowledge(UUID playerUUID, ItemStack stack);

    /**
     * Removes from the knowledge of the provided player
     * @param playerUUID The Player to modify
     * @param stack The ItemStack to remove
     */
    void removeKnowledge(UUID playerUUID, ItemStack stack);
}
