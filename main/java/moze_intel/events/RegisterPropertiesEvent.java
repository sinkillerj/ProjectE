package moze_intel.events;

import moze_intel.proxies.CommonProxy;
import moze_intel.utils.PlayerKnowledge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
			
			NBTTagCompound playerData = CommonProxy.getEntityData(player.getCommandSenderName());
			
			if (playerData != null)
			{
				PlayerKnowledge.getProperties(player).loadNBTData(playerData);
			}
			
			PlayerKnowledge.getProperties(player).syncPlayerProps(player);
		}
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			NBTTagCompound playerData = new NBTTagCompound();
			
			PlayerKnowledge.getProperties((EntityPlayer) event.entity).saveNBTData(playerData);
			
			CommonProxy.storeEntityData(event.entity.getCommandSenderName(), playerData);
		}
	}
}
