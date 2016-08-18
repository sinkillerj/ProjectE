package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

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
		IItemHandler output = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);

		//Fuel Slot
		this.addSlotToContainer(new SlotItemHandler(fuel, 0, 49, 53));

		int counter = 0;

		//Input(0)
		this.addSlotToContainer(new SlotItemHandler(input, counter++, 49, 17));

		//Input Storage
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++) {
				PELogger.logInfo(Integer.toString(counter));
				this.addSlotToContainer(new SlotItemHandler(input, counter++, 13 + i * 18, 8 + j * 18));
			}

		counter = output.getSlots() - 1;

		//Output
		this.addSlotToContainer(new SlotItemHandler(output, counter--, 109, 35));

		//OutputStorage
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++) {
				PELogger.logInfo(Integer.toString(counter));
				this.addSlotToContainer(new SlotItemHandler(output, counter--, 131 + i * 18, 8 + j * 18));
			}

		//Player Inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		Slot slot = this.getSlot(slotIndex);
		
		if (slot == null || !slot.getHasStack()) 
		{
			return null;
		}
		
		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();
		
		if (slotIndex <= 18)
		{
			if (!this.mergeItemStack(stack, 19, 55, false))
			{
				return null;
			}
		}
		else
		{
			
			if (TileEntityFurnace.isItemFuel(newStack) || newStack.getItem() instanceof IItemEmc)
			{
				if (!this.mergeItemStack(stack, 0, 1, false))
				{
					return null;
				}
			}
			else if (FurnaceRecipes.instance().getSmeltingResult(newStack) != null)
			{
				if (!this.mergeItemStack(stack, 1, 10, false))
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
		
		if (stack.stackSize == 0)
		{
			slot.putStack(null);
		}
		else
		{
			slot.onSlotChanged();
		}
		
		return newStack;
	}
}
