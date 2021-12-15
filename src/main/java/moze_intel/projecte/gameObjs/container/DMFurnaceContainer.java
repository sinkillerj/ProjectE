package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.utils.ContainerHelper;
import moze_intel.projecte.utils.GuiHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.items.IItemHandler;

public class DMFurnaceContainer extends Container {

	public final DMFurnaceTile tile;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;

	public DMFurnaceContainer(ContainerTypeRegistryObject<?> type, int windowId, PlayerInventory invPlayer, DMFurnaceTile tile) {
		super(type.get(), windowId);
		this.tile = tile;
		initSlots(invPlayer);
	}

	public DMFurnaceContainer(int windowId, PlayerInventory invPlayer, DMFurnaceTile tile) {
		this(PEContainerTypes.DM_FURNACE_CONTAINER, windowId, invPlayer, tile);
	}

	public static DMFurnaceContainer fromNetwork(int windowId, PlayerInventory invPlayer, PacketBuffer buffer) {
		return new DMFurnaceContainer(windowId, invPlayer, (DMFurnaceTile) GuiHandler.getTeFromBuf(buffer));
	}

	void initSlots(PlayerInventory invPlayer) {
		IItemHandler fuel = tile.getFuel();
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Fuel Slot
		this.addSlot(new ValidatedSlot(fuel, 0, 49, 53, SlotPredicates.FURNACE_FUEL));

		//Input(0)
		this.addSlot(new ValidatedSlot(input, 0, 49, 17, stack -> !tile.getSmeltingResult(stack).isEmpty()));

		int counter = input.getSlots() - 1;

		//Input Storage
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlot(new ValidatedSlot(input, counter--, 13 + i * 18, 8 + j * 18, stack -> !tile.getSmeltingResult(stack).isEmpty()));
			}
		}

		counter = output.getSlots() - 1;

		//Output
		this.addSlot(new ValidatedSlot(output, counter--, 109, 35, s -> false));

		//OutputStorage
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				this.addSlot(new ValidatedSlot(output, counter--, 131 + i * 18, 8 + j * 18, s -> false));
			}
		}

		ContainerHelper.addPlayerInventory(this::addSlot, invPlayer, 8, 84);
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

		if (slotIndex <= 18) {
			if (!this.moveItemStackTo(stack, 19, 55, false)) {
				return ItemStack.EMPTY;
			}
		} else if (AbstractFurnaceTileEntity.isFuel(newStack) || newStack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent()) {
			if (!this.moveItemStackTo(stack, 0, 1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (!tile.getSmeltingResult(newStack).isEmpty()) {
			if (!this.moveItemStackTo(stack, 1, 10, false)) {
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

	@Override
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return player.level.getBlockState(tile.getBlockPos()).getBlock() == PEBlocks.DARK_MATTER_FURNACE.getBlock()
			   && player.distanceToSqr(tile.getBlockPos().getX() + 0.5, tile.getBlockPos().getY() + 0.5, tile.getBlockPos().getZ() + 0.5) <= 64.0;
	}

	@Override
	public void addSlotListener(@Nonnull IContainerListener par1IContainerListener) {
		super.addSlotListener(par1IContainerListener);
		par1IContainerListener.setContainerData(this, 0, tile.furnaceCookTime);
		par1IContainerListener.setContainerData(this, 1, tile.furnaceBurnTime);
		par1IContainerListener.setContainerData(this, 2, tile.currentItemBurnTime);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		for (IContainerListener crafter : this.containerListeners) {
			if (lastCookTime != tile.furnaceCookTime) {
				crafter.setContainerData(this, 0, tile.furnaceCookTime);
			}

			if (lastBurnTime != tile.furnaceBurnTime) {
				crafter.setContainerData(this, 1, tile.furnaceBurnTime);
			}

			if (lastItemBurnTime != tile.currentItemBurnTime) {
				crafter.setContainerData(this, 2, tile.currentItemBurnTime);
			}
		}

		lastCookTime = tile.furnaceCookTime;
		lastBurnTime = tile.furnaceBurnTime;
		lastItemBurnTime = tile.currentItemBurnTime;
	}

	@Override
	public void setData(int id, int data) {
		if (id == 0) {
			tile.furnaceCookTime = data;
		} else if (id == 1) {
			tile.furnaceBurnTime = data;
		} else if (id == 2) {
			tile.currentItemBurnTime = data;
		}
	}
}