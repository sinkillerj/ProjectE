package moze_intel.projecte.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT)
public class PlayerRender {

	@SubscribeEvent
	public static void onFOVUpdateEvent(FOVModifierEvent evt) {
		if (!evt.getPlayer().getItemBySlot(EquipmentSlot.FEET).isEmpty() && evt.getPlayer().getItemBySlot(EquipmentSlot.FEET).getItem() instanceof GemFeet) {
			evt.setNewFov(evt.getFov() - 0.4F);
		}
	}
}