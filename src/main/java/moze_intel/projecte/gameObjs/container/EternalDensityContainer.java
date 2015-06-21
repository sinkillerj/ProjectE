package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.inventory.EternalDensityInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class EternalDensityContainer extends Container
{
	private EternalDensityInventory inventory;
	
	public EternalDensityContainer(InventoryPlayer invPlayer, EternalDensityInventory gemInv)
	{
		inventory = gemInv;
		
		 for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j)
			{
				this.addSlotToContainer(new SlotGhost(gemInv, j + i * 3, 62 + j * 18, 26 + i * 18));
			}

		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 93 + i * 18));
			}

		for (int i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 151));
		}

	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		Slot slot = getSlot(slotIndex);
		if (slotIndex > 8)
		{
			int index = inventory.findFirstEmptySlot();
			if (index != -1)
			{
				ItemStack toSet = slot.getStack().copy();
				toSet.stackSize = 1;
				inventory.setInventorySlotContents(index, toSet);
			}
		}
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) 
	{
		return true;
	}
	
	@Override
	public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
	{
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem()) 
		{
			return null;
		}
		
		if (slot >= 0 && slot < 9)
		{
			inventory.setInventorySlotContents(slot, null);
		}
		
		return super.slotClick(slot, button, flag, player);
	}
	
	@Override
	public boolean canDragIntoSlot(Slot slot) 
	{
		return false;
	}
}
