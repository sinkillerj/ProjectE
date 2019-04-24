package moze_intel.projecte.handlers;

import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.IFireProtector;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class InternalAbilities
{
	@CapabilityInject(InternalAbilities.class)
	public static final Capability<InternalAbilities> CAPABILITY = null;
	public static final ResourceLocation NAME = new ResourceLocation(PECore.MODID, "internal_abilities");

	private final EntityPlayerMP player;
	private boolean swrgOverride = false;
	private boolean gemArmorReady = false;
	private boolean hadFlightItem = false;
	private boolean wasFlyingGamemode = false;
	private boolean isFlyingGamemode = false;
	private boolean wasFlying = false;
	private int projectileCooldown = 0;
	private int gemChestCooldown = 0;

	public InternalAbilities(EntityPlayerMP player)
	{
		this.player = player;
	}

	public void resetProjectileCooldown() {
		projectileCooldown = ProjectEConfig.misc.projectileCooldown;
	}

	public int getProjectileCooldown() {
		return projectileCooldown;
	}

	public void resetGemCooldown() {
		gemChestCooldown = ProjectEConfig.misc.gemChestCooldown;
	}

	public int getGemCooldown() {
		return gemChestCooldown;
	}

	public void setGemState(boolean state)
	{
		gemArmorReady = state;
	}

	public boolean getGemState()
	{
		return gemArmorReady;
	}

	// Checks if the server state of player capas mismatches with what ProjectE determines. If so, change it serverside and send a packet to client
	public void tick()
	{
		if (projectileCooldown > 0)
		{
			projectileCooldown--;
		}

		if (gemChestCooldown > 0)
		{
			gemChestCooldown--;
		}

		if (!shouldPlayerFly())
		{
			if (hadFlightItem)
			{
				if (player.capabilities.allowFlying)
				{
					PlayerHelper.updateClientServerFlight(player, false);
				}

				hadFlightItem = false;
			}
			wasFlyingGamemode = false;
			wasFlying = false;
		}
		else
		{
			if (!hadFlightItem)
			{
				if (!player.capabilities.allowFlying)
				{
					PlayerHelper.updateClientServerFlight(player, true);
				}

				hadFlightItem = true;
			}
			else if (wasFlyingGamemode && !isFlyingGamemode)
			{
				//Player was in a gamemode that allowed flight, but no longer is but they still should be allowed to fly
				//Sync the fact to the client. Also passes wasFlying so that if they were flying previously,
				//and are still allowed to the gamemode change doesn't force them out of it
				PlayerHelper.updateClientServerFlight(player, true, wasFlying);
			}
			wasFlyingGamemode = isFlyingGamemode;
			wasFlying = player.capabilities.isFlying;
		}

		if (!shouldPlayerResistFire())
		{
			if (player.isImmuneToFire())
			{
				player.isImmuneToFire = false;
			}
		}
		else
		{
			if (!player.isImmuneToFire())
			{
				player.isImmuneToFire = true;
			}
		}

		if (!shouldPlayerStep())
		{
			if (player.stepHeight > 0.6F)
			{
				PlayerHelper.updateClientServerStepHeight(player, 0.6F);
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

	public void onDimensionChange()
	{
		// Resend everything needed on clientside (all except fire resist)
		PlayerHelper.updateClientServerFlight(player, player.capabilities.allowFlying);
		PlayerHelper.updateClientServerStepHeight(player, shouldPlayerStep() ? 1.0F : 0.6F);
	}

	private boolean shouldPlayerFly()
	{
		if (!hasSwrg())
		{
			disableSwrgFlightOverride();
		}

		isFlyingGamemode = player.capabilities.isCreativeMode || player.isSpectator();
		if (isFlyingGamemode || swrgOverride)
		{
			return true;
		}

		for (ItemStack stack : player.inventory.armorInventory)
		{
			if (!stack.isEmpty()
					&& stack.getItem() instanceof IFlightProvider
					&& ((IFlightProvider) stack.getItem()).canProvideFlight(stack, player))
			{
				return true;
			}
		}

		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (!stack.isEmpty()
					&& stack.getItem() instanceof IFlightProvider
					&& ((IFlightProvider) stack.getItem()).canProvideFlight(stack, player))
			{
				return true;
			}
		}

		IItemHandler baubles = PlayerHelper.getBaubles(player);
		if (baubles != null)
		{
			for (int i = 0; i < baubles.getSlots(); i++)
			{
				ItemStack stack = baubles.getStackInSlot(i);
				if (!stack.isEmpty()
						&& stack.getItem() instanceof IFlightProvider
						&& ((IFlightProvider) stack.getItem()).canProvideFlight(stack, player))
				{
					return true;
				}
			}
		}

		return false;
	}
	
	private boolean shouldPlayerResistFire()
	{
		if (player.capabilities.isCreativeMode)
		{
			return true;
		}


		for (ItemStack stack : player.inventory.armorInventory)
		{
			if (!stack.isEmpty()
					&& stack.getItem() instanceof IFireProtector
					&& ((IFireProtector) stack.getItem()).canProtectAgainstFire(stack, player))
			{
				return true;
			}
		}

		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (!stack.isEmpty()
					&& stack.getItem() instanceof IFireProtector
					&& ((IFireProtector) stack.getItem()).canProtectAgainstFire(stack, player))
			{
				return true;
			}
		}

		IItemHandler baubles = PlayerHelper.getBaubles(player);
		if (baubles != null)
		{
			for (int i = 0; i < baubles.getSlots(); i++)
			{
				ItemStack stack = baubles.getStackInSlot(i);
				if (!stack.isEmpty()
						&& stack.getItem() instanceof IFireProtector
						&& ((IFireProtector) stack.getItem()).canProtectAgainstFire(stack, player))
				{
					return true;
				}
			}
		}

		return false;
	}
	
	private boolean shouldPlayerStep()
	{
		for (ItemStack stack : player.inventory.armorInventory)
		{
			if (!stack.isEmpty()
					&& stack.getItem() instanceof IStepAssister
					&& ((IStepAssister) stack.getItem()).canAssistStep(stack, player))
			{
				return true;
			}
		}

		for (int i = 0; i <= 8; i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (!stack.isEmpty()
					&& stack.getItem() instanceof IStepAssister
					&& ((IStepAssister) stack.getItem()).canAssistStep(stack, player))
			{
				return true;
			}
		}

		IItemHandler baubles = PlayerHelper.getBaubles(player);
		if (baubles != null)
		{
			for (int i = 0; i < baubles.getSlots(); i++)
			{
				ItemStack stack = baubles.getStackInSlot(i);
				if (!stack.isEmpty()
						&& stack.getItem() instanceof IStepAssister
						&& ((IStepAssister) stack.getItem()).canAssistStep(stack, player))
				{
					return true;
				}
			}
		}

		return false;
	}

	private boolean hasSwrg()
	{
		for (int i = 0; i <= 8; i++)
		{
			if (!player.inventory.mainInventory.get(i).isEmpty() && player.inventory.mainInventory.get(i).getItem() == ObjHandler.swrg)
			{
				return true;
			}
		}

		IItemHandler baubles = PlayerHelper.getBaubles(player);
		if (baubles != null)
		{
			for (int i = 0; i < baubles.getSlots(); i++)
			{
				if (!baubles.getStackInSlot(i).isEmpty() && baubles.getStackInSlot(i).getItem() == ObjHandler.swrg)
				{
					return true;
				}
			}
		}
		return false;
	}

	public void enableSwrgFlightOverride()
	{
		swrgOverride = true;
	}

	public void disableSwrgFlightOverride()
	{
		swrgOverride = false;
	}

	public static class Provider implements ICapabilityProvider
	{
		private final InternalAbilities capInstance;

		public Provider(EntityPlayerMP player)
		{
			capInstance = new InternalAbilities(player);
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
		{
			return capability == CAPABILITY;
		}

		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
		{
			if (capability == CAPABILITY)
				return CAPABILITY.cast(capInstance);
			else return null;
		}
	}
}
