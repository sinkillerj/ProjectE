package moze_intel.projecte.handlers;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.items.IItemHandler;

public class InternalAbilities {

	private static final AttributeModifier FLIGHT = new AttributeModifier(PECore.rl("flight"), 1, Operation.ADD_VALUE);
	private static final AttributeModifier WATER_SPEED_BOOST = new AttributeModifier(PECore.rl("water_speed_boost"), 0.15, Operation.ADD_VALUE);
	private static final AttributeModifier LAVA_SPEED_BOOST = new AttributeModifier(PECore.rl("lava_speed_boost"), 0.15, Operation.ADD_VALUE);

	public static void tick(Player player) {
		Predicate<Player> applyWaterSpeed = Predicates.alwaysFalse();
		Predicate<Player> applyLavaSpeed = Predicates.alwaysFalse();
		//Single inventory scan to detect all ability items at once (instead of 4 separate hotbar/curios passes)
		boolean hasEvertide = false;
		boolean hasVolcanite = false;
		boolean hasArcana = false;
		ItemStack swrgStack = ItemStack.EMPTY;
		for (int i = 0; i < Inventory.getSelectionSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.is(PEItems.EVERTIDE_AMULET)) {
				hasEvertide = true;
			} else if (stack.is(PEItems.VOLCANITE_AMULET)) {
				hasVolcanite = true;
			} else if (stack.is(PEItems.ARCANA_RING)) {
				hasArcana = true;
			} else if (swrgStack.isEmpty() && stack.is(PEItems.SWIFTWOLF_RENDING_GALE)) {
				swrgStack = stack;
			}
		}
		ItemStack offhand = player.getOffhandItem();
		if (!offhand.isEmpty()) {
			if (offhand.is(PEItems.EVERTIDE_AMULET)) {
				hasEvertide = true;
			} else if (offhand.is(PEItems.VOLCANITE_AMULET)) {
				hasVolcanite = true;
			} else if (swrgStack.isEmpty() && offhand.is(PEItems.SWIFTWOLF_RENDING_GALE)) {
				swrgStack = offhand;
			}
		}
		//Single curios capability query for the whole tick (cached instead of re-querying per check)
		IItemHandler curios = player.getCapability(IntegrationHelper.CURIO_ITEM_HANDLER);
		if (curios != null) {
			for (int i = 0, slots = curios.getSlots(); i < slots; i++) {
				ItemStack stack = curios.getStackInSlot(i);
				if (stack.isEmpty()) {
					continue;
				}
				if (stack.is(PEItems.EVERTIDE_AMULET)) {
					hasEvertide = true;
				} else if (stack.is(PEItems.VOLCANITE_AMULET)) {
					hasVolcanite = true;
				} else if (swrgStack.isEmpty() && stack.is(PEItems.SWIFTWOLF_RENDING_GALE)) {
					swrgStack = stack;
				}
			}
		}
		ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
		boolean hasGemHelmet = !helmet.isEmpty() && helmet.is(PEItems.GEM_HELMET);
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		boolean hasGemChestplate = !chestplate.isEmpty() && chestplate.is(PEItems.GEM_CHESTPLATE);

		WalkOnType waterWalkOnType = hasEvertide ? WalkOnType.ABLE_WITH_SPEED : (hasGemHelmet ? WalkOnType.ABLE : WalkOnType.UNABLE);
		WalkOnType lavaWalkOnType = hasVolcanite ? WalkOnType.ABLE_WITH_SPEED : (hasGemChestplate ? WalkOnType.ABLE : WalkOnType.UNABLE);
		if (waterWalkOnType.canWalk() || lavaWalkOnType.canWalk()) {
			FluidState below = player.level().getFluidState(player.getOnPos());
			boolean water = waterWalkOnType.canWalk() && below.is(FluidTags.WATER);
			//Note: Technically we could probably only have lava be true if water is false, but given the
			// fact vanilla uses tags for logic and technically (although it probably would cause lots of
			// weirdness, the block we are standing on could be both water and lava, which would mean that
			// we would want to apply both speed boosts).
			boolean lava = lavaWalkOnType.canWalk() && below.is(FluidTags.LAVA);
			if ((water || lava) && player.getInBlockState().isAir()) {
				if (!player.isShiftKeyDown()) {
					player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1));
					player.resetFallDistance();
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
			updateAttribute(player, Attributes.MOVEMENT_SPEED, WATER_SPEED_BOOST, applyWaterSpeed);
			updateAttribute(player, Attributes.MOVEMENT_SPEED, LAVA_SPEED_BOOST, applyLavaSpeed);
			//Note: Curios, and the offhand are handled by the attribute on the arcana ring. We want it to provide flight in other slots on the hotbar as well
			// so we have to do it here. We do this rather than only doing a hotbar curios check with no attribute, so that the tooltip shows it provides flight
			boolean shouldFly = (!swrgStack.isEmpty() && ItemPE.hasEmc(player, swrgStack, 64, true)) || hasArcana;
			updateAttribute(player, NeoForgeMod.CREATIVE_FLIGHT, FLIGHT, shouldFly ? Predicates.alwaysTrue() : Predicates.alwaysFalse());
		}
	}

	//Note: The attributes that currently use this cannot be converted to just being attributes on the items, as they can be disabled based on the player state
	private static void updateAttribute(Player player, Holder<Attribute> attribute, AttributeModifier modifier, Predicate<Player> applyAttribute) {
		AttributeInstance attributeInstance = player.getAttribute(attribute);
		if (attributeInstance != null) {
			boolean hasModifier = attributeInstance.hasModifier(modifier.id());
			if (applyAttribute.test(player)) {
				if (!hasModifier) {
					//Should have it, but doesn't have the modifier yet, add it
					attributeInstance.addTransientModifier(modifier);
				}
			} else if (hasModifier) {
				//Shouldn't have the modifier, remove it
				attributeInstance.removeModifier(modifier.id());
			}
		}
	}

	private enum WalkOnType {
		ABLE,
		ABLE_WITH_SPEED,
		UNABLE;

		public boolean canWalk() {
			return this != UNABLE;
		}

		public Predicate<Player> applySpeed(boolean onType) {
			//Note: We do this instead of wrapping it to avoid the capturing lambdas
			if (onType && this == ABLE_WITH_SPEED) {
				return Predicates.alwaysTrue();
			}
			return Predicates.alwaysFalse();
		}
	}
}