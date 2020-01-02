package moze_intel.projecte.integration.curios;

import javax.annotation.Nullable;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.LazyOptionalHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.CuriosAPI.IMC;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

public class CuriosIntegration {

	@Nullable
	public static IItemHandler getAll(LivingEntity living) {
		return LazyOptionalHelper.toOptional(CuriosAPI.getCuriosHandler(living)).map(curiosHandler ->
				new CombinedInvWrapper(curiosHandler.getCurioMap().values().toArray(new IItemHandlerModifiable[0]))).orElse(null);
	}

	public static void sendIMC(InterModEnqueueEvent event) {
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("necklace"));
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("belt"));
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("ring"));
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("klein_star"));
		InterModComms.sendTo(IntegrationHelper.CURIO_MODID, IMC.REGISTER_ICON, () -> new Tuple<>("klein_star", IntegrationHelper.CURIOS_KLEIN_STAR));
	}
}