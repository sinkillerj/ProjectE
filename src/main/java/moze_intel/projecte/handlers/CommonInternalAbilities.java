package moze_intel.projecte.handlers;

import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;

public class CommonInternalAbilities {

	private static final AttributeModifier WATER_SPEED_BOOST = new AttributeModifier("Walk on water speed boost", 0.15, Operation.ADDITION);
	private static final AttributeModifier LAVA_SPEED_BOOST = new AttributeModifier("Walk on lava speed boost", 0.15, Operation.ADDITION);

	public void tick(Player player) {
		boolean applyWaterSpeed = false;
		boolean applyLavaSpeed = false;
		WalkOnType waterWalkOnType = canWalkOnWater(player);
		WalkOnType lavaWalkOnType = canWalkOnLava(player);
		if (waterWalkOnType.canWalk() || lavaWalkOnType.canWalk()) {
			FluidState below = player.level().getFluidState(player.blockPosition().below());
			boolean water = waterWalkOnType.canWalk() && below.is(FluidTags.WATER);
			//Note: Technically we could probably only have lava be true if water is false, but given the
			// fact vanilla uses tags for logic and technically (although it probably would cause lots of
			// weirdness, the block we are standing on could be both water and lava, which would mean that
			// we would want to apply both speed boosts).
			boolean lava = lavaWalkOnType.canWalk() && below.is(FluidTags.LAVA);
			if ((water || lava) && player.getFeetBlockState().isAir()) {
				if (!player.isShiftKeyDown()) {
					player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
					player.fallDistance = 0.0F;
					player.setOnGround(true);
				}
				applyWaterSpeed = waterWalkOnType.applySpeed(water);
				applyLavaSpeed = lavaWalkOnType.applySpeed(lava);
			} else if (!player.level().isClientSide) {
				if (waterWalkOnType.canWalk() && player.isInWater()) {
					//Things that apply water walking also refresh air supply when in water
					player.setAirSupply(player.getMaxAirSupply());
				}
			}
		}
		if (!player.level().isClientSide) {
			AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
			if (attribute != null) {
				updateSpeed(attribute, applyWaterSpeed, WATER_SPEED_BOOST);
				updateSpeed(attribute, applyLavaSpeed, LAVA_SPEED_BOOST);
			}
		}
	}

	private void updateSpeed(AttributeInstance attribute, boolean apply, AttributeModifier speedModifier) {
		if (apply) {
			if (!attribute.hasModifier(speedModifier)) {
				attribute.addTransientModifier(speedModifier);
			}
		} else if (attribute.hasModifier(speedModifier)) {
			attribute.removeModifier(speedModifier.getId());
		}
	}

	private WalkOnType canWalkOnWater(Player player) {
		if (PlayerHelper.checkHotbarCurios(player, stack -> !stack.isEmpty() && stack.is(PEItems.EVERTIDE_AMULET))) {
			return WalkOnType.ABLE_WITH_SPEED;
		}
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		return !helmet.isEmpty() && helmet.is(PEItems.GEM_HELMET) ? WalkOnType.ABLE : WalkOnType.UNABLE;
	}

	private WalkOnType canWalkOnLava(Player player) {
		if (PlayerHelper.checkHotbarCurios(player, stack -> !stack.isEmpty() && stack.is(PEItems.VOLCANITE_AMULET))) {
			return WalkOnType.ABLE_WITH_SPEED;
		}
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		return !chestplate.isEmpty() && chestplate.is(PEItems.GEM_CHESTPLATE) ? WalkOnType.ABLE : WalkOnType.UNABLE;
	}

	private enum WalkOnType {
		ABLE,
		ABLE_WITH_SPEED,
		UNABLE;

		public boolean canWalk() {
			return this != UNABLE;
		}

		public boolean applySpeed(boolean onType) {
			return onType && this == ABLE_WITH_SPEED;
		}
	}
}