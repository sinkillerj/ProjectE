package moze_intel.projecte.emc;

import com.google.common.collect.Lists;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.Comparator;
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
		
		addToMap(new ItemStack(Items.COAL, 1, 1));
		addToMap(new ItemStack(Items.REDSTONE));
		addToMap(new ItemStack(Blocks.REDSTONE_BLOCK));
		addToMap(new ItemStack(Items.COAL));
		addToMap(new ItemStack(Blocks.COAL_BLOCK));
		addToMap(new ItemStack(Items.GUNPOWDER));
		addToMap(new ItemStack(Items.GLOWSTONE_DUST));
		addToMap(new ItemStack(ObjHandler.fuels, 1, 0));
		addToMap(new ItemStack(ObjHandler.fuelBlock, 1, 0));
		addToMap(new ItemStack(Items.BLAZE_POWDER));
		addToMap(new ItemStack(Blocks.GLOWSTONE));
		addToMap(new ItemStack(ObjHandler.fuels, 1, 1));
		addToMap(new ItemStack(ObjHandler.fuelBlock, 1, 1));
		addToMap(new ItemStack(ObjHandler.fuels, 1, 2));
		addToMap(new ItemStack(ObjHandler.fuelBlock, 1, 2));
		
		Collections.sort(FUEL_MAP, Comparator.comparing(EMCMapper::getEmcValue));
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
			if (!FUEL_MAP.contains(stack))
			{
				FUEL_MAP.add(stack);
			}
		}
	}

	private static boolean mapContains(SimpleStack stack)
	{
		return stack.isValid() && FUEL_MAP.contains(stack);
	}

	private static int indexInMap(SimpleStack stack)
	{
		return FUEL_MAP.indexOf(stack);
	}
}
