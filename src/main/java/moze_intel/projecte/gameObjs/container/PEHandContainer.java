package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.slots.HotBarSlot;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.network.packets.to_client.container.SyncOffhandPkt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PEHandContainer extends PEContainer {

	public final InteractionHand hand;
	private final int selected;
	protected final ItemStack stack;
	//Note: Only used if the hand is the offhand
	private ItemStack remoteOffHand = ItemStack.EMPTY;

	protected PEHandContainer(ContainerTypeRegistryObject<? extends PEHandContainer> typeRO, int windowId, Inventory playerInv, InteractionHand hand, int selected) {
		super(typeRO, windowId, playerInv);
		this.hand = hand;
		this.selected = selected;
		this.stack = getStack();
	}

	protected ItemStack getStack() {
		return getStack(this.playerInv, this.hand, this.selected);
	}

	public static ItemStack getStack(Inventory playerInv, InteractionHand hand, int selected) {
		if (hand == null) {//Transmutation container, placed
			return ItemStack.EMPTY;
		}
		return hand == InteractionHand.OFF_HAND ? playerInv.player.getOffhandItem() : playerInv.getItem(selected);
	}

	@Override
	protected void broadcastPE(boolean all) {
		super.broadcastPE(all);
		if (hand == InteractionHand.OFF_HAND) {
			ItemStack offhandStack = getStack();
			if (!ItemStack.matches(offhandStack, remoteOffHand)) {
				remoteOffHand = offhandStack.copy();
				syncDataChange(new SyncOffhandPkt((short) containerId, remoteOffHand));
			}
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
	public void clicked(int slotId, int dragType, @NotNull ContainerInput clickType, @NotNull Player player) {
		if (clickType == ContainerInput.SWAP) {
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

	public void clickPostValidate(int slotId, int dragType, @NotNull ContainerInput clickType, @NotNull Player player) {
		super.clicked(slotId, dragType, clickType, player);
	}
}
