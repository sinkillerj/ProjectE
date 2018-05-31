package moze_intel.projecte.proxies;

import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.entity.player.EntityPlayer;

public interface IProxy
{
    void registerKeyBinds();
    void registerRenderers();
    void registerLayerRenderers();
    void initializeManual();
    void clearClientKnowledge();
    IKnowledgeProvider getClientTransmutationProps();
    IAlchBagProvider getClientBagProps();
    EntityPlayer getClientPlayer();
    boolean isJumpPressed();
}
