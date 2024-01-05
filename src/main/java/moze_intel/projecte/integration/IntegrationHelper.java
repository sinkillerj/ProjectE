package moze_intel.projecte.integration;

import moze_intel.projecte.integration.curios.CurioItemCapability;
import moze_intel.projecte.integration.top.TOPIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

public class IntegrationHelper {

	public static final String CURIO_MODID = "curios";
	public static final String TOP_MODID = "theoneprobe";

	public static final EntityCapability<IItemHandler, Void> CURIO_ITEM_HANDLER = EntityCapability.createVoid(new ResourceLocation(CURIO_MODID, "item_handler"), IItemHandler.class);

	public static void sendIMCMessages(InterModEnqueueEvent event) {
		ModList modList = ModList.get();
		if (modList.isLoaded(TOP_MODID)) {
			TOPIntegration.sendIMC(event);
		}
	}

	public static void registerCuriosCapability(RegisterCapabilitiesEvent event, Item item) {
		if (ModList.get().isLoaded(CURIO_MODID)) {
			CurioItemCapability.register(event, item);
		}
	}
}