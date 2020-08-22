package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.inventory.MercurialEyeInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.utils.ContainerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

public class MercurialEyeContainer extends Container {

	private final MercurialEyeInventory inventory;

	public static MercurialEyeContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf) {
		Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
		return new MercurialEyeContainer(windowId, invPlayer, hand);
	}

	public MercurialEyeContainer(int windowId, PlayerInventory invPlayer, Hand hand) {
		super(PEContainerTypes.MERCURIAL_EYE_CONTAINER.get(), windowId);
		inventory = new MercurialEyeInventory(invPlayer.player.getHeldItem(hand));

		//Klein Star
		this.addSlot(new ValidatedSlot(inventory, 0, 50, 26, SlotPredicates.EMC_HOLDER));

		//Target
		this.addSlot(new SlotGhost(inventory, 1, 104, 26, SlotPredicates.MERCURIAL_TARGET));

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 6, 56);
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity var1) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack slotClick(int slot, int button, @Nonnull ClickType flag, PlayerEntity player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == inventory.invItem) {
			return ItemStack.EMPTY;
		}

		if (slot == 1 && !inventory.getStackInSlot(slot).isEmpty()) {
			inventory.setStackInSlot(1, ItemStack.EMPTY);
		}

		return super.slotClick(slot, button, flag, player);
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int slotIndex) {
		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.getHasStack()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();

		if (slotIndex < 2) {
			// Moving to player inventory
			if (!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true)) {
				return ItemStack.EMPTY;
			}
			slot.onSlotChanged();
		} else {
			// Moving from player inventory
			Slot kleinSlot = inventorySlots.get(0);
			if (kleinSlot.isItemValid(stack) && kleinSlot.getStack().isEmpty()) { // Is a valid klein star and the slot is empty?
				kleinSlot.putStack(stack.split(1));
			} else {
				Slot targetSlot = inventorySlots.get(1);
				if (targetSlot.isItemValid(stack) && targetSlot.getStack().isEmpty()) { // Is a valid target block and the slot is empty?
					targetSlot.putStack(stack.split(1));
				} else {
					// Is neither, ignore
					return ItemStack.EMPTY;
				}
			}
		}
		if (stack.isEmpty()) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			slot.onSlotChanged();
		}
		return slot.onTake(player, newStack);
	}
}