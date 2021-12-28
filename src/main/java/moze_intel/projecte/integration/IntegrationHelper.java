package moze_intel.projecte.integration;

import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.integration.curios.CurioItemCapability;
import moze_intel.projecte.integration.curios.CuriosIntegration;
import moze_intel.projecte.integration.top.TOPIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public class IntegrationHelper {

	public static final String CURIO_MODID = "curios";
	public static final String TOP_MODID = "theoneprobe";

	//Double supplier to make sure it does not resolve early
	public static final Supplier<Supplier<ItemCapability<?>>> CURIO_CAP_SUPPLIER = () -> CurioItemCapability::new;

	public static final ResourceLocation CURIOS_KLEIN_STAR = PECore.rl("curios/empty_klein_star");

	public static void sendIMCMessages(InterModEnqueueEvent event) {
		ModList modList = ModList.get();
		if (modList.isLoaded(CURIO_MODID)) {
			CuriosIntegration.sendIMC(event);
		}
		if (modList.isLoaded(TOP_MODID)) {
			TOPIntegration.sendIMC(event);
		}
	}
}