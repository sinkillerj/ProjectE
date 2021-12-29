package moze_intel.projecte.utils;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
	public static InteractionResultHolder<ItemStack> actionResultFromType(InteractionResult type, ItemStack stack) {
		return switch (type) {
			case SUCCESS -> InteractionResultHolder.success(stack);
			case CONSUME -> InteractionResultHolder.consume(stack);
			case FAIL -> InteractionResultHolder.fail(stack);
			default -> InteractionResultHolder.pass(stack);
		};
	}

	/**
	 * @return True if the only aspect these stacks differ by is stack size, false if item, meta, or nbt differ.
	 */
	public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2) {
		return ItemStack.matches(getNormalizedStack(stack1), getNormalizedStack(stack2));
	}

	/**
	 * Checks the item's NBT for a given boolean value.
	 *
	 * @param stack Stack, assumes it has already been validated to not be empty
	 * @param key   NBT Key to check.
	 *
	 * @return {@code true} if the item NBT matches.
	 */
	public static boolean checkItemNBT(ItemStack stack, String key) {
		return stack.hasTag() && stack.getOrCreateTag().getBoolean(key);
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
	 * Copies the nbt compound similar to how {@link CompoundTag#copy()} does, except it just skips the desired key instead of having to copy a potentially large value
	 * which may be expensive, and then remove it from the copy.
	 *
	 * @implNote If the input {@link CompoundTag} only contains the key we want to skip, we return null instead of an empty {@link CompoundTag}.
	 */
	@Nullable
	public static CompoundTag copyNBTSkipKey(@Nonnull CompoundTag nbt, @Nonnull String keyToSkip) {
		CompoundTag copiedNBT = new CompoundTag();
		for (String key : nbt.getAllKeys()) {
			if (keyToSkip.equals(key)) {
				continue;
			}
			Tag innerNBT = nbt.get(key);
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
		return size(stack, 1);
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

	public static boolean isOre(BlockState state) {
		return state.is(Tags.Blocks.ORES);
	}

	public static boolean isOre(Block b) {
		return Tags.Blocks.ORES.contains(b);
	}

	public static boolean isOre(Item i) {
		return isOre(Block.byItem(i));
	}

	public static boolean isRepairableDamagedItem(ItemStack stack) {
		return stack.isDamageableItem() && stack.isRepairable() && stack.getDamageValue() > 0;
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
	public static CompoundTag recombineNBT(List<CompoundTag> pieces) {
		if (pieces.isEmpty()) {
			return null;
		}
		CompoundTag combinedNBT = pieces.get(0);
		for (int i = 1; i < pieces.size(); i++) {
			combinedNBT = combinedNBT.merge(pieces.get(i));
		}
		return combinedNBT;
	}

	public static ItemStack size(ItemStack stack, int size) {
		if (size <= 0 || stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return ItemHandlerHelper.copyStackWithSize(stack, size);
	}

	public static BlockState stackToState(ItemStack stack) {
		return stack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock().defaultBlockState() : null;
	}
}