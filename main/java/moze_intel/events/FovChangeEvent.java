package moze_intel.events;

import moze_intel.gameObjs.ObjHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
