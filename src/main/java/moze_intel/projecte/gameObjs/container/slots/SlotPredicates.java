package moze_intel.projecte.gameObjs.container.slots;

import java.util.function.Predicate;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;

public final class SlotPredicates {

	public static final Predicate<ItemStack> ALWAYS_FALSE = input -> false;

	public static final Predicate<ItemStack> HAS_EMC = EMCHelper::doesItemHaveEmc;

	public static final Predicate<ItemStack> COLLECTOR_LOCK = FuelMapper::isStackFuel;

	public static final Predicate<ItemStack> COLLECTOR_INV = input -> input.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY) != null ||
																	  (FuelMapper.isStackFuel(input) && !FuelMapper.isStackMaxFuel(input));

	// slotrelayklein, slotmercurialklein
	public static final Predicate<ItemStack> EMC_HOLDER = input -> input.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY) != null;

	// slotrelayinput
	public static final Predicate<ItemStack> RELAY_INV = input -> EMC_HOLDER.test(input) || HAS_EMC.test(input);

	public static final Predicate<ItemStack> FURNACE_FUEL = input -> EMC_HOLDER.test(input) || CommonHooks.getBurnTime(input, RecipeType.SMELTING) > 0;

	public static final Predicate<ItemStack> MERCURIAL_TARGET = input -> {
		if (input.isEmpty()) {
			return false;
		}
		BlockState state = ItemHelper.stackToState(input, null);
		return state != null && !state.hasBlockEntity() && EMCHelper.doesItemHaveEmc(input);
	};

	private SlotPredicates() {
	}
}