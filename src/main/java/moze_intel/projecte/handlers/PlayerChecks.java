package moze_intel.projecte.handlers;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import moze_intel.projecte.utils.IFireProtectionItem;
import moze_intel.projecte.utils.IFlightItem;
import moze_intel.projecte.utils.IStepAssistItem;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.armor.GemFeet;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class PlayerChecks
{
	private static final List<EntityPlayerMP> flyChecks = Lists.newArrayList();
	private static final List<EntityPlayerMP> fireChecks = Lists.newArrayList();
	private static final List<EntityPlayerMP> stepChecks = Lists.newArrayList();
	public static final Set<EntityPlayerMP> gemArmorReadyChecks = Sets.newHashSet();

	public static void setGemState(EntityPlayerMP player, boolean state)
	{
		if (state)
		{
			gemArmorReadyChecks.add(player);
		}
		else
		{
			gemArmorReadyChecks.remove(player);
		}
	}

	public static boolean getGemState(EntityPlayerMP player)
	{
		return gemArmorReadyChecks.contains(player);
	}

	public static void update()
	{
		Iterator<EntityPlayerMP> iter = flyChecks.iterator();

		while (iter.hasNext())
		{
			EntityPlayerMP player = iter.next();

			if (!canPlayerFly(player))
			{
				if (player.capabilities.allowFlying)
				{
					PlayerHelper.updateClientServerFlight(player, false);
				}

				iter.remove();
				PELogger.logDebug("Removed " + player.getCommandSenderName() + " from flight checks.");
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
					PlayerHelper.setPlayerFireImmunity(player, false);
				}

				iter.remove();
				PELogger.logDebug("Removed " + player.getCommandSenderName() + " from fire checks.");
			}
		}

		iter = stepChecks.iterator();

		while (iter.hasNext())
		{
			EntityPlayerMP player = iter.next();

			if (!canPlayerStep(player))
			{
				player.stepHeight = 0.5f;
				PlayerHelper.updateClientStepHeight(player, 0.5F);

				iter.remove();
				PELogger.logDebug("Removed " + player.getCommandSenderName() + " from step checks.");
			}
		}
	}

	public static void onPlayerChangeDimension(EntityPlayerMP playerMP)
	{
		if (canPlayerFly(playerMP))
		{
			PlayerHelper.updateClientServerFlight(playerMP, true);
		}

		if (isPlayerFireImmune(playerMP))
		{
			PlayerHelper.setPlayerFireImmunity(playerMP, true);
		}

		if (canPlayerStep(playerMP))
		{
			playerMP.stepHeight = 1.0f;
			PlayerHelper.updateClientStepHeight(playerMP, 1.0F);
		}
	}

	public static void addPlayerFlyChecks(EntityPlayerMP player)
	{
		if (!flyChecks.contains(player))
		{
			flyChecks.add(player);
			PELogger.logDebug("Added " + player.getCommandSenderName() + " to flight checks.");
		}
	}
	
	public static void addPlayerFireChecks(EntityPlayerMP player)
	{
		if (!fireChecks.contains(player))
		{
			fireChecks.add(player);
			PELogger.logDebug("Added " + player.getCommandSenderName() + " to fire checks.");
		}
	}
	
	public static void addPlayerStepChecks(EntityPlayerMP player)
	{
		if (!stepChecks.contains(player))
		{
			stepChecks.add(player);
			PELogger.logDebug("Added " + player.getCommandSenderName() + " to step height checks.");
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
					PELogger.logDebug("Removed " + player.getCommandSenderName() + " from flight checks.");
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
					PELogger.logDebug("Removed " + player + " from fire checks.");
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
					PELogger.logDebug("Removed " + player + " from step height checks.");
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
	
	private static boolean canPlayerFly(EntityPlayer player)
	{
		if (player.capabilities.isCreativeMode)
		{
			return true;
		}

		ItemStack armor = player.getCurrentArmor(0);
		if (armor != null && armor.getItem() instanceof GemFeet)
		{
			return true;
		}

		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			
			if (stack != null && stack.getItem() instanceof IFlightItem)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean isPlayerFireImmune(EntityPlayer player)
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
			
			if (stack != null && stack.getItem() instanceof IFireProtectionItem)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean canPlayerStep(EntityPlayer player)
	{
		ItemStack boots = player.getCurrentArmor(0);
		
		return ((boots != null && boots.getItem() == ObjHandler.gemFeet && GemFeet.isStepAssistEnabled(boots)) || hasPlayerEquippedStepAssist(player));
	}
	
	private static boolean hasPlayerEquippedStepAssist(EntityPlayer player)
	{
		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			
			if (stack != null && stack.getItem() instanceof IStepAssistItem)
			{
				return true;
			}
		}
		
		return false;
	}
}
