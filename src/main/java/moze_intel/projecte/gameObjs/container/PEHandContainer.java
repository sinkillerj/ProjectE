package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.slots.HotBarSlot;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

public class PEHandContainer extends PEContainer {

	public final InteractionHand hand;
	private final int selected;

	public PEHandContainer(ContainerTypeRegistryObject<? extends PEHandContainer> typeRO, int windowId, InteractionHand hand, int selected) {
		super(typeRO, windowId);
		this.hand = hand;
		this.selected = selected;
	}

	protected ItemStack getStack(Inventory invPlayer) {
		return hand == InteractionHand.OFF_HAND ? invPlayer.player.getOffhandItem() : invPlayer.getItem(selected);
	}

	@Override
	protected HotBarSlot createHotBarSlot(@Nonnull Inventory inv, int index, int x, int y) {
		// special handling to prevent removing the hand container from the player's inventory slot
		if (hand == InteractionHand.MAIN_HAND && index == selected) {
			return new HotBarSlot(inv, index, x, y) {
				@Override
				public boolean mayPickup(@Nonnull Player player) {
					return false;
				}
			};
		}
		return super.createHotBarSlot(inv, index, x, y);
	}

	@Override
	public boolean stillValid(@Nonnull Player player) {
		return true;
	}

	@Override
	public void clicked(int slotId, int dragType, @Nonnull ClickType clickType, @Nonnull Player player) {
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

	public void clickPostValidate(int slotId, int dragType, @Nonnull ClickType clickType, @Nonnull Player player) {
		super.clicked(slotId, dragType, clickType, player);
	}
}