package moze_intel.projecte.gameObjs.container;

import java.util.Objects;
import moze_intel.projecte.gameObjs.container.slots.ComponentSlotGhost;
import moze_intel.projecte.gameObjs.container.slots.ISlotGhost;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerCopySlot;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class MercurialEyeContainer extends PEHandContainer {

	public static MercurialEyeContainer fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
		return new MercurialEyeContainer(windowId, playerInv, buf.readEnum(InteractionHand.class), buf.readByte());
	}

	private final ComponentSlotGhost mercurialTarget;

	public MercurialEyeContainer(int windowId, Inventory playerInv, InteractionHand hand, int selected) {
		super(PEContainerTypes.MERCURIAL_EYE_CONTAINER, windowId, playerInv, hand, selected);
		IItemHandler handler = Objects.requireNonNull(this.stack.getCapability(ItemHandler.ITEM));
		//Klein Star
		this.addSlot(new InventoryContainerCopySlot(handler, 0, 50, 26));
		//Target
		this.addSlot(mercurialTarget = new ComponentSlotGhost(handler, 1, 104, 26));
		addPlayerInventory(6, 56);
	}

	@Override
	public void clickPostValidate(int slotId, int button, @NotNull ClickType flag, @NotNull Player player) {
		Slot slot = tryGetSlot(slotId);
		if (!(slot instanceof ISlotGhost ghost) || !ghost.tryClear()) {
			super.clickPostValidate(slotId, button, flag, player);
		}
	}

	@NotNull
	@Override
	public ItemStack quickMoveStack(@NotNull Player player, int slotID) {
		//If we are in the inventory start by trying to insert into the ghost slot if it isn't empty
		if (slotID > 1 && !mercurialTarget.hasItem()) {
			Slot currentSlot = tryGetSlot(slotID);
			if (currentSlot == null || !currentSlot.hasItem()) {
				return ItemStack.EMPTY;
			}
			ItemStack slotStack = currentSlot.getItem();
			if (!slotStack.isEmpty() && mercurialTarget.mayPlace(slotStack)) {
				//Fake that it is now empty, so we don't move the stack to a different spot of the inventory
				return ItemStack.EMPTY;
			}
		}
		return super.quickMoveStack(player, slotID);
	}
}