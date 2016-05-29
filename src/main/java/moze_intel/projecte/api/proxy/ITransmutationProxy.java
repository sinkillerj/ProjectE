package moze_intel.projecte.api.proxy;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface ITransmutationProxy
{
    /**
     * Register a world transmutation with the Philosopher's Stone
     * Calls this during the postinit phase
     * @param origin Original blockstate when targeting world transmutation
     * @param result1 First result blockstate
     * @param result2 Alternate result blockstate (when sneaking). You may pass null, in which there will be no alternate transmutation
     * @return Whether the registration succeeded. It may fail if transmutations already exist for block origin
     */
    boolean registerWorldTransmutation(IBlockState origin, IBlockState result1, IBlockState result2);

    /**
     * Gets an {@link IKnowledgeProvider} representing the UUID provided.
     *
     * If the provided UUID is offline, note that the returned {@link IKnowledgeProvider} is immutable!
     * If called clientside, {@param playerUUID} is ignored and the client player is used instead.
     * If called serverside, this must be called after the server has reached state SERVER_STARTED.
     *
     * @param playerUUID The UUID to query
     * @return an {@link IKnowledgeProvider} representing the UUID provided
     */
    IKnowledgeProvider getKnowledgeProviderFor(UUID playerUUID);
}
