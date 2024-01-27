package moze_intel.projecte.handlers;

import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
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
			int x = Mth.floor(player.getX());
			//TODO - 1.20.4: FIGURE THIS OUT and why we even use the riding offset
			//int y = (int) (player.getY() - player.getMyRidingOffset());
			int y = (int) (player.getY() - (player.getVehicle() == null ? 0 : player.getMyRidingOffset(player.getVehicle())));
			int z = Mth.floor(player.getZ());
			BlockPos pos = new BlockPos(x, y, z);
			FluidState below = player.level().getFluidState(pos.below());
			boolean water = waterWalkOnType.canWalk() && below.is(FluidTags.WATER);
			//Note: Technically we could probably only have lava be true if water is false, but given the
			// fact vanilla uses tags for logic and technically (although it probably would cause lots of
			// weirdness, the block we are standing on could be both water and lava, which would mean that
			// we would want to apply both speed boosts).
			boolean lava = lavaWalkOnType.canWalk() && below.is(FluidTags.LAVA);
			if ((water || lava) && player.level().isEmptyBlock(pos)) {
				if (!player.isShiftKeyDown()) {
					player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
					player.fallDistance = 0.0F;
					player.setOnGround(true);
				}
				applyWaterSpeed = water && waterWalkOnType == WalkOnType.ABLE_WITH_SPEED;
				applyLavaSpeed = lava && lavaWalkOnType == WalkOnType.ABLE_WITH_SPEED;
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
		if (PlayerHelper.checkHotbarCurios(player, stack -> !stack.isEmpty() && stack.getItem() == PEItems.EVERTIDE_AMULET.get())) {
			return WalkOnType.ABLE_WITH_SPEED;
		}
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		return !helmet.isEmpty() && helmet.getItem() == PEItems.GEM_HELMET.get() ? WalkOnType.ABLE : WalkOnType.UNABLE;
	}

	private WalkOnType canWalkOnLava(Player player) {
		if (PlayerHelper.checkHotbarCurios(player, stack -> !stack.isEmpty() && stack.getItem() == PEItems.VOLCANITE_AMULET.get())) {
			return WalkOnType.ABLE_WITH_SPEED;
		}
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		return !chestplate.isEmpty() && chestplate.getItem() == PEItems.GEM_CHESTPLATE.get() ? WalkOnType.ABLE : WalkOnType.UNABLE;
	}

	private enum WalkOnType {
		ABLE,
		ABLE_WITH_SPEED,
		UNABLE;

		public boolean canWalk() {
			return this != UNABLE;
		}
	}
}