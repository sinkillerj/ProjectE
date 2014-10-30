package moze_intel.projecte.emc;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Comparators;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FuelMapper 
{
	private static final List<SimpleStack> FUEL_MAP = new ArrayList<SimpleStack>();
	
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
		if (Utils.doesItemHaveEmc(stack))
		{
			FUEL_MAP.add(new SimpleStack(stack));
		}
	}
	
	public static boolean isStackFuel(ItemStack stack)
	{
		return FUEL_MAP.contains(new SimpleStack(stack));
	}
	
	public static boolean isStackMaxFuel(ItemStack stack)
	{
		return FUEL_MAP.indexOf(new SimpleStack(stack)) == FUEL_MAP.size() - 1;
	}
	
	public static ItemStack getFuelUpgrade(ItemStack stack)
	{
		SimpleStack fuel = new SimpleStack(stack);

		int index = FUEL_MAP.indexOf(fuel);
		
		if (index == -1)
		{
			PELogger.logFatal("Fuel not found in fuel map: "+stack);
			return null;
		}
		
		int nextIndex = index == FUEL_MAP.size() - 1 ? 0 : index + 1;
		
		return Utils.getStackFromSimpleStack(FUEL_MAP.get(nextIndex));
	}
}
