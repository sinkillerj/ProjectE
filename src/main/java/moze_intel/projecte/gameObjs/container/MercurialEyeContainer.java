package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class MercurialEyeContainer extends PEHandContainer {

	public static MercurialEyeContainer fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
		return new MercurialEyeContainer(windowId, playerInv, buf.readEnum(InteractionHand.class), buf.readByte());
	}

	private final SlotGhost mercurialTarget;

	public MercurialEyeContainer(int windowId, Inventory playerInv, InteractionHand hand, int selected) {
		super(PEContainerTypes.MERCURIAL_EYE_CONTAINER, windowId, playerInv, hand, selected);
		IItemHandler handler = getStack().getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(NullPointerException::new);
		//Klein Star
		this.addSlot(new ValidatedSlot(handler, 0, 50, 26, SlotPredicates.EMC_HOLDER));
		//Target
		this.addSlot(mercurialTarget = new SlotGhost(handler, 1, 104, 26, SlotPredicates.MERCURIAL_TARGET));
		addPlayerInventory(6, 56);
	}

	@Override
	public void clickPostValidate(int slotId, int button, @NotNull ClickType flag, @NotNull Player player) {
		Slot slot = tryGetSlot(slotId);
		if (slot instanceof SlotGhost && !slot.getItem().isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			super.clickPostValidate(slotId, button, flag, player);
		}
	}

	@NotNull
	@Override
	public ItemStack quickMoveStack(@NotNull Player player, int slotID) {
		//If we are in the inventory start by trying to insert into the ghost slot if it isn't empty
		if (slotID > 1 && !mercurialTarget.hasItem()) {
			Slot currentSlot = slots.get(slotID);
			if (currentSlot == null || !currentSlot.hasItem()) {
				return ItemStack.EMPTY;
			}
			ItemStack slotStack = currentSlot.getItem();
			if (!slotStack.isEmpty() && mercurialTarget.isValid(slotStack)) {
				mercurialTarget.set(slotStack);
				//Fake that it is now empty, so we don't move the stack to a different spot of the inventory
				return ItemStack.EMPTY;
			}
		}
		return super.quickMoveStack(player, slotID);
	}
}