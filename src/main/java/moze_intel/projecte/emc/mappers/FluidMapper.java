package moze_intel.projecte.emc.mappers;

import com.google.common.collect.Maps;
import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FluidMapper implements IEMCMapper<NormalizedSimpleStack, Integer> {
	private IMappingCollector<NormalizedSimpleStack, Long> mapper;

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		mapper.setValueBefore(NormalizedSimpleStack.getFor(FluidRegistry.WATER), Integer.MIN_VALUE/*=Free. TODO: Use IntArithmetic*/);
		mapper.setValueBefore(NormalizedSimpleStack.getFor(FluidRegistry.LAVA), 64);
		Map<String, Integer> fixValue = Maps.newHashMap();
		fixValue.put("milk", 16);
		for (Map.Entry<String, Integer> entry : fixValue.entrySet()) {
			Fluid f = FluidRegistry.getFluid(entry.getKey());
			if (f != null) {
				mapper.setValueBefore(NormalizedSimpleStack.getFor(f), entry.getValue());
			}
		}

		Map<String, NormalizedSimpleStack> molten = Maps.newHashMap();
		molten.put("obsidian.molten", NormalizedSimpleStack.getFor(Blocks.obsidian));
		molten.put("glass.molten", NormalizedSimpleStack.getFor(Blocks.glass));
		molten.put("ender", NormalizedSimpleStack.getFor(Items.ender_pearl));

		for (Map.Entry<String, NormalizedSimpleStack> entry : molten.entrySet()) {
			Fluid f = FluidRegistry.getFluid(entry.getKey());
			if (f != null) {
				mapper.addConversion(1, NormalizedSimpleStack.getFor(f), Arrays.asList(entry.getValue()));
			}
		}

		for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
			Fluid fluid = data.fluid.getFluid();
			mapper.addConversion(1, NormalizedSimpleStack.getFor(data.filledContainer), Arrays.asList(NormalizedSimpleStack.getFor(data.emptyContainer), NormalizedSimpleStack.getFor(fluid)));

			List<ItemStack> odItems = getODEntriesForFluid(handleFluidName(data.fluid.getFluid()));
			for (ItemStack itemStack:odItems) {
				mapper.addConversion(1,NormalizedSimpleStack.getFor(fluid), Arrays.asList(NormalizedSimpleStack.getFor(itemStack)));
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

		List<ItemStack> list = ItemHelper.getODItems("ingot" + name);

		if (list.isEmpty())
		{
			list = ItemHelper.getODItems("dust" + name);

			if (list.isEmpty())
			{
				list = ItemHelper.getODItems("gem" + name);
			}
		}

		return list;
	}
}
