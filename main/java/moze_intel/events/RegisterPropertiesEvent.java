package moze_intel.events;

import moze_intel.playerData.AlchemicalBagData;
import moze_intel.playerData.TransmutationKnowledge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RegisterPropertiesEvent 
{
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			TransmutationKnowledge.sync((EntityPlayer) event.entity);
			AlchemicalBagData.sync((EntityPlayer) event.entity);
		}
	}
}
