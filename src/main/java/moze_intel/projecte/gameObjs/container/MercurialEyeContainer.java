package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class MercurialEyeContainer extends PEHandContainer {

	public static MercurialEyeContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf) {
		return new MercurialEyeContainer(windowId, invPlayer, buf.readEnum(Hand.class), buf.readByte());
	}

	public MercurialEyeContainer(int windowId, PlayerInventory invPlayer, Hand hand, int selected) {
		super(PEContainerTypes.MERCURIAL_EYE_CONTAINER, windowId, hand, selected);
		IItemHandler handler = getStack(invPlayer).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);
		//Klein Star
		this.addSlot(new ValidatedSlot(handler, 0, 50, 26, SlotPredicates.EMC_HOLDER));
		//Target
		this.addSlot(new SlotGhost(handler, 1, 104, 26, SlotPredicates.MERCURIAL_TARGET));
		addPlayerInventory(invPlayer, 6, 56);
	}

	@Nonnull
	@Override
	public ItemStack clickPostValidate(int slotId, int button, @Nonnull ClickType flag, @Nonnull PlayerEntity player) {
		Slot slot = tryGetSlot(slotId);
		if (slot instanceof SlotGhost && !slot.getItem().isEmpty()) {
			slot.set(ItemStack.EMPTY);
			return ItemStack.EMPTY;
		}
		return super.clickPostValidate(slotId, button, flag, player);
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotID) {
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