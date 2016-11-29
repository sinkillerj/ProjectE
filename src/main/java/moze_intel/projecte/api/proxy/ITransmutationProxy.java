package moze_intel.projecte.api.proxy;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.block.state.IBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    boolean registerWorldTransmutation(@Nonnull IBlockState origin, @Nonnull IBlockState result1, @Nullable IBlockState result2);

    /**
     * Gets an {@link IKnowledgeProvider} representing the UUID provided.
     *
     * If the provided UUID is offline, note that the returned {@link IKnowledgeProvider} is immutable!
     * If called clientside, {@param playerUUID} is ignored and the client player is used instead.
     * If called serverside, this must be called after the server has reached state SERVER_STARTED.
     *
     * If the provided UUID could not be found both on or offline, an {@link IKnowledgeProvider} with no knowledge is returned.
     *
     * @param playerUUID The UUID to query
     * @return an {@link IKnowledgeProvider} representing the UUID provided, or an {@link IKnowledgeProvider} representing no knowledge if
     * the requested UUID could not be found
     */
    @Nonnull IKnowledgeProvider getKnowledgeProviderFor(@Nonnull UUID playerUUID);
}
