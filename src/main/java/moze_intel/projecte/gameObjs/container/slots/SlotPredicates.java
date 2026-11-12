package moze_intel.projecte.gameObjs.container.slots;

import java.util.function.Predicate;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class SlotPredicates {

	public static final Predicate<ItemStack> ALWAYS_FALSE = input -> false;

	public static final Predicate<ItemStack> HAS_EMC = IEMCProxy.INSTANCE::hasValue;

	public static final Predicate<ItemStack> COLLECTOR_LOCK = FuelMapper::isStackFuel;

	public static final Predicate<ItemStack> COLLECTOR_INV = input -> input.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY) != null ||
																	  (FuelMapper.isStackFuel(input) && !FuelMapper.isStackMaxFuel(input));

	public static final Predicate<ItemStack> EMC_HOLDER = input -> input.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY) != null;

	public static final Predicate<ItemStack> RELAY_INV = input -> EMC_HOLDER.test(input) || HAS_EMC.test(input);

	public static final Predicate<ItemStack> FURNACE_FUEL = input -> EMC_HOLDER.test(input) || FuelMapper.isStackFuel(input);

	public static final Predicate<ItemStack> MERCURIAL_TARGET = input -> {
		if (input.isEmpty()) {
			return false;
		}
		BlockState state = ItemHelper.stackToState(input, null);
		return state != null && !state.hasBlockEntity() && IEMCProxy.INSTANCE.hasValue(input);
	};

	private SlotPredicates() {
	}
}
