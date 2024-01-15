package moze_intel.projecte.handlers;

import java.util.UUID;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.IStepAssister;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

//TODO - 1.20.4: Validate this works properly given we don't persist it and don't sync it (and it resets on death)
public final class InternalAbilities {

	private static final UUID STEP_ASSIST_MODIFIER_UUID = UUID.fromString("4726C09D-FD86-46D0-92DD-49ED952A12D2");
	private static final AttributeModifier STEP_ASSIST = new AttributeModifier(STEP_ASSIST_MODIFIER_UUID, "Step Assist", 0.4, Operation.ADDITION);

	private boolean swrgOverride = false;
	private boolean gemArmorReady = false;
	private boolean hadFlightItem = false;
	private boolean wasFlyingGamemode = false;
	private boolean isFlyingGamemode = false;
	private boolean wasFlying = false;
	private int projectileCooldown = 0;
	private int gemChestCooldown = 0;

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
	public void tick(Player player) {
		if (projectileCooldown > 0) {
			projectileCooldown--;
		}

		if (gemChestCooldown > 0) {
			gemChestCooldown--;
		}

		if (!shouldPlayerFly(player)) {
			if (hadFlightItem) {
				if (player.getAbilities().mayfly) {
					updateClientServerFlight(player, false);
				}
				hadFlightItem = false;
			}
			wasFlyingGamemode = false;
			wasFlying = false;
		} else {
			if (!hadFlightItem) {
				if (!player.getAbilities().mayfly) {
					updateClientServerFlight(player, true);
				}
				hadFlightItem = true;
			} else if (wasFlyingGamemode && !isFlyingGamemode) {
				//Player was in a gamemode that allowed flight, but no longer is, but they still should be allowed to fly
				//Sync the fact to the client. Also passes wasFlying so that if they were flying previously,
				//and are still allowed to the gamemode change doesn't force them out of it
				updateClientServerFlight(player, true, wasFlying);
			}
			wasFlyingGamemode = isFlyingGamemode;
			wasFlying = player.getAbilities().flying;
		}

		AttributeInstance attributeInstance = player.getAttribute(NeoForgeMod.STEP_HEIGHT.value());
		if (attributeInstance != null) {
			AttributeModifier existing = attributeInstance.getModifier(STEP_ASSIST_MODIFIER_UUID);
			if (shouldPlayerStep(player)) {
				if (existing == null) {
					//Should step but doesn't have the modifier yet, add it
					attributeInstance.addTransientModifier(STEP_ASSIST);
				}
			} else if (existing != null) {
				//Shouldn't step but has modifier, remove it
				attributeInstance.removeModifier(existing.getId());
			}
		}
	}

	public void onDimensionChange(Player player) {
		// Resend everything needed on clientside (all except fire resist)
		updateClientServerFlight(player, player.getAbilities().mayfly);
	}

	private boolean shouldPlayerFly(Player player) {
		if (!hasSwrg(player)) {
			disableSwrgFlightOverride();
		}
		isFlyingGamemode = player.isCreative() || player.isSpectator();
		if (isFlyingGamemode || swrgOverride) {
			return true;
		}
		return PlayerHelper.checkArmorHotbarCurios(player, stack -> !stack.isEmpty() && stack.getItem() instanceof IFlightProvider provider && provider.canProvideFlight(stack, player));
	}

	private boolean shouldPlayerStep(Player player) {
		return PlayerHelper.checkArmorHotbarCurios(player, stack -> !stack.isEmpty() && stack.getItem() instanceof IStepAssister assister && assister.canAssistStep(stack, player));
	}

	private boolean hasSwrg(Player player) {
		return PlayerHelper.checkHotbarCurios(player, stack -> !stack.isEmpty() && stack.getItem() == PEItems.SWIFTWOLF_RENDING_GALE.get());
	}

	public void enableSwrgFlightOverride() {
		swrgOverride = true;
	}

	public void disableSwrgFlightOverride() {
		swrgOverride = false;
	}

	private void updateClientServerFlight(Player player, boolean allowFlying) {
		updateClientServerFlight(player, allowFlying, allowFlying && player.getAbilities().flying);
	}

	private void updateClientServerFlight(Player player, boolean allowFlying, boolean isFlying) {
		player.getAbilities().mayfly = allowFlying;
		player.getAbilities().flying = isFlying;
		player.onUpdateAbilities();
	}
}