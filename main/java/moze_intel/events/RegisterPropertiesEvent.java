package moze_intel.events;

import moze_intel.proxies.CommonProxy;
import moze_intel.utils.PlayerBagInventory;
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
			
			if (PlayerBagInventory.getProperties(player) == null)
			{
				PlayerBagInventory.register(player);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entity;
			
			NBTTagCompound playerKnowledge = CommonProxy.getEntityKnowledge(player.getCommandSenderName());
			
			if (playerKnowledge != null)
			{
				PlayerKnowledge.getProperties(player).loadNBTData(playerKnowledge);
				PlayerKnowledge.getProperties(player).syncPlayerProps(player);
			}
			
			NBTTagCompound playerBagData = CommonProxy.getEntityBagData(player.getCommandSenderName());
			
			if (playerBagData != null)
			{
				PlayerBagInventory.getProperties(player).loadNBTData(playerBagData);
				PlayerKnowledge.getProperties(player).syncPlayerProps(player);
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event)
	{
		if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer)
		{
			NBTTagCompound playerKnowledge = new NBTTagCompound();
			NBTTagCompound playerBagData = new NBTTagCompound();
			
			PlayerKnowledge.getProperties((EntityPlayer) event.entity).saveNBTData(playerKnowledge);
			PlayerBagInventory.getProperties((EntityPlayer) event.entity).saveNBTData(playerBagData);
			
			CommonProxy.storeEntityKnowleddge(event.entity.getCommandSenderName(), playerKnowledge);
			CommonProxy.storeEntityBagData(event.entity.getCommandSenderName(), playerBagData);
		}
	}
}
