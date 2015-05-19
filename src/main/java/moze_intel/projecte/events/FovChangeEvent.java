package moze_intel.projecte.events;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;

@SideOnly(Side.CLIENT)
public class FovChangeEvent 
{
	@SubscribeEvent
	public void onFOVChange(FOVUpdateEvent event)
	{
		ItemStack legs = event.entity.getCurrentArmor(1);
		
		if (legs != null && legs.getItem() == ObjHandler.gemLegs)
		{
			event.newfov -= 0.5f;
		}
	}
}
