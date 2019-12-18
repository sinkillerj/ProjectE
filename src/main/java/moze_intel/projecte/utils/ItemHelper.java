package moze_intel.projecte.utils;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Helpers for Inventories, ItemStacks, Items, and the Ore Dictionary Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class ItemHelper {

	/**
	 * Gets an ActionResult based on a type
	 */
	public static ActionResult<ItemStack> actionResultFromType(ActionResultType type, ItemStack stack) {
		switch (type) {
			case SUCCESS:
				return ActionResult.func_226248_a_(stack);
			case CONSUME:
				return ActionResult.func_226249_b_(stack);
			case FAIL:
				return ActionResult.func_226251_d_(stack);
			case PASS:
			default:
				return ActionResult.func_226250_c_(stack);
		}
	}

	/**
	 * @return True if the only aspect these stacks differ by is stack size, false if item, meta, or nbt differ.
	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2) {
		return ItemStack.areItemStacksEqual(getNormalizedStack(stack1), getNormalizedStack(stack2));
	}

	/**
	 * Compacts an inventory and returns if the inventory is/was empty.
	 *
	 * @return True if the inventory was empty.
	 */
	public static boolean compactInventory(IItemHandlerModifiable inventory) {
		List<ItemStack> temp = new ArrayList<>();
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stackInSlot = inventory.getStackInSlot(i);
			if (!stackInSlot.isEmpty()) {
				temp.add(stackInSlot);
				inventory.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
		for (ItemStack s : temp) {
			ItemHandlerHelper.insertItemStacked(inventory, s, false);
		}
		return temp.isEmpty();
	}

	/**
	 * Compacts and sorts list of items, without regard for stack sizes
	 */
	public static void compactItemListNoStacksize(List<ItemStack> list) {
		for (int i = 0; i < list.size(); i++) {
			ItemStack s = list.get(i);
			if (!s.isEmpty()) {
				for (int j = i + 1; j < list.size(); j++) {
					ItemStack s1 = list.get(j);
					if (ItemHandlerHelper.canItemStacksStack(s, s1)) {
						s.grow(s1.getCount());
						list.set(j, ItemStack.EMPTY);
					}
				}
			}
		}
		list.removeIf(ItemStack::isEmpty);
		list.sort(Comparators.ITEMSTACK_ASCENDING);
	}

	/**
	 * Copies the nbt compound similar to how {@link CompoundNBT#copy()} does, except it just skips the desired key instead of having to copy a potentially large value
	 * which may be expensive, and then remove it from the copy.
	 *
	 * @implNote If the input {@link CompoundNBT} only contains the key we want to skip, we return null instead of an empty {@link CompoundNBT}.
	 */
	@Nullable
	public static CompoundNBT copyNBTSkipKey(@Nonnull CompoundNBT nbt, @Nonnull String keyToSkip) {
		CompoundNBT copiedNBT = new CompoundNBT();
		for (String key : nbt.keySet()) {
			if (keyToSkip.equals(key)) {
				continue;
			}
			INBT innerNBT = nbt.get(key);
			if (innerNBT != null) {
				//Shouldn't be null but double check
				copiedNBT.put(key, innerNBT.copy());
			}
		}
		if (copiedNBT.isEmpty()) {
			return null;
		}
		return copiedNBT;
	}

	/**
	 * Returns an ItemStack with stacksize 1.
	 */
	public static ItemStack getNormalizedStack(ItemStack stack) {
		ItemStack result = stack.copy();
		result.setCount(1);
		return result;
	}

	public static IItemHandlerModifiable immutableCopy(IItemHandler toCopy) {
		final List<ItemStack> list = new ArrayList<>(toCopy.getSlots());
		for (int i = 0; i < toCopy.getSlots(); i++) {
			list.add(toCopy.getStackInSlot(i));
		}
		return new IItemHandlerModifiable() {
			@Override
			public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
			}

			@Override
			public int getSlots() {
				return list.size();
			}

			@Nonnull
			@Override
			public ItemStack getStackInSlot(int slot) {
				return list.get(slot);
			}

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return stack;
			}

			@Nonnull
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return ItemStack.EMPTY;
			}

			@Override
			public int getSlotLimit(int slot) {
				return getStackInSlot(slot).getMaxStackSize();
			}

			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return true;
			}
		};
	}

	public static boolean isDamageable(ItemStack stack) {
		return stack.isDamageable();
	}

	public static boolean isOre(BlockState state) {
		return state.isIn(Tags.Blocks.ORES);
	}

	public static boolean isOre(Block b) {
		return Tags.Blocks.ORES.contains(b);
	}

	public static boolean isOre(Item i) {
		return isOre(Block.getBlockFromItem(i));
	}

	/**
	 * @return The amount of the given stack that could not fit. If it all fit, zero is returned
	 */
	public static int simulateFit(NonNullList<ItemStack> inv, ItemStack stack) {
		int remainder = stack.getCount();
		for (ItemStack invStack : inv) {
			if (invStack.isEmpty()) {
				//Slot is empty, just put it all there
				return 0;
			}
			if (ItemHandlerHelper.canItemStacksStack(stack, invStack)) {
				int amountSlotNeeds = invStack.getMaxStackSize() - invStack.getCount();
				//Double check we don't have an over sized stack
				if (amountSlotNeeds > 0) {
					if (remainder <= amountSlotNeeds) {
						//If the slot can accept it all, return it all fit
						return 0;
					}
					//Otherwise take that many items out and
					remainder -= amountSlotNeeds;
				}
			}
		}
		return remainder;
	}

	@Nullable
	public static CompoundNBT recombineNBT(List<CompoundNBT> pieces) {
		if (pieces.isEmpty()) {
			return null;
		}
		CompoundNBT combinedNBT = pieces.get(0);
		for (int i = 1; i < pieces.size(); i++) {
			combinedNBT = combinedNBT.merge(pieces.get(i));
		}
		return combinedNBT;
	}

	public static BlockState stackToState(ItemStack stack) {
		if (stack.getItem() instanceof BlockItem) {
			return ((BlockItem) stack.getItem()).getBlock().getDefaultState();
		}
		return null;
	}
}