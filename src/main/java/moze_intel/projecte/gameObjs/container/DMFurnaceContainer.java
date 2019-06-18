package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class DMFurnaceContainer extends RMFurnaceContainer
{
	public DMFurnaceContainer(PlayerInventory invPlayer, DMFurnaceTile tile)
	{
		super(invPlayer, tile);
	}

	void initSlots(PlayerInventory invPlayer)
	{
		IItemHandler fuel = tile.getFuel();
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Fuel Slot
		this.addSlot(new ValidatedSlot(fuel, 0, 49, 53, SlotPredicates.FURNACE_FUEL));

		//Input(0)
		this.addSlot(new ValidatedSlot(input, 0, 49, 17, stack -> !tile.getSmeltingResult(stack).isEmpty()));

		int counter = input.getSlots() - 1;

		//Input Storage
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++) {
				this.addSlot(new ValidatedSlot(input, counter--, 13 + i * 18, 8 + j * 18, stack -> !tile.getSmeltingResult(stack).isEmpty()));
			}

		counter = output.getSlots() - 1;

		//Output
		this.addSlot(new ValidatedSlot(output, counter--, 109, 35, s -> false));

		//OutputStorage
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++) {
				this.addSlot(new ValidatedSlot(output, counter--, 131 + i * 18, 8 + j * 18, s -> false));
			}

		//Player Inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142));
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex)
	{
		Slot slot = this.getSlot(slotIndex);
		
		if (slot == null || !slot.getHasStack()) 
		{
			return ItemStack.EMPTY;
		}
		
		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();
		
		if (slotIndex <= 18)
		{
			if (!this.mergeItemStack(stack, 19, 55, false))
			{
				return ItemStack.EMPTY;
			}
		}
		else
		{
			
			if (FurnaceTileEntity.isItemFuel(newStack) || newStack.getItem() instanceof IItemEmc)
			{
				if (!this.mergeItemStack(stack, 0, 1, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!tile.getSmeltingResult(newStack).isEmpty())
			{
				if (!this.mergeItemStack(stack, 1, 10, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else
			{
				return ItemStack.EMPTY;
			}
		}
		
		if (stack.isEmpty())
		{
			slot.putStack(ItemStack.EMPTY);
		}
		else
		{
			slot.onSlotChanged();
		}
		
		return newStack;
	}

	@Override
	public boolean canInteractWith(@Nonnull PlayerEntity player)
	{
		return player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.dmFurnaceOff
				&& player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
}
