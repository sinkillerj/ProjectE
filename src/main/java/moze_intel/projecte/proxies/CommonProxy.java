package moze_intel.projecte.proxies;

import moze_intel.projecte.playerData.AlchBagProps;
import moze_intel.projecte.playerData.TransmutationProps;

public class CommonProxy
{
	public void registerKeyBinds() {} 
	public void registerRenderers() {}
	public void registerClientOnlyEvents() {}
	public void registerModels() {}
	public void clearClientKnowledge() { }
	public TransmutationProps getClientTransmutationProps() { return null; }
	public AlchBagProps getClientBagProps() { return null; }
}
