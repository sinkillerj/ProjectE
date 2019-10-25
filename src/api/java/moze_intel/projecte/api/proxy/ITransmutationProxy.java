package moze_intel.projecte.api.proxy;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface ITransmutationProxy
{
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
