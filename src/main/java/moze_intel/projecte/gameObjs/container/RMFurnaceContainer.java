package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
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
		super(PEContainerTypes.RM_FURNACE_CONTAINER, windowId, invPlayer, tile);
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
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return player.level.getBlockState(tile.getBlockPos()).getBlock() == PEBlocks.RED_MATTER_FURNACE.getBlock()
			   && player.distanceToSqr(tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5, tile.getBlockPos().getZ() + 0.5) <= 64.0;
	}

	@Nonnull
	@Override
	public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int slotIndex) {
		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.hasItem()) {
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getItem();
		ItemStack newStack = stack.copy();

		if (slotIndex <= 26) {
			if (!this.moveItemStackTo(stack, 27, 63, false)) {
				return ItemStack.EMPTY;
			}
		} else if (AbstractFurnaceTileEntity.isFuel(newStack) || newStack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent()) {
			if (!this.moveItemStackTo(stack, 0, 1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (!tile.getSmeltingResult(newStack).isEmpty()) {
			if (!this.moveItemStackTo(stack, 1, 14, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			return ItemStack.EMPTY;
		}
		if (stack.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return newStack;
	}
}