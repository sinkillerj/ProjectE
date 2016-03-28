package moze_intel.projecte.events;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerRender
{
	@SubscribeEvent
	public void onFOVUpdateEvent(FOVUpdateEvent evt)
	{
		if (evt.getEntity().getItemStackFromSlot(EntityEquipmentSlot.FEET) != null && evt.getEntity().getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == ObjHandler.gemFeet)
		{
			evt.setNewfov(evt.getFov() - 0.4F);
		}
	}
}
