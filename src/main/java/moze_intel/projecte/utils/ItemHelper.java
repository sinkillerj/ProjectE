package moze_intel.projecte.utils;

import com.google.common.collect.Lists;
import moze_intel.projecte.gameObjs.entity.EntityLootBall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Helpers for Inventories, ItemStacks, Items, and the Ore Dictionary
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class ItemHelper
{
	/**
	 * @return True if the only aspect these stacks differ by is stack size, false if item, meta, or nbt differ.
	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		return ItemStack.areItemStacksEqual(getNormalizedStack(stack1), getNormalizedStack(stack2));
	}

	public static boolean areItemStacksEqualIgnoreNBT(ItemStack stack1, ItemStack stack2)
	{
		if (stack1.getItem() != stack2.getItem())
		{
			return false;
		}


		if (stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE)
		{
			return true;
		}

		return stack1.getItemDamage() == stack2.getItemDamage();
	}

	public static boolean basicAreStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		return (stack1.getItem() == stack2.getItem()) && (stack1.getItemDamage() == stack2.getItemDamage());
	}

	public static void compactInventory(IItemHandlerModifiable inventory)
	{
		List<ItemStack> temp = new ArrayList<>();
		for (int i = 0; i < inventory.getSlots(); i++)
		{
			if (inventory.getStackInSlot(i) != null)
			{
				temp.add(inventory.getStackInSlot(i));
				inventory.setStackInSlot(i, null);
			}
		}

		for (ItemStack s : temp)
		{
			ItemHandlerHelper.insertItemStacked(inventory, s, false);
		}
	}

	public static void compactItemList(List<ItemStack> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			ItemStack s = list.get(i);
			for (int j = i + 1; j < list.size(); j++)
			{
				ItemStack s1 = list.get(j);
				if (areItemStacksEqual(s, s1))
				{
					if (s.stackSize + s1.stackSize <= s.getMaxStackSize())
					{
						s.stackSize += s1.stackSize;
						s1.stackSize = 0;
					}
					else
					{
						s1.stackSize = (s1.stackSize + s.stackSize) - s.getMaxStackSize();
						s.stackSize = s.getMaxStackSize();
					}
				}
			}
		}

		Collections.sort(list, Comparators.ITEMSTACK_ASCENDING);
		trimItemList(list);
	}

	/**
	 * Compacts and sorts list of items, without regard for stack sizes
	 */
	public static void compactItemListNoStacksize(List<ItemStack> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			ItemStack s = list.get(i);
			for (int j = i + 1; j < list.size(); j++)
			{
				ItemStack s1 = list.get(j);
				if (areItemStacksEqual(s, s1))
				{
					s.stackSize += s1.stackSize;
					s1.stackSize = 0;
				}
			}
		}

		Collections.sort(list, Comparators.ITEMSTACK_ASCENDING);
		trimItemList(list);
	}

	public static boolean containsItemStack(List<ItemStack> list, ItemStack toSearch)
	{
		for (ItemStack stack : list) {
			if (stack == null) {
				continue;
			}

			if (stack.getItem().equals(toSearch.getItem())) {
				if (!stack.getHasSubtypes() || stack.getItemDamage() == toSearch.getItemDamage()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns an ItemStack with stacksize 1.
	 */
	public static ItemStack getNormalizedStack(ItemStack stack)
	{
		ItemStack result = stack.copy();
		result.stackSize = 1;
		return result;
	}

	/**
	 * Get a List of itemstacks from an OD name.<br>
	 * It also makes sure that no items with damage 32767 are included, to prevent errors.
	 */
	public static List<ItemStack> getODItems(String oreName)
	{
		List<ItemStack> result = Lists.newArrayList();

		for (ItemStack stack : OreDictionary.getOres(oreName))
		{
			if (stack == null)
			{
				continue;
			}

			if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
			{
				List<ItemStack> list = Lists.newArrayList();

				ItemStack copy = stack.copy();
				copy.setItemDamage(0);

				list.add(copy.copy());

				String startName = copy.getUnlocalizedName();

				for (int i = 1; i <= 128; i++)
				{
					try
					{
						copy.setItemDamage(i);

						if (copy.getUnlocalizedName() == null || copy.getUnlocalizedName().equals(startName))
						{
							result.addAll(list);
							break;
						}
					}
					catch (Exception e)
					{
						PELogger.logFatal("Couldn't retrieve OD items for: " + oreName);
						PELogger.logFatal("Caused by: " + e.toString());

						result.addAll(list);
						break;
					}

					list.add(copy.copy());

					if (i == 128)
					{
						copy.setItemDamage(0);
						result.add(copy);
					}
				}
			}
			else
			{
				result.add(stack.copy());
			}
		}

		return result;
	}

	public static String getOreDictionaryName(ItemStack stack)
	{
		if (stack == null || stack.getItem() == null)
		{
			return "Unknown";
		}
		int[] oreIds = OreDictionary.getOreIDs(stack);

		if (oreIds.length == 0)
		{
			return "Unknown";
		}

		return OreDictionary.getOreName(oreIds[0]);
	}

	public static ItemStack getStackFromString(String internal, int metaData)
	{
		Item item = Item.REGISTRY.getObject(new ResourceLocation(internal));

		if (item == null)
		{
			return null;
		}

		return new ItemStack(item, 1, metaData);
	}

	public static boolean hasSpace(ItemStack[] inv, ItemStack stack)
	{
		for (ItemStack invStack : inv)
		{
			if (invStack == null)
			{
				return true;
			}

			if (areItemStacksEqual(stack, invStack) && invStack.stackSize < invStack.getMaxStackSize())
			{
				return true;
			}
		}

		return false;
	}

	public static IItemHandlerModifiable immutableCopy(IItemHandler toCopy)
	{
		final List<ItemStack> list = new ArrayList<>(toCopy.getSlots());
		for (int i = 0; i < toCopy.getSlots(); i++)
		{
			list.add(toCopy.getStackInSlot(i));
		}

		return new IItemHandlerModifiable()
		{
			@Override
			public void setStackInSlot(int slot, ItemStack stack) {}

			@Override
			public int getSlots() {
				return list.size();
			}

			@Override
			public ItemStack getStackInSlot(int slot) {
				return list.get(slot);
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				return stack;
			}

			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return null;
			}
		};
	}

	public static boolean invContainsItem(IItemHandler inv, ItemStack toSearch)
	{
		for (int i = 0; i < inv.getSlots(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);

			if (stack != null && basicAreStacksEqual(stack, toSearch))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isOre(IBlockState state)
	{
		if (state.getBlock() == Blocks.LIT_REDSTONE_ORE)
		{
			return true;
		}
		String oreDictName = getOreDictionaryName(stateToStack(state, 1));
		return oreDictName.startsWith("ore") || oreDictName.startsWith("denseore");
	}

	public static void pushLootBallInInv(IItemHandler inv, EntityLootBall ball)
	{
		List<ItemStack> results = Lists.newArrayList();
		for (ItemStack s : ball.getItemList())
		{
			ItemStack result = ItemHandlerHelper.insertItemStacked(inv, s, false);
			if (result != null)
			{
				results.add(result);
			}
		}
		ball.setItemList(results);
	}

	public static IBlockState stackToState(ItemStack stack)
	{
		if (stack.getItem() instanceof ItemBlock)
		{
			return ((ItemBlock) stack.getItem()).block.getStateFromMeta(stack.getItemDamage());
		}
		else
		{
			return null;
		}
	}

	public static ItemStack stateToStack(IBlockState state, int stackSize)
	{
		return new ItemStack(state.getBlock(), stackSize, state.getBlock().getMetaFromState(state));
	}

	public static ItemStack stateToDroppedStack(IBlockState state, int stackSize)
	{
		return new ItemStack(state.getBlock(), stackSize, state.getBlock().damageDropped(state));
	}

	private static void trimItemList(List<ItemStack> list)
	{
		Iterator<ItemStack> iter = list.iterator();
		while (iter.hasNext())
		{
			ItemStack s = iter.next();
			if (s.stackSize <= 0)
			{
				iter.remove();
			}
		}
	}
}
