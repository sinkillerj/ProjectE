package moze_intel.projecte.gameObjs.items;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@FunctionalInterface
public interface ICapabilityAware {

	void attachCapabilities(RegisterCapabilitiesEvent event);
}