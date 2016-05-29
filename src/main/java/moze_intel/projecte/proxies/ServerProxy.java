package moze_intel.projecte.proxies;

import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy implements IProxy
{
	public void registerKeyBinds() {} 
	public void registerRenderers() {}
	public void registerLayerRenderers() {}
	public void registerClientOnlyEvents() {}
	public void registerModels() {}
	public void initializeManual() {}
	public void clearClientKnowledge() {}
	public IKnowledgeProvider getClientTransmutationProps() { return null; }
	public IAlchBagProvider getClientBagProps() { return null; }
	public EntityPlayer getClientPlayer() { return null; }
	public boolean isJumpPressed() { return false; }
}
