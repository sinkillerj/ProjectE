package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
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
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AlchBagContainer extends Container {

	public final Hand hand;
	private final int blocked;
	private final boolean immutable;

	public static AlchBagContainer fromNetwork(int windowId, PlayerInventory playerInv, PacketBuffer buf) {
		return new AlchBagContainer(windowId, playerInv, buf.readEnum(Hand.class), new ItemStackHandler(104), buf.readBoolean());
	}

	public AlchBagContainer(int windowId, PlayerInventory invPlayer, Hand hand, IItemHandlerModifiable invBag, boolean immutable) {
		super(PEContainerTypes.ALCH_BAG_CONTAINER.get(), windowId);
		this.hand = hand;
		this.immutable = immutable;

		//Bag Inventory
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 13; j++) {
				this.addSlot(new SlotItemHandler(invBag, j + i * 13, 12 + j * 18, 5 + i * 18));
			}
		}

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 48, 152);

		blocked = hand == Hand.MAIN_HAND ? (slots.size() - 1) - (8 - invPlayer.selected) : -1;
	}

	@Override
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotIndex) {
		if (immutable) {
			return ItemStack.EMPTY;
		}

		Slot slot = this.getSlot(slotIndex);

		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getItem();
		ItemStack newStack = stack.copy();

		if (slotIndex < 104) {
			if (!this.moveItemStackTo(stack, 104, this.slots.size(), true)) {
				return ItemStack.EMPTY;
			}
			slot.setChanged();
		} else if (!this.moveItemStackTo(stack, 0, 104, false)) {
			return ItemStack.EMPTY;
		}
		if (stack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return slot.onTake(player, newStack);
	}

	@Nonnull
	@Override
	public ItemStack clicked(int slot, int dragType, @Nonnull ClickType clickType, @Nonnull PlayerEntity player) {
		if (immutable || slot == blocked || clickType == ClickType.SWAP && dragType == 40 && blocked == -1) {
			return ItemStack.EMPTY;
		}
		return super.clicked(slot, dragType, clickType, player);
	}
}