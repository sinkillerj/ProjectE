package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class PlayerRender {

	@SubscribeEvent
	public static void onFOVUpdateEvent(ComputeFovModifierEvent evt) {
		if (!evt.getPlayer().getItemBySlot(EquipmentSlot.FEET).isEmpty() && evt.getPlayer().getItemBySlot(EquipmentSlot.FEET).getItem() instanceof GemFeet) {
			evt.setNewFovModifier(evt.getNewFovModifier() - 0.5F * Minecraft.getInstance().options.fovEffectScale().get().floatValue());
		}
	}
}