package moze_intel.projecte.integration.curios;

import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.curios.api.SlotTypeMessage;

public class CuriosIntegration {

	@Nullable
	public static IItemHandler getAll(LivingEntity living) {
		//TODO - 1.16: FIXME, I don't think this will work at all as it isn't that type of itemhandler
		return null;
		/*return LazyOptionalHelper.toOptional(CuriosApi.getCuriosHelper().getCuriosHandler(living)).map(curiosHandler ->
				new CombinedInvWrapper(curiosHandler.getCurios().values().toArray(new IItemHandlerModifiable[0]))).orElse(null);*/
	}

	public static void sendIMC(InterModEnqueueEvent event) {
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("necklace").build());
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("belt").build());
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").build());
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("klein_star").icon(PECore.rl("klein_star")).build());
	}
}