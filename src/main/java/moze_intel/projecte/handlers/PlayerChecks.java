package moze_intel.projecte.handlers;

import com.google.common.collect.Sets;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Set;

public final class PlayerChecks
{
	private static final Set<EntityPlayerMP> swrgOverrides = Sets.newHashSet();
	private static final Set<EntityPlayerMP> gemArmorReadyChecks = Sets.newHashSet();

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
				PlayerHelper.updateClientServerFlight(player, false);
			}
		}
		else
		{
			if (!player.capabilities.allowFlying)
			{
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
		if (player.capabilities.isCreativeMode || swrgOverrides.contains(player))
		{
			return true;
		}

		for (ItemStack stack : player.inventory.armorInventory)
		{
			if (stack != null
					&& stack.getItem() instanceof IFlightProvider
					&& ((IFlightProvider) stack.getItem()).canProvideFlight(stack, player))
			{
				return true;
			}
		}

		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack != null
					&& stack.getItem() instanceof IFlightProvider
					&& ((IFlightProvider) stack.getItem()).canProvideFlight(stack, player))
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
						&& ((IFlightProvider) stack.getItem()).canProvideFlight(stack, player))
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
					&& ((IStepAssister) stack.getItem()).canAssistStep(stack, player))
			{
				return true;
			}
		}

		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack != null
					&& stack.getItem() instanceof IStepAssister
					&& ((IStepAssister) stack.getItem()).canAssistStep(stack, player))
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
						&& ((IStepAssister) stack.getItem()).canAssistStep(stack, player))
				{
					return true;
				}
			}
		}

		return false;
	}

	public static void enableSwrgFlightOverride(EntityPlayerMP player)
	{
		swrgOverrides.add(player);
	}

	public static void disableSwrgFlightOverride(EntityPlayerMP player)
	{
		swrgOverrides.remove(player);
	}

	public static void clearLists()
	{
		swrgOverrides.clear();
		gemArmorReadyChecks.clear();
	}

	public static void removePlayerFromLists(EntityPlayerMP player)
	{
		swrgOverrides.remove(player);
		gemArmorReadyChecks.remove(player);
	}
}
