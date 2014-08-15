package moze_intel.events;

import moze_intel.utils.PlayerKnowledge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RegisterPropertiesEvent 
{
	@SubscribeEvent
	public void onEntityCreation(EntityConstructing event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entity;
			
			if (PlayerKnowledge.getProperties(player) == null)
			{
				PlayerKnowledge.register(player);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entity;
			PlayerKnowledge.getProperties(player).syncPlayerProps(player);
		}
	}
}
