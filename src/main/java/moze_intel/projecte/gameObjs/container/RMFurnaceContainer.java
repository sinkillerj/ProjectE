package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.utils.ContainerHelper;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.items.IItemHandler;

public class RMFurnaceContainer extends DMFurnaceContainer {

	public RMFurnaceContainer(int windowId, PlayerInventory invPlayer, RMFurnaceTile tile) {
		super(ObjHandler.RM_FURNACE_CONTAINER, windowId, invPlayer, tile);
	}

	public static RMFurnaceContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buffer) {
		return new RMFurnaceContainer(windowId, invPlayer, (RMFurnaceTile) GuiHandler.getTeFromBuf(buffer));
	}

	@Override
	void initSlots(PlayerInventory invPlayer) {
		IItemHandler fuel = tile.getFuel();
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Fuel
		this.addSlot(new ValidatedSlot(fuel, 0, 65, 53, SlotPredicates.FURNACE_FUEL));

		//Input(0)
		this.addSlot(new ValidatedSlot(input, 0, 65, 17, stack -> !tile.getSmeltingResult(stack).isEmpty()));

		int counter = input.getSlots() - 1;

		//Input storage
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlot(new ValidatedSlot(input, counter--, 11 + i * 18, 8 + j * 18, stack -> !tile.getSmeltingResult(stack).isEmpty()));
			}
		}

		counter = output.getSlots() - 1;

		//Output(0)
		this.addSlot(new ValidatedSlot(output, counter--, 125, 35, s -> false));

		//Output Storage
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlot(new ValidatedSlot(output, counter--, 147 + i * 18, 8 + j * 18, s -> false));
			}
		}

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 24, 84);
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player) {
		return player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.rmFurnace
			   && player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
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

		if (slotIndex <= 26) {
			if (!this.mergeItemStack(stack, 27, 63, false)) {
				return ItemStack.EMPTY;
			}
		} else if (AbstractFurnaceTileEntity.isFuel(newStack) || newStack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent()) {
			if (!this.mergeItemStack(stack, 0, 1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (!tile.getSmeltingResult(newStack).isEmpty()) {
			if (!this.mergeItemStack(stack, 1, 14, false)) {
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
		return newStack;
	}
}