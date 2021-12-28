package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class MercurialEyeContainer extends PEHandContainer {

	public static MercurialEyeContainer fromNetwork(int windowId, Inventory invPlayer, FriendlyByteBuf buf) {
		return new MercurialEyeContainer(windowId, invPlayer, buf.readEnum(InteractionHand.class), buf.readByte());
	}

	public MercurialEyeContainer(int windowId, Inventory invPlayer, InteractionHand hand, int selected) {
		super(PEContainerTypes.MERCURIAL_EYE_CONTAINER, windowId, hand, selected);
		IItemHandler handler = getStack(invPlayer).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);
		//Klein Star
		this.addSlot(new ValidatedSlot(handler, 0, 50, 26, SlotPredicates.EMC_HOLDER));
		//Target
		this.addSlot(new SlotGhost(handler, 1, 104, 26, SlotPredicates.MERCURIAL_TARGET));
		addPlayerInventory(invPlayer, 6, 56);
	}

	@Override
	public void clickPostValidate(int slotId, int button, @Nonnull ClickType flag, @Nonnull Player player) {
		Slot slot = tryGetSlot(slotId);
		if (slot instanceof SlotGhost && !slot.getItem().isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			super.clickPostValidate(slotId, button, flag, player);
		}
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull Player player, int slotID) {
		if (slotID > 1) {
			//If we are in the inventory start by trying to insert into the ghost slot if it isn't empty
			Slot targetSlot = slots.get(1);
			if (!targetSlot.hasItem()) {
				Slot currentSlot = slots.get(slotID);
				if (currentSlot == null || !currentSlot.hasItem()) {
					return ItemStack.EMPTY;
				}
				ItemStack slotStack = currentSlot.getItem();
				targetSlot.mayPlace(slotStack);
				//Fake that it is now empty, so we don't move the stack to a different spot of the inventory
				return ItemStack.EMPTY;
			}
		}
		return super.quickMoveStack(player, slotID);
	}
}