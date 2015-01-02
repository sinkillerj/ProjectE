package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.gameObjs.container.slots.transmuteportable.SlotTabletConsume;
import moze_intel.projecte.gameObjs.container.slots.transmuteportable.SlotTabletInput;
import moze_intel.projecte.gameObjs.container.slots.transmuteportable.SlotTabletLock;
import moze_intel.projecte.gameObjs.container.slots.transmuteportable.SlotTabletOutput;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.utils.Utils;
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
		this.addSlotToContainer(new SlotTabletInput(table, 0, 43, 29));
		this.addSlotToContainer(new SlotTabletInput(table, 1, 34, 47));
		this.addSlotToContainer(new SlotTabletInput(table, 2, 52, 47));
		this.addSlotToContainer(new SlotTabletInput(table, 3, 16, 56));
		this.addSlotToContainer(new SlotTabletInput(table, 4, 70, 56));
		this.addSlotToContainer(new SlotTabletInput(table, 5, 34, 65));
		this.addSlotToContainer(new SlotTabletInput(table, 6, 52, 65));
		this.addSlotToContainer(new SlotTabletInput(table, 7, 43, 83));
		this.addSlotToContainer(new SlotTabletLock(table, 8, 158, 56));
		this.addSlotToContainer(new SlotTabletConsume(table, 9, 107, 103));
		this.addSlotToContainer(new SlotTabletOutput(table, 10, 123, 36));
		this.addSlotToContainer(new SlotTabletOutput(table, 11, 140, 19));
		this.addSlotToContainer(new SlotTabletOutput(table, 12, 158, 15));
		this.addSlotToContainer(new SlotTabletOutput(table, 13, 176, 19));
		this.addSlotToContainer(new SlotTabletOutput(table, 14, 193, 36));
		this.addSlotToContainer(new SlotTabletOutput(table, 15, 199, 56));
		this.addSlotToContainer(new SlotTabletOutput(table, 16, 193, 76));
		this.addSlotToContainer(new SlotTabletOutput(table, 17, 176, 93));
		this.addSlotToContainer(new SlotTabletOutput(table, 18, 158, 97));
		this.addSlotToContainer(new SlotTabletOutput(table, 19, 140, 93));
		this.addSlotToContainer(new SlotTabletOutput(table, 20, 123, 76));
		this.addSlotToContainer(new SlotTabletOutput(table, 21, 116, 56));
		this.addSlotToContainer(new SlotTabletOutput(table, 22, 158, 37));
		this.addSlotToContainer(new SlotTabletOutput(table, 23, 139, 56));
		this.addSlotToContainer(new SlotTabletOutput(table, 24, 177, 56));
		this.addSlotToContainer(new SlotTabletOutput(table, 25, 158, 75));
		
		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++) 
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 35 + j * 18, 123 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 35 + i * 18, 181));
		
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
			int emc = Utils.getEmcValue(newStack);
			
			int stackSize = 0;
			
			while (table.emc >= emc && stackSize < newStack.getMaxStackSize() && Utils.hasSpace(player.inventory.mainInventory, newStack))
			{
				table.removeEmc(emc);
				Utils.pushStackInInv(player.inventory, Utils.getNormalizedStack(newStack));
				stackSize++;
			}
			
			table.updateOutputs();
		}
		else if (slotIndex >= 26)
		{
			int emc = Utils.getEmcValue(stack);
			
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
			
			if (stack.getItem() == ObjHandler.kleinStars)
			{
				table.addEmc(KleinStar.getEmc(stack));
			}
			
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
