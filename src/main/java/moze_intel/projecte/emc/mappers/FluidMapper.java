package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidMapper implements IEMCMapper<NormalizedSimpleStack, Integer> {
	private IMappingCollector<NormalizedSimpleStack, Long> mapper;

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(FluidRegistry.WATER), Integer.MIN_VALUE/*=Free. TODO: Use IntArithmetic*/, IMappingCollector.FixedValue.FixAndInherit);
		mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(FluidRegistry.LAVA), 64, IMappingCollector.FixedValue.FixAndInherit);
		Map<String, Integer> fixValue = new HashMap<String, Integer>();
		fixValue.put("milk", 16);
		for (Map.Entry<String, Integer> entry : fixValue.entrySet()) {
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

			List<ItemStack> odItems = getODEntriesForFluid(handleFluidName(data.fluid.getFluid()));
			for (ItemStack itemStack:odItems) {
				mapper.addConversion(1,NormalizedSimpleStack.getNormalizedSimpleStackFor(fluid), Arrays.asList(NormalizedSimpleStack.getNormalizedSimpleStackFor(itemStack)));
			}
		}
	}

	@Override
	public String getName() {
		return "FluidMapper";
	}

	@Override
	public String getDescription() {
		return "Adds Conversions for fluid container items and fluids.";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	private static String handleFluidName(Fluid fluid)
	{
		String name = fluid.getName();

		if (name.endsWith(".molten"))
		{
			name = name.substring(0, name.indexOf(".molten"));
		}
		else if (name.endsWith(".liquid"))
		{
			name = name.substring(0, name.indexOf(".liquid"));
		}

		return name;
	}

	private static List<ItemStack> getODEntriesForFluid(String name)
	{
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1);

		List<ItemStack> list = Utils.getODItems("ingot" + name);

		if (list.isEmpty())
		{
			list = Utils.getODItems("dust" + name);

			if (list.isEmpty())
			{
				list = Utils.getODItems("gem" + name);
			}
		}

		return list;
	}
}
