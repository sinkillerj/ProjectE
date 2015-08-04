package moze_intel.projecte.events;

import moze_intel.projecte.gameObjs.ObjHandler;
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
		if (evt.entity.getCurrentArmor(0) != null && evt.entity.getCurrentArmor(0).getItem() == ObjHandler.gemFeet)
		{
			evt.newfov = evt.fov - 0.4F;
		}
	}
}
