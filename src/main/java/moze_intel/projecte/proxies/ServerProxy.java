package moze_intel.projecte.proxies;

import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy implements IProxy
{
	@Override public void registerKeyBinds() {}
	@Override public void registerRenderers() {}
	@Override public void registerLayerRenderers() {}
	@Override public void initializeManual() {}
	@Override public void clearClientKnowledge() {}
	@Override public IKnowledgeProvider getClientTransmutationProps() { return null; }
	@Override public IAlchBagProvider getClientBagProps() { return null; }
	@Override public EntityPlayer getClientPlayer() { return null; }
	@Override public boolean isJumpPressed() { return false; }
}
