package moze_intel.projecte.api;

import java.util.UUID;

public interface ITransmutationProxy
{
    boolean hasKnowledgeFor(UUID playerUUID, Object obj);

    boolean addKnowledge(UUID playerUUID, Object obj);

    boolean removeKnowledge(UUID playerUUID, Object obj);
}
