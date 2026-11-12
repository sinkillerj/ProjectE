package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

/**
 * Restricts insert/extract on a {@link PEItemStacksHandler} for automation exposure.
 */
public class WrappedItemHandler implements ResourceHandler<ItemResource>, IndexModifier<ItemResource> {

	private final PEItemStacksHandler compose;
	private final WriteMode mode;

	public WrappedItemHandler(PEItemStacksHandler compose, WriteMode mode) {
		this.compose = compose;
		this.mode = mode;
	}

	public int getSlots() {
		return compose.getSlots();
	}

	@NotNull
	public ItemStack getStackInSlot(int slot) {
		return compose.getStackInSlot(slot);
	}

	@Override
	public void set(int index, ItemResource resource, int amount) {
		compose.set(index, resource, amount);
	}

	@Override
	public int size() {
		return compose.size();
	}

	@Override
	public ItemResource getResource(int index) {
		return compose.getResource(index);
	}

	@Override
	public long getAmountAsLong(int index) {
		return compose.getAmountAsLong(index);
	}

	@Override
	public long getCapacityAsLong(int index, ItemResource resource) {
		return compose.getCapacityAsLong(index, resource);
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		return compose.isValid(index, resource);
	}

	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
		if (mode != WriteMode.IN && mode != WriteMode.IN_OUT) {
			return 0;
		}
		return compose.insert(index, resource, amount, transaction);
	}

	@Override
	public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
		if (mode != WriteMode.OUT && mode != WriteMode.IN_OUT) {
			return 0;
		}
		return compose.extract(index, resource, amount, transaction);
	}

	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (mode == WriteMode.IN || mode == WriteMode.IN_OUT) {
			return compose.insertItem(slot, stack, simulate);
		}
		return stack;
	}

	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (mode == WriteMode.OUT || mode == WriteMode.IN_OUT) {
			return compose.extractItem(slot, amount, simulate);
		}
		return ItemStack.EMPTY;
	}

	public PEItemStacksHandler getCompose() {
		return compose;
	}

	public enum WriteMode {
		IN,
		OUT,
		IN_OUT,
		NONE
	}
}
