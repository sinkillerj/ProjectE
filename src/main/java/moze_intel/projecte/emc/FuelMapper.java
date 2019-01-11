package moze_intel.projecte.emc;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class FuelMapper
{
	private static final List<Item> FUEL_MAP = new ArrayList<>();
	
	public static void loadMap()
	{
		FUEL_MAP.clear();

		addToMap(new ItemStack(Items.CHARCOAL));
		addToMap(new ItemStack(Items.REDSTONE));
		addToMap(new ItemStack(Blocks.REDSTONE_BLOCK));
		addToMap(new ItemStack(Items.COAL));
		addToMap(new ItemStack(Blocks.COAL_BLOCK));
		addToMap(new ItemStack(Items.GUNPOWDER));
		addToMap(new ItemStack(Items.GLOWSTONE_DUST));
		addToMap(new ItemStack(ObjHandler.alchemicalCoal));
		addToMap(new ItemStack(ObjHandler.alchemicalCoalBlock));
		addToMap(new ItemStack(Items.BLAZE_POWDER));
		addToMap(new ItemStack(Blocks.GLOWSTONE));
		addToMap(new ItemStack(ObjHandler.mobiusFuel));
		addToMap(new ItemStack(ObjHandler.mobiusFuelBlock));
		addToMap(new ItemStack(ObjHandler.aeternalisFuel));
		addToMap(new ItemStack(ObjHandler.aeternalisFuelBlock));
		
		FUEL_MAP.sort(Comparator.comparing(EMCMapper::getEmcValue));
	}
	
	private static void addToMap(ItemStack stack)
	{
		if (EMCHelper.doesItemHaveEmc(stack) && !FUEL_MAP.contains(stack.getItem()))
		{
			FUEL_MAP.add(stack.getItem());
		}
	}
	
	public static boolean isStackFuel(ItemStack stack)
	{
		return FUEL_MAP.contains(stack.getItem());
	}
	
	public static boolean isStackMaxFuel(ItemStack stack)
	{
		return FUEL_MAP.indexOf(stack.getItem()) == FUEL_MAP.size() - 1;
	}
	
	public static ItemStack getFuelUpgrade(ItemStack stack)
	{
		int index = FUEL_MAP.indexOf(stack.getItem());
		
		if (index == -1)
		{
			PECore.LOGGER.warn("Tried to upgrade invalid fuel: {}", stack);
			return ItemStack.EMPTY;
		}
		
		int nextIndex = index == FUEL_MAP.size() - 1 ? 0 : index + 1;
		
		return new ItemStack(FUEL_MAP.get(nextIndex));
	}

	/**
	 * @return An immutable version of the Fuel Map
	 */
	public static List<Item> getFuelMap()
	{
		return Collections.unmodifiableList(FUEL_MAP);
	}
}
