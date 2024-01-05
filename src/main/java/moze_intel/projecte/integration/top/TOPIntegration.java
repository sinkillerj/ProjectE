package moze_intel.projecte.integration.top;

import moze_intel.projecte.integration.IntegrationHelper;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

public class TOPIntegration {

	public static void sendIMC(InterModEnqueueEvent event) {
		InterModComms.sendTo(IntegrationHelper.TOP_MODID, "getTheOneProbe", PEProbeInfoProvider::new);
	}
}