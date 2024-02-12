package moze_intel.projecte.gameObjs.container.inventory;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.network.PacketUtils;
import moze_intel.projecte.network.packets.to_server.UpdateGemModePKT;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class EternalDensityInventory implements IItemHandlerModifiable {

	private final NonNullList<ItemStack> inventoryStacks = NonNullList.withSize(9, ItemStack.EMPTY);
	private final ItemStackHandler inventory = new ItemStackHandler(inventoryStacks);
	private boolean isInWhitelist;
	public final ItemStack invItem;

	public EternalDensityInventory(ItemStack stack) {
		this.invItem = stack;
		this.isInWhitelist = stack.getData(PEAttachmentTypes.GEM_WHITELIST);
		List<ItemStack> targets = stack.getData(PEAttachmentTypes.GEM_TARGETS);
		for (int i = 0, size = Math.min(targets.size(), 9); i < size; i++) {
			inventoryStacks.set(i, targets.get(i).copy());
		}
	}

	@Override
	public int getSlots() {
		return inventory.getSlots();
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@NotNull
	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		ItemStack ret = inventory.insertItem(slot, stack, simulate);
		writeBack();
		return ret;
	}

	@NotNull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack ret = inventory.extractItem(slot, amount, simulate);
		writeBack();
		return ret;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return inventory.isItemValid(slot, stack);
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		inventory.setStackInSlot(slot, stack);
		writeBack();
	}

	private void writeBack() {
		for (int i = 0; i < inventory.getSlots(); ++i) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				inventory.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
		invItem.setData(PEAttachmentTypes.GEM_WHITELIST, isInWhitelist);
		//TODO - 1.20.4: Test this
		List<ItemStack> targets = new ArrayList<>(inventoryStacks.size());
		for (int i = 0, size = inventoryStacks.size(); i < size; i++) {
			ItemStack target = inventoryStacks.get(i);
			if (!target.isEmpty() && targets.stream().noneMatch(r -> ItemHandlerHelper.canItemStacksStack(r, target))) {
				targets.set(i, target.copy());
			}
		}
		invItem.setData(PEAttachmentTypes.GEM_TARGETS, targets);
	}

	public void changeMode() {
		isInWhitelist = !isInWhitelist;
		writeBack();
		PacketUtils.sendToServer(new UpdateGemModePKT(isInWhitelist));
	}

	public boolean isWhitelistMode() {
		return isInWhitelist;
	}
}