package moze_intel.projecte.handlers;

import java.util.UUID;
import moze_intel.projecte.PECore;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.NotNull;

public final class InternalAbilities {

	private static final UUID STEP_ASSIST_MODIFIER_UUID = UUID.fromString("4726C09D-FD86-46D0-92DD-49ED952A12D2");
	private static final AttributeModifier STEP_ASSIST = new AttributeModifier(STEP_ASSIST_MODIFIER_UUID, "Step Assist", 0.4, Operation.ADDITION);
	public static final Capability<InternalAbilities> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	public static final ResourceLocation NAME = PECore.rl("internal_abilities");

	private final ServerPlayer player;
	private boolean swrgOverride = false;
	private boolean gemArmorReady = false;
	private boolean hadFlightItem = false;
	private boolean wasFlyingGamemode = false;
	private boolean isFlyingGamemode = false;
	private boolean wasFlying = false;
	private int projectileCooldown = 0;
	private int gemChestCooldown = 0;

	public InternalAbilities(ServerPlayer player) {
		this.player = player;
	}

	public void resetProjectileCooldown() {
		projectileCooldown = ProjectEConfig.server.cooldown.player.projectile.get();
	}

	public int getProjectileCooldown() {
		return projectileCooldown;
	}

	public void resetGemCooldown() {
		gemChestCooldown = ProjectEConfig.server.cooldown.player.gemChest.get();
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

	// Checks if the server state of player caps mismatches with what ProjectE determines. If so, change it serverside and send a packet to client
	public void tick() {
		if (projectileCooldown > 0) {
			projectileCooldown--;
		}

		if (gemChestCooldown > 0) {
			gemChestCooldown--;
		}

		if (!shouldPlayerFly()) {
			if (hadFlightItem) {
				if (player.getAbilities().mayfly) {
					PlayerHelper.updateClientServerFlight(player, false);
				}
				hadFlightItem = false;
			}
			wasFlyingGamemode = false;
			wasFlying = false;
		} else {
			if (!hadFlightItem) {
				if (!player.getAbilities().mayfly) {
					PlayerHelper.updateClientServerFlight(player, true);
				}
				hadFlightItem = true;
			} else if (wasFlyingGamemode && !isFlyingGamemode) {
				//Player was in a gamemode that allowed flight, but no longer is, but they still should be allowed to fly
				//Sync the fact to the client. Also passes wasFlying so that if they were flying previously,
				//and are still allowed to the gamemode change doesn't force them out of it
				PlayerHelper.updateClientServerFlight(player, true, wasFlying);
			}
			wasFlyingGamemode = isFlyingGamemode;
			wasFlying = player.getAbilities().flying;
		}

		AttributeInstance attributeInstance = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
		if (attributeInstance != null) {
			AttributeModifier existing = attributeInstance.getModifier(STEP_ASSIST_MODIFIER_UUID);
			if (shouldPlayerStep()) {
				if (existing == null) {
					//Should step but doesn't have the modifier yet, add it
					attributeInstance.addTransientModifier(STEP_ASSIST);
				}
			} else if (existing != null) {
				//Shouldn't step but has modifier, remove it
				attributeInstance.removeModifier(existing);
			}
		}
	}

	public void onDimensionChange() {
		// Resend everything needed on clientside (all except fire resist)
		PlayerHelper.updateClientServerFlight(player, player.getAbilities().mayfly);
	}

	private boolean shouldPlayerFly() {
		if (!hasSwrg()) {
			disableSwrgFlightOverride();
		}
		isFlyingGamemode = player.isCreative() || player.isSpectator();
		if (isFlyingGamemode || swrgOverride) {
			return true;
		}
		return PlayerHelper.checkArmorHotbarCurios(player, stack -> !stack.isEmpty() && stack.getItem() instanceof IFlightProvider provider && provider.canProvideFlight(stack, player));
	}

	private boolean shouldPlayerStep() {
		return PlayerHelper.checkArmorHotbarCurios(player, stack -> !stack.isEmpty() && stack.getItem() instanceof IStepAssister assister && assister.canAssistStep(stack, player));
	}

	private boolean hasSwrg() {
		return PlayerHelper.checkHotbarCurios(player, stack -> !stack.isEmpty() && stack.getItem() == PEItems.SWIFTWOLF_RENDING_GALE.get());
	}

	public void enableSwrgFlightOverride() {
		swrgOverride = true;
	}

	public void disableSwrgFlightOverride() {
		swrgOverride = false;
	}

	public static class Provider extends BasicCapabilityResolver<InternalAbilities> {

		public Provider(ServerPlayer player) {
			super(() -> new InternalAbilities(player));
		}

		@NotNull
		@Override
		public Capability<InternalAbilities> getMatchingCapability() {
			return CAPABILITY;
		}
	}
}