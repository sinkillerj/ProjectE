package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.gameObjs.container.slots.transmuteportable.SlotTabletConsume;
import moze_intel.projecte.gameObjs.container.slots.transmuteportable.SlotTabletInput;
import moze_intel.projecte.gameObjs.container.slots.transmuteportable.SlotTabletUnlearn;
import moze_intel.projecte.gameObjs.container.slots.transmuteportable.SlotTabletLock;
import moze_intel.projecte.gameObjs.container.slots.transmuteportable.SlotTabletOutput;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TransmuteTabletContainer extends Container
{
	public TransmuteTabletInventory table;

	public TransmuteTabletContainer(InventoryPlayer invPlayer, TransmuteTabletInventory inventory)
	{
		this.table = inventory;
		
		//Tablet Inventory
		this.addSlotToContainer(new SlotTabletInput(table, 0, 43, 23));
		this.addSlotToContainer(new SlotTabletInput(table, 1, 34, 41));
		this.addSlotToContainer(new SlotTabletInput(table, 2, 52, 41));
		this.addSlotToContainer(new SlotTabletInput(table, 3, 16, 50));
		this.addSlotToContainer(new SlotTabletInput(table, 4, 70, 50));
		this.addSlotToContainer(new SlotTabletInput(table, 5, 34, 59));
		this.addSlotToContainer(new SlotTabletInput(table, 6, 52, 59));
		this.addSlotToContainer(new SlotTabletInput(table, 7, 43, 77));
		this.addSlotToContainer(new SlotTabletLock(table, 8, 158, 50));
		this.addSlotToContainer(new SlotTabletConsume(table, 9, 107, 97));
		this.addSlotToContainer(new SlotTabletOutput(table, 10, 123, 30));
		this.addSlotToContainer(new SlotTabletOutput(table, 11, 140, 13));
		this.addSlotToContainer(new SlotTabletOutput(table, 12, 158, 9));
		this.addSlotToContainer(new SlotTabletOutput(table, 13, 176, 13));
		this.addSlotToContainer(new SlotTabletOutput(table, 14, 193, 30));
		this.addSlotToContainer(new SlotTabletOutput(table, 15, 199, 50));
		this.addSlotToContainer(new SlotTabletOutput(table, 16, 193, 70));
		this.addSlotToContainer(new SlotTabletOutput(table, 17, 176, 87));
		this.addSlotToContainer(new SlotTabletOutput(table, 18, 158, 91));
		this.addSlotToContainer(new SlotTabletOutput(table, 19, 140, 87));
		this.addSlotToContainer(new SlotTabletOutput(table, 20, 123, 70));
		this.addSlotToContainer(new SlotTabletOutput(table, 21, 116, 50));
		this.addSlotToContainer(new SlotTabletOutput(table, 22, 158, 31));
		this.addSlotToContainer(new SlotTabletOutput(table, 23, 139, 50));
		this.addSlotToContainer(new SlotTabletOutput(table, 24, 177, 50));
		this.addSlotToContainer(new SlotTabletOutput(table, 25, 158, 69));
		this.addSlotToContainer(new SlotTabletUnlearn(table, 26, 89, 97));
		
		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++) 
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 35 + j * 18, 117 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 35 + i * 18, 175));
		
		table.openInventory();
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) 
	{
		return true;
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
		
		if (slotIndex <= 7)
		{
			return null;
		}
		else if (slotIndex >= 10 && slotIndex <= 25)
		{	
			int emc = EMCHelper.getEmcValue(newStack);
			
			int stackSize = 0;
			
			while (table.emc >= emc && stackSize < newStack.getMaxStackSize() && ItemHelper.hasSpace(player.inventory.mainInventory, newStack))
			{
				table.removeEmc(emc);
				ItemHelper.pushStackInInv(player.inventory, ItemHelper.getNormalizedStack(newStack));
				stackSize++;
			}
			
			table.updateOutputs();
		}
		else if (slotIndex >= 26)
		{
			int emc = EMCHelper.getEmcValue(stack);
			
			if (emc == 0)
			{
				return null;
			}
			
			while(!table.hasMaxedEmc() && stack.stackSize > 0)
			{
				table.addEmc(emc);
				--stack.stackSize;
			}
			
			table.handleKnowledge(newStack);

			if (stack.stackSize == 0)
			{
				slot.putStack(null);
			}
		}
		
		return null;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		table.closeInventory();
		
		super.onContainerClosed(player);
	}
	
	@Override
	public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
	{
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem())
		{
			return null;
		}

		return super.slotClick(slot, button, flag, player);
	}
	
	@Override
	public boolean canDragIntoSlot(Slot slot) 
	{
		return false;
	}
}
