package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PedestalContainer extends Container
{
	private final DMPedestalTile tile;
	public PedestalContainer(InventoryPlayer inventory, DMPedestalTile tile)
	{
		this.tile = tile;

		//Pedestal Inventory
		this.addSlotToContainer(new Slot(tile, 0, 80, 20));

		int slotIndex = -1;
		//Player Hotbar
		for (int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(inventory, ++slotIndex, 18 * i + 8, 112));
		}
		//Player Inventory
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(inventory, ++slotIndex, 18 * j + 8, 18 * i + 54));
			}
		}
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

		if (slotIndex == 0)
		{
			if (!this.mergeItemStack(stack, 1, this.inventorySlots.size(), false))
				return null;
			slot.onSlotChanged();
		}
		else if (!this.mergeItemStack(stack, 0, 1, false))
		{
			return null;
		}
		if (stack.stackSize == 0)
		{
			slot.putStack(null);
		}
		else
		{
			slot.onSlotChanged();
		}

		slot.onPickupFromSlot(player, newStack);
		return newStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5) <= 64.0;
	}
}
