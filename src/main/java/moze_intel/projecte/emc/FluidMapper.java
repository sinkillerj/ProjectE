package moze_intel.projecte.emc;

import moze_intel.projecte.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.LinkedHashMap;
import java.util.List;

public final class FluidMapper
{
	//Will use later for fluid transmutation.
	private static final LinkedHashMap<Fluid, Integer> MAP = new LinkedHashMap<Fluid, Integer>();

	public static void map()
	{
		lazyInit();

		for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData())
		{
			if (data.emptyContainer == null || data.filledContainer == null || data.fluid == null)
			{
				continue;
			}

			Fluid fluid = data.fluid.getFluid();

			if (!isFluidValid(fluid) || !Utils.doesItemHaveEmc(data.emptyContainer))
			{
				continue;
			}

			if (MAP.containsKey(fluid))
			{
				EMCMapper.addMapping(data.filledContainer, Utils.getEmcValue(data.emptyContainer) + MAP.get(fluid));
				continue;
			}

			List<ItemStack> odItems = getODEntriesForFluid(handleFluidName(data.fluid.getFluid()));

			int minEmc = -1;

			for (ItemStack stack : odItems)
			{
				if (!Utils.doesItemHaveEmc(stack))
				{
					continue;
				}

				int emc = Utils.getEmcValue(stack);

				if (minEmc == -1 || emc < minEmc)
				{
					minEmc = emc;
				}
			}

			if (minEmc != -1)
			{
				addFluidEMC(fluid, minEmc);
				EMCMapper.addMapping(data.filledContainer, Utils.getEmcValue(data.emptyContainer) + minEmc);
			}
		}
	}

	public static boolean doesFluidHaveEMC(Block block)
	{
		Fluid fluid = FluidRegistry.lookupFluidForBlock(block);

		if (fluid != null)
		{
			return MAP.containsKey(fluid);
		}

		return false;
	}

	//Should always check if the block has EMC before-hand, or will cause an NPE
	public static int getFluidEMC(Block block)
	{
		return MAP.get(FluidRegistry.lookupFluidForBlock(block));
	}

	public static void addFluidEMC(Fluid fluid, int emcValue)
	{
		if (!MAP.containsKey(fluid) && emcValue > 0)
		{
			MAP.put(fluid, emcValue);
		}
	}

	private static void lazyInit()
	{
		MAP.put(FluidRegistry.WATER, 0);
		MAP.put(FluidRegistry.LAVA, 64);

		addManualRegistration("milk", 16);

		if (Utils.doesBlockHaveEmc(Blocks.obsidian))
		{
			addManualRegistration("obsidian.molten", Utils.getEmcValue(Blocks.obsidian));
		}

		if (Utils.doesBlockHaveEmc(Blocks.glass))
		{
			addManualRegistration("glass.molten", Utils.getEmcValue(Blocks.glass));
		}

		if (Utils.doesItemHaveEmc(Items.ender_pearl))
		{
			addManualRegistration("ender", Utils.getEmcValue(Items.ender_pearl));
		}
	}

	private static void addManualRegistration(String fluidName, int emcValue)
	{
		Fluid fluid = FluidRegistry.getFluid(fluidName);

		if (fluid != null)
		{
			addFluidEMC(fluid, emcValue);
		}
	}

	private static boolean isFluidValid(Fluid fluid)
	{
		return MAP.containsKey(fluid) || !getODEntriesForFluid(handleFluidName(fluid)).isEmpty();
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
