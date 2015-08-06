package moze_intel.projecte.handlers;

import com.google.common.collect.Sets;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Set;

public final class PlayerChecks
{
	private static final Set<EntityPlayerMP> flyChecks = Sets.newHashSet();
	private static final Set<EntityPlayerMP> fireChecks = Sets.newHashSet();
	private static final Set<EntityPlayerMP> stepChecks = Sets.newHashSet();
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

	// Checks if the server state of player capas mismatches with what ProjectE determines. If so, change it serverside and send a packet to client
	public static void update(EntityPlayerMP player)
	{
		if (!shouldPlayerFly(player))
		{
			if (player.capabilities.allowFlying)
			{
				PELogger.logDebug("PE says cannot fly, MC can fly, telling client it can't fly");
				PlayerHelper.updateClientServerFlight(player, false);
			}
		}
		else
		{
			if (!player.capabilities.allowFlying)
			{
				PELogger.logDebug("PE says fly, MC cannot fly, telling client it can fly");
				PlayerHelper.updateClientServerFlight(player, true);
			}
		}

		if (!shouldPlayerResistFire(player))
		{
			if (player.isImmuneToFire())
			{
				PlayerHelper.setPlayerFireImmunity(player, false);
			}
		}
		else
		{
			if (!player.isImmuneToFire())
			{
				PlayerHelper.setPlayerFireImmunity(player, true);
			}
		}

		if (!shouldPlayerStep(player))
		{
			if (player.stepHeight > 0.5F)
			{
				PlayerHelper.updateClientServerStepHeight(player, 0.5F);
			}
		}
		else
		{
			if (player.stepHeight < 1.0F)
			{
				PlayerHelper.updateClientServerStepHeight(player, 1.0F);
			}
		}
	}


	public static void onPlayerChangeDimension(EntityPlayerMP playerMP)
	{
		// Resend everything needed on clientside (all except fire resist)
		PlayerHelper.updateClientServerFlight(playerMP, playerMP.capabilities.allowFlying);
		PlayerHelper.updateClientServerStepHeight(playerMP, playerMP.stepHeight);
	}

	private static boolean shouldPlayerFly(EntityPlayerMP player)
	{
		if (player.capabilities.isCreativeMode)
		{
			return true;
		}

		for (ItemStack stack : player.inventory.armorInventory)
		{
			if (stack != null
					&& stack.getItem() instanceof IFlightProvider
					&& ((IFlightProvider) stack.getItem()).canProvideFlight(stack))
			{
				return true;
			}
		}

		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack != null
					&& stack.getItem() instanceof IFlightProvider
					&& ((IFlightProvider) stack.getItem()).canProvideFlight(stack))
			{
				return true;
			}
		}

		IInventory baubles = PlayerHelper.getBaubles(player);
		if (baubles != null)
		{
			for (int i = 0; i < baubles.getSizeInventory(); i++)
			{
				ItemStack stack = baubles.getStackInSlot(i);
				if (stack != null
						&& stack.getItem() instanceof IFlightProvider
						&& ((IFlightProvider) stack.getItem()).canProvideFlight(stack))
				{
					return true;
				}
			}
		}

		return false;
	}
	
	private static boolean shouldPlayerResistFire(EntityPlayerMP player)
	{
		if (player.capabilities.isCreativeMode)
		{
			return true;
		}


		for (ItemStack stack : player.inventory.armorInventory)
		{
			if (stack != null
					&& stack.getItem() instanceof IFireProtector
					&& ((IFireProtector) stack.getItem()).canProtectAgainstFire(stack, player))
			{
				return true;
			}
		}

		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack != null
					&& stack.getItem() instanceof IFireProtector
					&& ((IFireProtector) stack.getItem()).canProtectAgainstFire(stack, player))
			{
				return true;
			}
		}

		IInventory baubles = PlayerHelper.getBaubles(player);
		if (baubles != null)
		{
			for (int i = 0; i < baubles.getSizeInventory(); i++)
			{
				ItemStack stack = baubles.getStackInSlot(i);
				if (stack != null
						&& stack.getItem() instanceof IFireProtector
						&& ((IFireProtector) stack.getItem()).canProtectAgainstFire(stack, player))
				{
					return true;
				}
			}
		}

		return false;
	}
	
	private static boolean shouldPlayerStep(EntityPlayerMP player)
	{
		if (player.capabilities.isCreativeMode)
		{
			return true;
		}


		for (ItemStack stack : player.inventory.armorInventory)
		{
			if (stack != null
					&& stack.getItem() instanceof IStepAssister
					&& ((IStepAssister) stack.getItem()).canAssistStep(stack))
			{
				return true;
			}
		}

		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack != null
					&& stack.getItem() instanceof IStepAssister
					&& ((IStepAssister) stack.getItem()).canAssistStep(stack))
			{
				return true;
			}
		}

		IInventory baubles = PlayerHelper.getBaubles(player);
		if (baubles != null)
		{
			for (int i = 0; i < baubles.getSizeInventory(); i++)
			{
				ItemStack stack = baubles.getStackInSlot(i);
				if (stack != null
						&& stack.getItem() instanceof IStepAssister
						&& ((IStepAssister) stack.getItem()).canAssistStep(stack))
				{
					return true;
				}
			}
		}

		return false;
	}
}
