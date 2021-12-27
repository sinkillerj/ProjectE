package moze_intel.projecte.gameObjs.container;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.container.slots.HotBarSlot;
import moze_intel.projecte.gameObjs.container.slots.IInsertableSlot;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.gameObjs.container.slots.MainInventorySlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;

public abstract class PEContainer extends Container {

	protected final List<InventoryContainerSlot> inventoryContainerSlots = new ArrayList<>();
	protected final List<MainInventorySlot> mainInventorySlots = new ArrayList<>();
	protected final List<HotBarSlot> hotBarSlots = new ArrayList<>();
	// Vanilla only syncs int fields in the superclass as shorts (yay legacy)
	// here we hold fields we really want to use 32 bits for
	private final List<IntReferenceHolder> intFields = new ArrayList<>();
	protected final List<BoxedLong> longFields = new ArrayList<>();

	protected PEContainer(ContainerTypeRegistryObject<? extends PEContainer> typeRO, int id) {
		super(typeRO.get(), id);
	}

	protected void addPlayerInventory(PlayerInventory invPlayer, int xStart, int yStart) {
		int slotSize = 18;
		int rows = 3;
		//Main Inventory
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < 9; j++) {
				addSlot(createMainInventorySlot(invPlayer, j + i * 9 + 9, xStart + j * slotSize, yStart + i * slotSize));
			}
		}
		yStart = yStart + slotSize * rows + 4;
		//Hot Bar
		for (int i = 0; i < PlayerInventory.getSelectionSize(); i++) {
			addSlot(createHotBarSlot(invPlayer, i, xStart + i * slotSize, yStart));
		}
	}

	protected MainInventorySlot createMainInventorySlot(@Nonnull PlayerInventory inv, int index, int x, int y) {
		return new MainInventorySlot(inv, index, x, y);
	}

	protected HotBarSlot createHotBarSlot(@Nonnull PlayerInventory inv, int index, int x, int y) {
		return new HotBarSlot(inv, index, x, y);
	}

	@Nonnull
	@Override
	protected Slot addSlot(@Nonnull Slot slot) {
		super.addSlot(slot);
		if (slot instanceof InventoryContainerSlot) {
			inventoryContainerSlots.add((InventoryContainerSlot) slot);
		} else if (slot instanceof MainInventorySlot) {
			mainInventorySlots.add((MainInventorySlot) slot);
		} else if (slot instanceof HotBarSlot) {
			hotBarSlots.add((HotBarSlot) slot);
		}
		return slot;
	}

	@Nullable
	public Slot tryGetSlot(int slotId) {
		if (slotId >= 0 && slotId < slots.size()) {
			return getSlot(slotId);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return The contents in this slot AFTER transferring items away.
	 *
	 * @implNote Copy/based off Mekanism
	 */
	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotID) {
		Slot currentSlot = slots.get(slotID);
		if (currentSlot == null || !currentSlot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack slotStack = currentSlot.getItem();
		ItemStack stackToInsert = slotStack;
		if (currentSlot instanceof InventoryContainerSlot) {
			//Insert into stacks that already contain an item in the order hot bar -> main inventory
			stackToInsert = insertItem(hotBarSlots, stackToInsert, true);
			stackToInsert = insertItem(mainInventorySlots, stackToInsert, true);
			//If we still have any left then input into the empty stacks in the order of main inventory -> hot bar
			// Note: Even though we are doing the main inventory, we still need to do both, ignoring empty then not instead of
			// just directly inserting into the main inventory, in case there are empty slots before the one we can stack with
			stackToInsert = insertItem(hotBarSlots, stackToInsert, false);
			stackToInsert = insertItem(mainInventorySlots, stackToInsert, false);
		} else {
			//We are in the main inventory or the hot bar
			//Start by trying to insert it into the tile's inventory slots, first attempting to stack with other items
			stackToInsert = insertItem(inventoryContainerSlots, stackToInsert, true);
			if (slotStack.getCount() == stackToInsert.getCount()) {
				//Then as long as if we still have the same number of items (failed to insert), try to insert it into the tile's inventory slots allowing for empty items
				stackToInsert = insertItem(inventoryContainerSlots, stackToInsert, false);
				if (slotStack.getCount() == stackToInsert.getCount()) {
					//Else if we failed to do that also, try transferring to armor inventory, main inventory or the hot bar, depending on which one we currently are in
					if (currentSlot instanceof MainInventorySlot) {
						stackToInsert = insertItem(hotBarSlots, stackToInsert, true);
						stackToInsert = insertItem(hotBarSlots, stackToInsert, false);
					} else if (currentSlot instanceof HotBarSlot) {
						stackToInsert = insertItem(mainInventorySlots, stackToInsert, true);
						stackToInsert = insertItem(mainInventorySlots, stackToInsert, false);
					}
				}
			}
		}
		if (stackToInsert.getCount() == slotStack.getCount()) {
			//If nothing changed then return that fact
			return ItemStack.EMPTY;
		}
		//Otherwise, decrease the stack by the amount we inserted, and return it as a new stack for what is now in the slot
		int difference = slotStack.getCount() - stackToInsert.getCount();
		currentSlot.remove(difference);
		ItemStack newStack = ItemHelper.size(slotStack, difference);
		currentSlot.onTake(player, newStack);
		return newStack;
	}

	/**
	 * @param slots       Slots to insert into
	 * @param stack       Stack to insert (do not modify).
	 * @param ignoreEmpty {@code true} to ignore/skip empty slots, {@code false} to ignore/skip non-empty slots.
	 *
	 * @return Remainder
	 */
	@Nonnull
	public static <SLOT extends Slot & IInsertableSlot> ItemStack insertItem(List<SLOT> slots, @Nonnull ItemStack stack, boolean ignoreEmpty) {
		if (stack.isEmpty()) {
			//Skip doing anything if the stack is already empty.
			// Makes it easier to chain calls, rather than having to check if the stack is empty after our previous call
			return stack;
		}
		for (SLOT slot : slots) {
			if (ignoreEmpty != slot.hasItem()) {
				//Skip checking empty stacks if we want to ignore them, and skipp non-empty stacks if we don't want ot ignore them
				continue;
			}
			stack = slot.insertItem(stack, false);
			if (stack.isEmpty()) {
				break;
			}
		}
		return stack;
	}

	protected static boolean stillValid(PlayerEntity player, TileEntity tile, BlockRegistryObject<?, ?> blockRO) {
		BlockPos pos = tile.getBlockPos();
		return player.level.getBlockState(pos).getBlock() == blockRO.getBlock() &&
			   player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
	}

	public final void updateProgressBarLong(int idx, long data) {
		longFields.get(idx).set(data);
	}

	public final void updateProgressBarInt(int idx, int data) {
		intFields.get(idx).set(data);
	}

	@Nonnull
	@Override
	protected IntReferenceHolder addDataSlot(@Nonnull IntReferenceHolder referenceHolder) {
		intFields.add(referenceHolder);
		return referenceHolder;
	}

	@Override
	public void broadcastChanges() {
		for (int i = 0; i < longFields.size(); i++) {
			BoxedLong boxedLong = longFields.get(i);
			if (boxedLong.isDirty()) {
				for (IContainerListener listener : containerListeners) {
					PacketHandler.sendProgressBarUpdateLong(listener, this, i, boxedLong.get());
				}
			}
		}
		for (int i = 0; i < intFields.size(); i++) {
			IntReferenceHolder referenceHolder = intFields.get(i);
			if (referenceHolder.checkAndClearUpdateFlag()) {
				for (IContainerListener listener : containerListeners) {
					PacketHandler.sendProgressBarUpdateInt(listener, this, i, referenceHolder.get());
				}
			}
		}
		super.broadcastChanges();
	}

	public static class BoxedLong {

		private long inner;
		private boolean dirty = false;

		public long get() {
			return inner;
		}

		public void set(long v) {
			if (v != inner) {
				inner = v;
				dirty = true;
			}
		}

		public boolean isDirty() {
			boolean ret = dirty;
			dirty = false;
			return ret;
		}
	}
}