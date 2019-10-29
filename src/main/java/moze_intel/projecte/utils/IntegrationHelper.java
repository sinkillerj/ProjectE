package moze_intel.projecte.utils;

import java.util.function.Supplier;
import moze_intel.projecte.capability.ItemCapabilityWrapper.ItemCapability;
import moze_intel.projecte.integration.curios.CurioItemCapability;
import top.theillusivec4.curios.api.capability.ICurio;

public class IntegrationHelper {

	public static final String CURIO_MODID = "curios";

	//Double supplier to make sure it does not resolve early
	public static final Supplier<Supplier<ItemCapability<ICurio>>> CURIO_CAP_SUPPLIER = () -> CurioItemCapability::new;
}