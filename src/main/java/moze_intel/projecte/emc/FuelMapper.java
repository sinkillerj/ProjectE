package moze_intel.projecte.emc;

import com.google.common.collect.Lists;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Comparators;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public final class FuelMapper 
{
	private static final List<SimpleStack> FUEL_MAP = Lists.newArrayList();
	
	public static void loadMap()
	{
		if (!FUEL_MAP.isEmpty())
		{
			FUEL_MAP.clear();
		}
		
		addToMap(new ItemStack(Items.coal, 1, 1));
		addToMap(new ItemStack(Items.redstone));
		addToMap(new ItemStack(Blocks.redstone_block));
		addToMap(new ItemStack(Items.coal));
		addToMap(new ItemStack(Blocks.coal_block));
		addToMap(new ItemStack(Items.gunpowder));
		addToMap(new ItemStack(Items.glowstone_dust));
		addToMap(new ItemStack(ObjHandler.fuels, 1, 0));
		addToMap(new ItemStack(ObjHandler.fuelBlock, 1, 0));
		addToMap(new ItemStack(Items.blaze_powder));
		addToMap(new ItemStack(Blocks.glowstone));
		addToMap(new ItemStack(ObjHandler.fuels, 1, 1));
		addToMap(new ItemStack(ObjHandler.fuelBlock, 1, 1));
		addToMap(new ItemStack(ObjHandler.fuels, 1, 2));
		addToMap(new ItemStack(ObjHandler.fuelBlock, 1, 2));
		
		Collections.sort(FUEL_MAP, Comparators.SIMPLESTACK_ASCENDING);
	}
	
	private static void addToMap(ItemStack stack)
	{
		if (EMCHelper.doesItemHaveEmc(stack))
		{
			addToMap(new SimpleStack(stack));
		}
	}
	
	public static boolean isStackFuel(ItemStack stack)
	{
		return mapContains(new SimpleStack(stack));
	}
	
	public static boolean isStackMaxFuel(ItemStack stack)
	{
		return indexInMap(new SimpleStack(stack)) == FUEL_MAP.size() - 1;
	}
	
	public static ItemStack getFuelUpgrade(ItemStack stack)
	{
		SimpleStack fuel = new SimpleStack(stack);

		int index = indexInMap(fuel);
		
		if (index == -1)
		{
			PELogger.logFatal("Tried to upgrade invalid fuel: " + stack);
			return null;
		}
		
		int nextIndex = index == FUEL_MAP.size() - 1 ? 0 : index + 1;
		
		return FUEL_MAP.get(nextIndex).toItemStack();
	}

	private static void addToMap(SimpleStack stack)
	{
		if (stack.isValid())
		{
			SimpleStack copy = stack.copy();
			copy.qnty = 1;

			if (!FUEL_MAP.contains(copy))
			{
				FUEL_MAP.add(copy);
			}
		}
	}

	private static boolean mapContains(SimpleStack stack)
	{
		if (!stack.isValid())
		{
			return false;
		}

		SimpleStack copy = stack.copy();
		copy.qnty = 1;

		return FUEL_MAP.contains(copy);
	}

	private static int indexInMap(SimpleStack stack)
	{
		SimpleStack copy = stack.copy();
		copy.qnty = 1;

		return FUEL_MAP.indexOf(copy);
	}
}
