package moze_intel.projecte.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.armor.GemArmor;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.StepHeightPKT;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.LinkedList;

public class PlayerChecksEvent
{
	private static LinkedList<EntityPlayerMP> flyChecks = new LinkedList<EntityPlayerMP>();
	private static LinkedList<EntityPlayerMP> fireChecks = new LinkedList<EntityPlayerMP>();
	private static LinkedList<EntityPlayerMP> stepChecks = new LinkedList<EntityPlayerMP>();
	private static Iterator<EntityPlayerMP> iter;

	@SubscribeEvent
	public void tickEvent(WorldTickEvent event)
	{
		World world = event.world;
		
		iter = flyChecks.iterator();
		
		while (iter.hasNext())
		{
			EntityPlayerMP player = iter.next();
			
			if (!canPlayerFly(player))
			{
				if (player.capabilities.allowFlying)
				{
					Utils.setPlayerFlight(player, false);
				}
				
				iter.remove();
				PELogger.logInfo("Removed " + player.getCommandSenderName() + " from flight checks.");
			}
		}
		
		iter = fireChecks.iterator();
		
		while (iter.hasNext())
		{
			EntityPlayerMP player = iter.next();
			
			if (!isPlayerFireImmune(player))
			{
				if (player.isImmuneToFire())
				{
					Utils.setPlayerFireImmunity(player, false);
				}
				
				iter.remove();
				PELogger.logInfo("Removed " + player.getCommandSenderName() + " from fire checks.");
			}
		}
		
		iter = stepChecks.iterator();
		
		while (iter.hasNext())
		{
			EntityPlayerMP player = iter.next();
			
			if (!canPlayerStep(player))
			{
				PacketHandler.sendTo(new StepHeightPKT(0.5f), player);
				
				iter.remove();
				PELogger.logInfo("Removed " + player.getCommandSenderName() + " from step checks.");
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
		
		if (canPlayerStep(event.player))
		{
			PacketHandler.sendTo(new StepHeightPKT(1.0f), (EntityPlayerMP) event.player);
		}
	}
	
	public static void addPlayerFlyChecks(EntityPlayerMP player)
	{
		if (!flyChecks.contains(player))
		{
			flyChecks.add(player);
			PELogger.logInfo("Added " + player.getCommandSenderName() + " to flight checks.");
		}
	}
	
	public static void addPlayerFireChecks(EntityPlayerMP player)
	{
		if (!fireChecks.contains(player))
		{
			fireChecks.add(player);
			PELogger.logInfo("Added " + player.getCommandSenderName() + " to fire checks.");
		}
	}
	
	public static void addPlayerStepChecks(EntityPlayerMP player)
	{
		if (!stepChecks.contains(player))
		{
			stepChecks.add(player);
			PELogger.logInfo("Added " + player.getCommandSenderName() + " to step height checks.");
		}
	}
	
	public static void removePlayerFlyChecks(EntityPlayerMP player)
	{
		if (flyChecks.contains(player))
		{
			Iterator<EntityPlayerMP> iterator = flyChecks.iterator();
			
			while (iterator.hasNext())
			{
				if (iterator.next().equals(player))
				{
					iterator.remove();
					PELogger.logInfo("Removed " + player.getCommandSenderName() + " from flight checks.");
					return;
				}
			}
		}
	}
	
	public static void removePlayerFireChecks(EntityPlayerMP player)
	{
		if (fireChecks.contains(player))
		{
			Iterator<EntityPlayerMP> iterator = fireChecks.iterator();
			
			while (iterator.hasNext())
			{
				if (iterator.next().equals(player))
				{
					iterator.remove();
					PELogger.logInfo("Removed " + player + " from fire checks.");
					return;
				}
			}
		}
	}
	
	public static void removePlayerStepChecks(EntityPlayerMP player)
	{
		if (stepChecks.contains(player))
		{
			Iterator<EntityPlayerMP> iterator = stepChecks.iterator();
			
			while (iterator.hasNext())
			{
				if (iterator.next().equals(player))
				{
					iterator.remove();
					PELogger.logInfo("Removed " + player + " from step height checks.");
					return;
				}
			}
		}
	}
	
	public static void removePlayerFromLists(String username)
	{
		Iterator<EntityPlayerMP> iterator = flyChecks.iterator();
		
		while (iterator.hasNext())
		{
			if (iterator.next().getCommandSenderName().equals(username))
			{
				iterator.remove();
				break;
			}
		}
		
		iterator = fireChecks.iterator();
		
		while (iterator.hasNext())
		{
			if (iterator.next().getCommandSenderName().equals(username))
			{
				iterator.remove();
				break;
			}
		}
		
		iterator = stepChecks.iterator();
		
		while (iterator.hasNext())
		{
			if (iterator.next().getCommandSenderName().equals(username))
			{
				iterator.remove();
				break;
			}
		}
	}

	public static boolean isPlayerCheckedForStep(String player)
	{
        Iterator<EntityPlayerMP> iter = stepChecks.iterator();

        while (iter.hasNext())
        {
            if (iter.next().getCommandSenderName().equals(player))
            {
                return true;
            }
        }

		return false;
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
	
	public boolean canPlayerStep(EntityPlayer player)
	{
		ItemStack boots = player.getCurrentArmor(0);
		
		return (boots != null && boots.getItem() == ObjHandler.gemFeet && GemArmor.isStepAssistEnabled(boots));
	}
}
