package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.arithmetics.LongArithmetic;
import moze_intel.projecte.utils.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FluidMapper implements IEMCMapper<NormalizedSimpleStack, Long> {
	private IMappingCollector<NormalizedSimpleStack, Long> mapper;

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper) {
		mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(FluidRegistry.WATER), Long.MIN_VALUE/*=Free. TODO: Use LongArithmetic*/, IMappingCollector.FixedValue.FixAndInherit);
		mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(FluidRegistry.LAVA), 64L, IMappingCollector.FixedValue.FixAndInherit);
		Map<String, Long> fixValue = new HashMap<String, Long>();
		fixValue.put("milk", 16L);
		for (Map.Entry<String, Long> entry : fixValue.entrySet()) {
			Fluid f = FluidRegistry.getFluid(entry.getKey());
			if (f != null) {
				mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(f), entry.getValue(), IMappingCollector.FixedValue.FixAndInherit);
			}
		}

		Map<String, NormalizedSimpleStack> molten = new HashMap<String, NormalizedSimpleStack>();
		molten.put("obsidian.molten", NormalizedSimpleStack.getNormalizedSimpleStackFor(Blocks.obsidian));
		molten.put("glass.molten", NormalizedSimpleStack.getNormalizedSimpleStackFor(Blocks.glass));
		molten.put("ender", NormalizedSimpleStack.getNormalizedSimpleStackFor(Items.ender_pearl));

		for (Map.Entry<String, NormalizedSimpleStack> entry : molten.entrySet()) {
			Fluid f = FluidRegistry.getFluid(entry.getKey());
			if (f != null) {
				mapper.addConversion(1, NormalizedSimpleStack.getNormalizedSimpleStackFor(f), Arrays.asList(entry.getValue()));
			}
		}

		for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			Fluid fluid = data.fluid.getFluid();
			mapper.addConversion(1, NormalizedSimpleStack.getNormalizedSimpleStackFor(data.filledContainer), Arrays.asList(NormalizedSimpleStack.getNormalizedSimpleStackFor(data.emptyContainer), NormalizedSimpleStack.getNormalizedSimpleStackFor(fluid)));
		}
	}
}
