package moze_intel.projecte.proxies;

import moze_intel.projecte.playerData.AlchBagProps;
import moze_intel.projecte.playerData.TransmutationProps;
import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy implements IProxy
{
	public void registerKeyBinds() {} 
	public void registerRenderers() {}
	public void registerClientOnlyEvents() {}
	public void initializeManual() {}
	public void clearClientKnowledge() {}
	public TransmutationProps getClientTransmutationProps() { return null; }
	public AlchBagProps getClientBagProps() { return null; }
	public EntityPlayer getClientPlayer() { return null; }
	public boolean isJumpPressed() { return false; }
}
