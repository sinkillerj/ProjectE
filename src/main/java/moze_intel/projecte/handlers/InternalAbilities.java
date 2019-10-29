package moze_intel.projecte.handlers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public final class InternalAbilities {

	@CapabilityInject(InternalAbilities.class)
	public static Capability<InternalAbilities> CAPABILITY = null;
	public static final ResourceLocation NAME = new ResourceLocation(PECore.MODID, "internal_abilities");

	private final ServerPlayerEntity player;
	private boolean swrgOverride = false;
	private boolean gemArmorReady = false;
	private boolean hadFlightItem = false;
	private boolean wasFlyingGamemode = false;
	private boolean isFlyingGamemode = false;
	private boolean wasFlying = false;
	private int projectileCooldown = 0;
	private int gemChestCooldown = 0;

	public InternalAbilities(ServerPlayerEntity player) {
		this.player = player;
	}

	public void resetProjectileCooldown() {
		projectileCooldown = ProjectEConfig.misc.projectileCooldown.get();
	}

	public int getProjectileCooldown() {
		return projectileCooldown;
	}

	public void resetGemCooldown() {
		gemChestCooldown = ProjectEConfig.misc.gemChestCooldown.get();
	}

	public int getGemCooldown() {
		return gemChestCooldown;
	}

	public void setGemState(boolean state) {
		gemArmorReady = state;
	}

	public boolean getGemState() {
		return gemArmorReady;
	}

	// Checks if the server state of player capas mismatches with what ProjectE determines. If so, change it serverside and send a packet to client
	public void tick() {
		if (projectileCooldown > 0) {
			projectileCooldown--;
		}

		if (gemChestCooldown > 0) {
			gemChestCooldown--;
		}

		if (!shouldPlayerFly()) {
			if (hadFlightItem) {
				if (player.abilities.allowFlying) {
					PlayerHelper.updateClientServerFlight(player, false);
				}

				hadFlightItem = false;
			}
			wasFlyingGamemode = false;
			wasFlying = false;
		} else {
			if (!hadFlightItem) {
				if (!player.abilities.allowFlying) {
					PlayerHelper.updateClientServerFlight(player, true);
				}

				hadFlightItem = true;
			} else if (wasFlyingGamemode && !isFlyingGamemode) {
				//Player was in a gamemode that allowed flight, but no longer is but they still should be allowed to fly
				//Sync the fact to the client. Also passes wasFlying so that if they were flying previously,
				//and are still allowed to the gamemode change doesn't force them out of it
				PlayerHelper.updateClientServerFlight(player, true, wasFlying);
			}
			wasFlyingGamemode = isFlyingGamemode;
			wasFlying = player.abilities.isFlying;
		}

		if (!shouldPlayerStep()) {
			if (player.stepHeight > 0.6F) {
				PlayerHelper.updateClientServerStepHeight(player, 0.6F);
			}
		} else {
			if (player.stepHeight < 1.0F) {
				PlayerHelper.updateClientServerStepHeight(player, 1.0F);
			}
		}
	}

	public void onDimensionChange() {
		// Resend everything needed on clientside (all except fire resist)
		PlayerHelper.updateClientServerFlight(player, player.abilities.allowFlying);
		PlayerHelper.updateClientServerStepHeight(player, shouldPlayerStep() ? 1.0F : 0.6F);
	}

	private boolean shouldPlayerFly() {
		if (!hasSwrg()) {
			disableSwrgFlightOverride();
		}

		isFlyingGamemode = player.abilities.isCreativeMode || player.isSpectator();
		if (isFlyingGamemode || swrgOverride) {
			return true;
		}

		for (ItemStack stack : player.inventory.armorInventory) {
			if (!stack.isEmpty() && stack.getItem() instanceof IFlightProvider && ((IFlightProvider) stack.getItem()).canProvideFlight(stack, player)) {
				return true;
			}
		}

		for (int i = 0; i <= 8; i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (!stack.isEmpty() && stack.getItem() instanceof IFlightProvider && ((IFlightProvider) stack.getItem()).canProvideFlight(stack, player)) {
				return true;
			}
		}

		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				ItemStack stack = curios.getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() instanceof IFlightProvider && ((IFlightProvider) stack.getItem()).canProvideFlight(stack, player)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean shouldPlayerStep() {
		for (ItemStack stack : player.inventory.armorInventory) {
			if (!stack.isEmpty() && stack.getItem() instanceof IStepAssister && ((IStepAssister) stack.getItem()).canAssistStep(stack, player)) {
				return true;
			}
		}

		for (int i = 0; i <= 8; i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (!stack.isEmpty() && stack.getItem() instanceof IStepAssister && ((IStepAssister) stack.getItem()).canAssistStep(stack, player)) {
				return true;
			}
		}

		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				ItemStack stack = curios.getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() instanceof IStepAssister && ((IStepAssister) stack.getItem()).canAssistStep(stack, player)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean hasSwrg() {
		for (int i = 0; i <= 8; i++) {
			if (!player.inventory.mainInventory.get(i).isEmpty() && player.inventory.mainInventory.get(i).getItem() == ObjHandler.swrg) {
				return true;
			}
		}

		IItemHandler curios = PlayerHelper.getCurios(player);
		if (curios != null) {
			for (int i = 0; i < curios.getSlots(); i++) {
				if (!curios.getStackInSlot(i).isEmpty() && curios.getStackInSlot(i).getItem() == ObjHandler.swrg) {
					return true;
				}
			}
		}
		return false;
	}

	public void enableSwrgFlightOverride() {
		swrgOverride = true;
	}

	public void disableSwrgFlightOverride() {
		swrgOverride = false;
	}

	public static class Provider implements ICapabilityProvider {

		private final LazyOptional<InternalAbilities> capInstance;

		public Provider(ServerPlayerEntity player) {
			capInstance = LazyOptional.of(() -> new InternalAbilities(player));
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
			if (capability == CAPABILITY) {
				return capInstance.cast();
			}
			return LazyOptional.empty();
		}
	}
}