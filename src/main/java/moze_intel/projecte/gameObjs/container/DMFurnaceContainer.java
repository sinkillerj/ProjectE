package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class DMFurnaceContainer extends RMFurnaceContainer
{
	public DMFurnaceContainer(InventoryPlayer invPlayer, DMFurnaceTile tile)
	{
		super(invPlayer, tile);
	}

	void initSlots(InventoryPlayer invPlayer)
	{
		IItemHandler fuel = tile.getFuel();
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Fuel Slot
		this.addSlotToContainer(new ValidatedSlot(fuel, 0, 49, 53, SlotPredicates.FURNACE_FUEL));

		//Input(0)
		this.addSlotToContainer(new ValidatedSlot(input, 0, 49, 17, SlotPredicates.SMELTABLE));

		int counter = input.getSlots() - 1;

		//Input Storage
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++) {
				this.addSlotToContainer(new ValidatedSlot(input, counter--, 13 + i * 18, 8 + j * 18, SlotPredicates.SMELTABLE));
			}

		counter = output.getSlots() - 1;

		//Output
		this.addSlotToContainer(new ValidatedSlot(output, counter--, 109, 35, s -> false));

		//OutputStorage
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++) {
				this.addSlotToContainer(new ValidatedSlot(output, counter--, 131 + i * 18, 8 + j * 18, s -> false));
			}

		//Player Inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
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
			
			if (TileEntityFurnace.isItemFuel(newStack) || newStack.getItem() instanceof IItemEmc)
			{
				if (!this.mergeItemStack(stack, 0, 1, false))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!FurnaceRecipes.instance().getSmeltingResult(newStack).isEmpty())
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
	public boolean canInteractWith(@Nonnull EntityPlayer player)
	{
		return (player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.dmFurnaceOff
				|| player.world.getBlockState(tile.getPos()).getBlock() == ObjHandler.dmFurnaceOn)
				&& player.getDistanceSq(tile.getPos().getX() + 0.5, tile.getPos().getY() + 0.5, tile.getPos().getZ() + 0.5) <= 64.0;
	}
}
