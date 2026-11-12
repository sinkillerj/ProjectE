package moze_intel.projecte.gameObjs.container.inventory;

import java.util.Set;
import moze_intel.projecte.components.GemData;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import org.jetbrains.annotations.NotNull;

public class EternalDensityInventory extends PEItemStacksHandler {

	private final ItemStack invItem;
	private final boolean remote;

	public EternalDensityInventory(ItemStack stack, boolean remote) {
		super(9);
		this.invItem = stack;
		this.remote = remote;
		int slot = 0;
		for (ItemStack whitelisted : invItem.getOrDefault(PEDataComponentTypes.GEM_DATA, GemData.EMPTY).whitelist()) {
			if (!whitelisted.isEmpty()) {
				//Note: We copy it so that it doesn't mutate our gem data's stack
				stacks.set(slot++, whitelisted.copy());
				if (slot >= getSlots()) {
					break;
				}
			}
		}
	}

	@Override
	public int getSlots() {
		return 9;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}
		for (int i = 0, slots = getSlots(); i < slots; i++) {
			if (ItemStack.isSameItemSameComponents(stack, getStackInSlot(i))) {
				//Only allow duplicates if it is the same slot as it is already stored in
				return i == slot;
			}
		}
		return true;
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		if (isItemValid(slot, stack)) {//Ensure the stack is valid before setting it
			super.setStackInSlot(slot, stack);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	protected void onContentsChanged(int slot) {//TODO: Make use of the slot parameter somehow?
		if (remote) {//Skip updating the item on teh client as we already sync the data the client cares about
			return;
		}
		Set<ItemStack> targets = ItemStackLinkedSet.createTypeAndComponentsSet();
		for (int i = 0, slots = getSlots(); i < slots; ++i) {
			ItemStack stackInSlot = getStackInSlot(i);
			if (!stackInSlot.isEmpty()) {
				targets.add(stackInSlot.copyWithCount(1));
			}
		}
		invItem.update(PEDataComponentTypes.GEM_DATA, GemData.EMPTY, targets, GemData::withWhitelistSafe);
	}
}
