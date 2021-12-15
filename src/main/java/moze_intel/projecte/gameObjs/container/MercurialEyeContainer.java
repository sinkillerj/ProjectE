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
		return new MercurialEyeContainer(windowId, invPlayer, buf.readEnum(Hand.class));
	}

	public MercurialEyeContainer(int windowId, PlayerInventory invPlayer, Hand hand) {
		super(PEContainerTypes.MERCURIAL_EYE_CONTAINER.get(), windowId);
		inventory = new MercurialEyeInventory(invPlayer.player.getItemInHand(hand));

		//Klein Star
		this.addSlot(new ValidatedSlot(inventory, 0, 50, 26, SlotPredicates.EMC_HOLDER));

		//Target
		this.addSlot(new SlotGhost(inventory, 1, 104, 26, SlotPredicates.MERCURIAL_TARGET));

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 6, 56);
	}

	@Override
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack clicked(int slotId, int button, @Nonnull ClickType flag, @Nonnull PlayerEntity player) {
		if (slotId >= 0) {
			Slot slot = getSlot(slotId);
			if (slot != null && slot.getItem() == inventory.invItem) {
				return ItemStack.EMPTY;
			}
		}

		if (slotId == 1 && !inventory.getStackInSlot(slotId).isEmpty()) {
			inventory.setStackInSlot(1, ItemStack.EMPTY);
		}

		return super.clicked(slotId, button, flag, player);
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotIndex) {
		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getItem();
		ItemStack newStack = stack.copy();

		if (slotIndex < 2) {
			// Moving to player inventory
			if (!this.moveItemStackTo(stack, 2, this.slots.size(), true)) {
				return ItemStack.EMPTY;
			}
			slot.setChanged();
		} else {
			// Moving from player inventory
			Slot kleinSlot = slots.get(0);
			if (kleinSlot.mayPlace(stack) && kleinSlot.getItem().isEmpty()) { // Is a valid klein star and the slot is empty?
				kleinSlot.set(stack.split(1));
			} else {
				Slot targetSlot = slots.get(1);
				if (targetSlot.mayPlace(stack) && targetSlot.getItem().isEmpty()) { // Is a valid target block and the slot is empty?
					targetSlot.set(stack.split(1));
				} else {
					// Is neither, ignore
					return ItemStack.EMPTY;
				}
			}
		}
		if (stack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return slot.onTake(player, newStack);
	}
}