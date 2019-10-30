package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.projecte.utils.ContainerHelper;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;

public class CollectorMK2Container extends CollectorMK1Container {

	public static CollectorMK2Container fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buf) {
		return new CollectorMK2Container(windowId, invPlayer, (CollectorMK2Tile) GuiHandler.getTeFromBuf(buf));
	}

	public CollectorMK2Container(int windowId, PlayerInventory invPlayer, CollectorMK2Tile collector) {
		super(ObjHandler.COLLECTOR_MK2_CONTAINER, windowId, invPlayer, collector);
	}

	@Override
	void initSlots(PlayerInventory invPlayer) {
		IItemHandler aux = tile.getAux();
		IItemHandler main = tile.getInput();

		//Klein Star Slot
		this.addSlot(new ValidatedSlot(aux, CollectorMK2Tile.UPGRADING_SLOT, 140, 58, SlotPredicates.COLLECTOR_INV));

		int counter = main.getSlots() - 1;
		//Fuel Upgrade Slot
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlot(new ValidatedSlot(main, counter--, 18 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));
			}
		}

		//Upgrade Result
		this.addSlot(new ValidatedSlot(aux, CollectorMK2Tile.UPGRADE_SLOT, 140, 13, SlotPredicates.COLLECTOR_INV));

		//Upgrade Target
		this.addSlot(new SlotGhost(aux, CollectorMK2Tile.LOCK_SLOT, 169, 36, SlotPredicates.COLLECTOR_LOCK));

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 20, 84);
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(@Nonnull PlayerEntity player, int slotIndex) {
		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.getHasStack()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();

		if (slotIndex <= 14) {
			if (!this.mergeItemStack(stack, 15, 50, false)) {
				return ItemStack.EMPTY;
			}
		} else if (slotIndex <= 50) {
			if (!FuelMapper.isStackFuel(stack) || FuelMapper.isStackMaxFuel(stack) || !this.mergeItemStack(stack, 1, 12, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			return ItemStack.EMPTY;
		}

		if (stack.isEmpty()) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			slot.onSlotChanged();
		}
		return slot.onTake(player, stack);
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player) {
		return player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.collectorMK2
			   && player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
}