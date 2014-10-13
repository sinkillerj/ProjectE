package moze_intel.projecte.events;

import java.util.Iterator;
import java.util.LinkedList;

import baubles.api.BaublesApi;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.armor.GemArmor;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.StepHeightPKT;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class PlayerChecksEvent
{
	private static LinkedList<EntityPlayerMP> flyChecks = new LinkedList();
	private static LinkedList<EntityPlayerMP> fireChecks = new LinkedList();
	private static LinkedList<EntityPlayerMP> stepChecks = new LinkedList();
	
	@SubscribeEvent
	public void tickEvent(WorldTickEvent event)
	{
		World world = event.world;
		
		for (EntityPlayerMP player : flyChecks)
		{
			if (!canPlayerFly(player))
			{
				if (player.capabilities.allowFlying)
				{
					Utils.setPlayerFlight(player, false);
				}
				
				removePlayerFlyChecks(player);
			}
		}
		
		for (EntityPlayerMP player : fireChecks)
		{
			if (!isPlayerFireImmune(player))
			{
				if (player.isImmuneToFire())
				{
					Utils.setPlayerFireImmunity(player, false);
				}
				
				removePlayerFireChecks(player);
			}
		}
		
		for (EntityPlayerMP player : stepChecks)
		{
			if (!canPlayerStep(player))
			{
				PacketHandler.sendTo(new StepHeightPKT(0.5f), player);
				removePlayerStepChecks(player);
			}
		}
	}
	
	@SubscribeEvent
	public void playerChangeDimension(PlayerChangedDimensionEvent event)
	{
		if (canPlayerFly(event.player))
		{
			Utils.setPlayerFlight((EntityPlayerMP) event.player, true);
		}
		
		if (isPlayerFireImmune(event.player))
		{
			Utils.setPlayerFireImmunity(event.player, true);
		}
		
		if (canPlayerStep((EntityPlayerMP) event.player))
		{
			PacketHandler.sendTo(new StepHeightPKT(1.0f), (EntityPlayerMP) event.player);
		}
	}
	
	public static void addPlayerFlyChecks(EntityPlayerMP player)
	{
		if (!flyChecks.contains(player))
		{
			flyChecks.add(player);
			PELogger.logInfo("Added "+player.getCommandSenderName()+" to flight checks.");
		}
	}
	
	public static void addPlayerFireChecks(EntityPlayerMP player)
	{
		if (!fireChecks.contains(player))
		{
			fireChecks.add(player);
			PELogger.logInfo("Added "+player.getCommandSenderName()+" to fire checks.");
		}
	}
	
	public static void addPlayerStepChecks(EntityPlayerMP player)
	{
		if (!stepChecks.contains(player))
		{
			stepChecks.add(player);
			PELogger.logInfo("Added "+player.getCommandSenderName()+" to step height checks.");
		}
	}
	
	public static void removePlayerFlyChecks(EntityPlayerMP player)
	{
		if (flyChecks.contains(player))
		{
			flyChecks.remove();
			PELogger.logInfo("Removed "+player.getCommandSenderName()+" from flight checks.");
		}
	}
	
	public static void removePlayerFireChecks(EntityPlayerMP player)
	{
		if (fireChecks.contains(player))
		{
			fireChecks.remove(player);
			PELogger.logInfo("Removed "+player.getCommandSenderName()+" from fire checks.");
		}
	}
	
	public static void removePlayerStepChecks(EntityPlayerMP player)
	{
		if (stepChecks.contains(player))
		{
			stepChecks.remove(player);
			PELogger.logInfo("Removed "+player.getCommandSenderName()+" from step height checks.");
		}
	}
	
	public static void removePlayerFromLists(String username)
	{
		for (EntityPlayerMP player : flyChecks)
		{
			if (player.getCommandSenderName().equals(username))
			{
				flyChecks.remove(player);
			}
		}
		
		for (EntityPlayerMP player : fireChecks)
		{
			if (player.getCommandSenderName().equals(username))
			{
				fireChecks.remove(player);
			}
		}
		
		for (EntityPlayerMP player : stepChecks)
		{
			if (player.getCommandSenderName().equals(username))
			{
				stepChecks.remove(player);
			}
		}
	}
	
	public static boolean isPlayerCheckedForFlight(EntityPlayerMP player)
	{
		return flyChecks.contains(player);
	}
	
	public static boolean isPlayerCheckedForFire(EntityPlayerMP player)
	{
		return fireChecks.contains(player);
	}
	
	public static boolean isPlayerCheckedForStep(EntityPlayerMP player)
	{
		return stepChecks.contains(player);
	}
	
	public static void clearLists()
	{
		flyChecks.clear();
		fireChecks.clear();
		stepChecks.clear();
	}
	
	private boolean canPlayerFly(EntityPlayer player)
	{
		if (player.capabilities.isCreativeMode)
		{
			return true;
		}
		
		ItemStack boots = player.getCurrentArmor(0);
		
		if (boots != null && boots.getItem() == ObjHandler.gemFeet)
		{
			return true;
		}
		
		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			
			if (stack == null)
			{
				continue;
			}
			else if (stack.getItem() ==  ObjHandler.swrg /*|| stack.getItem() == ObjHandler.arcana*/)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isPlayerFireImmune(EntityPlayer player)
	{
		if (player.capabilities.isCreativeMode)
		{
			return true;
		}
		
		ItemStack chest = player.getCurrentArmor(2);
		
		if (chest != null && chest.getItem() == ObjHandler.gemChest)
		{
			return true;
		}
		
		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			
			if (stack == null)
			{
				continue;
			}
			else if (stack.getItem() == ObjHandler.volcanite /*|| stack.getItem() == ObjHandler.arcana*/)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean canPlayerStep(EntityPlayerMP player)
	{
		ItemStack boots = player.getCurrentArmor(0);
		
		return (boots != null && boots.getItem() == ObjHandler.gemFeet && GemArmor.isStepAssistEnabled(boots));
	}
}
