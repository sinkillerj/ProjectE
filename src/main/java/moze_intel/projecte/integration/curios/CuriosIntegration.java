package moze_intel.projecte.integration.curios;

import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class CuriosIntegration {

	@Nullable
	public static IItemHandler getAll(LivingEntity living) {
		return CuriosApi.getCuriosHelper().getEquippedCurios(living).resolve().orElse(null);
	}

	public static void sendIMC(InterModEnqueueEvent event) {
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("necklace").build());
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("belt").build());
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").build());
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("klein_star")
				.icon(PECore.rl("curios/empty_klein_star")).build());
	}
}