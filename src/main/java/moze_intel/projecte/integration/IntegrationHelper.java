package moze_intel.projecte.integration;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class IntegrationHelper {

	public static final String CURIO_MODID = "curios";
	public static final String TOP_MODID = "theoneprobe";

	//Double supplier to make sure it does not resolve early
	//public static final Supplier<Supplier<ItemCapability<ICurio>>> CURIO_CAP_SUPPLIER = () -> CurioItemCapability::new;

	public static void sendIMCMessages(InterModEnqueueEvent event) {
		ModList modList = ModList.get();
		/*if (modList.isLoaded(CURIO_MODID)) {
			CuriosIntegration.sendIMC(event);
		}
		if (modList.isLoaded(TOP_MODID)) {
			TOPIntegration.sendIMC(event);
		}*/
	}
}