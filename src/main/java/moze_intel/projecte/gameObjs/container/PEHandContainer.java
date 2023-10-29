package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.slots.HotBarSlot;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PEHandContainer extends PEContainer {

	public final InteractionHand hand;
	private final int selected;
	protected final ItemStack stack;

	protected PEHandContainer(ContainerTypeRegistryObject<? extends PEHandContainer> typeRO, int windowId, Inventory playerInv, InteractionHand hand, int selected) {
		super(typeRO, windowId, playerInv);
		this.hand = hand;
		this.selected = selected;
		if (this.hand == null) {//Transmutation container, placed
			this.stack = ItemStack.EMPTY;
		} else {
			this.stack = this.hand == InteractionHand.OFF_HAND ? this.playerInv.player.getOffhandItem() : this.playerInv.getItem(selected);
		}
	}

	@Override
	protected HotBarSlot createHotBarSlot(@NotNull Inventory inv, int index, int x, int y) {
		// special handling to prevent removing the hand container from the player's inventory slot
		if (hand == InteractionHand.MAIN_HAND && index == selected) {
			return new HotBarSlot(inv, index, x, y) {
				@Override
				public boolean mayPickup(@NotNull Player player) {
					return false;
				}
			};
		}
		return super.createHotBarSlot(inv, index, x, y);
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return this.hand == null || !this.stack.isEmpty() && player.getItemInHand(this.hand).is(this.stack.getItem());
	}

	@Override
	public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
		if (clickType == ClickType.SWAP) {
			if (hand == InteractionHand.OFF_HAND && dragType == 40) {
				//Block pressing f to swap it when it is in the offhand
				return;
			} else if (hand == InteractionHand.MAIN_HAND && dragType >= 0 && dragType < Inventory.getSelectionSize()) {
				//Block taking out of the selected slot (we don't validate we have a hotbar slot as we always should for this container)
				if (!hotBarSlots.get(dragType).mayPickup(player)) {
					return;
				}
			}
		}
		clickPostValidate(slotId, dragType, clickType, player);
	}

	public void clickPostValidate(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
		super.clicked(slotId, dragType, clickType, player);
	}
}