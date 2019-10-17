package moze_intel.projecte.emc;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

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

		addToMap(Items.CHARCOAL);
		addToMap(Items.REDSTONE);
		addToMap(Blocks.REDSTONE_BLOCK);
		addToMap(Items.COAL);
		addToMap(Blocks.COAL_BLOCK);
		addToMap(Items.GUNPOWDER);
		addToMap(Items.GLOWSTONE_DUST);
		addToMap(ObjHandler.alchemicalCoal);
		addToMap(ObjHandler.alchemicalCoalBlock);
		addToMap(Items.BLAZE_POWDER);
		addToMap(Blocks.GLOWSTONE);
		addToMap(ObjHandler.mobiusFuel);
		addToMap(ObjHandler.mobiusFuelBlock);
		addToMap(ObjHandler.aeternalisFuel);
		addToMap(ObjHandler.aeternalisFuelBlock);
		
		FUEL_MAP.sort(Comparator.comparing(EMCMapper::getEmcValue));
	}
	
	private static void addToMap(IItemProvider item)
	{
		if (EMCHelper.doesItemHaveEmc(item) && !FUEL_MAP.contains(item.asItem()))
		{
			FUEL_MAP.add(item.asItem());
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
