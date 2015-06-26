package moze_intel.projecte;

import moze_intel.projecte.api.proxy.ITransmutationProxy;

import java.util.UUID;

public class TransmutationProxyImpl implements ITransmutationProxy
{
    public static final ITransmutationProxy instance = new TransmutationProxyImpl();

    private TransmutationProxyImpl() {}

    @Override
    public boolean hasKnowledgeFor(UUID playerUUID, Object obj)
    {
        // todo
        return false;
    }

    @Override
    public boolean addKnowledge(UUID playerUUID, Object obj)
    {
        // todo
        return false;
    }

    @Override
    public boolean removeKnowledge(UUID playerUUID, Object obj)
    {
        // todo
        return false;
    }
}
