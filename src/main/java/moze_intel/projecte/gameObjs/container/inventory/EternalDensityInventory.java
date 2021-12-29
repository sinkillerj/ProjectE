package moze_intel.projecte.gameObjs.container.inventory;

import javax.annotation.Nonnull;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_server.UpdateGemModePKT;
import moze_intel.projecte.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class EternalDensityInventory implements IItemHandlerModifiable {

	private final ItemStackHandler inventory = new ItemStackHandler(9);
	private boolean isInWhitelist;
	public final ItemStack invItem;

	public EternalDensityInventory(ItemStack stack) {
		this.invItem = stack;
		if (stack.hasTag()) {
			readFromNBT(stack.getOrCreateTag());
		}
	}

	@Override
	public int getSlots() {
		return inventory.getSlots();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		ItemStack ret = inventory.insertItem(slot, stack, simulate);
		writeBack();
		return ret;
	}

	@Nonnull
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
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return inventory.isItemValid(slot, stack);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		inventory.setStackInSlot(slot, stack);
		writeBack();
	}

	private void writeBack() {
		for (int i = 0; i < inventory.getSlots(); ++i) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				inventory.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
		writeToNBT(invItem.getOrCreateTag());
	}

	public void readFromNBT(CompoundTag nbt) {
		isInWhitelist = nbt.getBoolean(Constants.NBT_KEY_GEM_WHITELIST);
		inventory.deserializeNBT(nbt.getCompound(Constants.NBT_KEY_GEM_ITEMS));
	}

	public void writeToNBT(CompoundTag nbt) {
		nbt.putBoolean(Constants.NBT_KEY_GEM_WHITELIST, isInWhitelist);
		nbt.put(Constants.NBT_KEY_GEM_ITEMS, inventory.serializeNBT());
	}

	public void changeMode() {
		isInWhitelist = !isInWhitelist;
		writeBack();
		PacketHandler.sendToServer(new UpdateGemModePKT(isInWhitelist));
	}

	public boolean isWhitelistMode() {
		return isInWhitelist;
	}
}