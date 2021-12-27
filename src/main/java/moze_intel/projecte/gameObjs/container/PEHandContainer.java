package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.slots.HotBarSlot;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class PEHandContainer extends PEContainer {

	public final Hand hand;
	private final int selected;

	public PEHandContainer(ContainerTypeRegistryObject<? extends PEHandContainer> typeRO, int windowId, Hand hand, int selected) {
		super(typeRO, windowId);
		this.hand = hand;
		this.selected = selected;
	}

	protected ItemStack getStack(PlayerInventory invPlayer) {
		return hand == Hand.OFF_HAND ? invPlayer.player.getOffhandItem() : invPlayer.getItem(selected);
	}

	@Override
	protected HotBarSlot createHotBarSlot(@Nonnull PlayerInventory inv, int index, int x, int y) {
		// special handling to prevent removing the hand container from the player's inventory slot
		if (hand == Hand.MAIN_HAND && index == selected) {
			return new HotBarSlot(inv, index, x, y) {
				@Override
				public boolean mayPickup(@Nonnull PlayerEntity player) {
					return false;
				}
			};
		}
		return super.createHotBarSlot(inv, index, x, y);
	}

	@Override
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack clicked(int slotId, int dragType, @Nonnull ClickType clickType, @Nonnull PlayerEntity player) {
		if (clickType == ClickType.SWAP) {
			if (hand == Hand.OFF_HAND && dragType == 40) {
				//Block pressing f to swap it when it is in the offhand
				return ItemStack.EMPTY;
			} else if (hand == Hand.MAIN_HAND && dragType >= 0 && dragType < PlayerInventory.getSelectionSize()) {
				//Block taking out of the selected slot (we don't validate we have a hotbar slot as we always should for this container)
				if (!hotBarSlots.get(dragType).mayPickup(player)) {
					return ItemStack.EMPTY;
				}
			}
		}
		return clickPostValidate(slotId, dragType, clickType, player);
	}

	@Nonnull
	public ItemStack clickPostValidate(int slotId, int dragType, @Nonnull ClickType clickType, @Nonnull PlayerEntity player) {
		return super.clicked(slotId, dragType, clickType, player);
	}
}