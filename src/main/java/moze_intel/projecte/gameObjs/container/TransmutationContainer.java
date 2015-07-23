package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotConsume;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotInput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotLock;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotOutput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotUnlearn;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TransmutationContainer extends Container
{
	public TransmutationInventory transmutationInventory;

	public TransmutationContainer(InventoryPlayer invPlayer, TransmutationInventory inventory)
	{
		this.transmutationInventory = inventory;
		
		// Transmutation Inventory
		this.addSlotToContainer(new SlotInput(transmutationInventory, 0, 43, 23));
		this.addSlotToContainer(new SlotInput(transmutationInventory, 1, 34, 41));
		this.addSlotToContainer(new SlotInput(transmutationInventory, 2, 52, 41));
		this.addSlotToContainer(new SlotInput(transmutationInventory, 3, 16, 50));
		this.addSlotToContainer(new SlotInput(transmutationInventory, 4, 70, 50));
		this.addSlotToContainer(new SlotInput(transmutationInventory, 5, 34, 59));
		this.addSlotToContainer(new SlotInput(transmutationInventory, 6, 52, 59));
		this.addSlotToContainer(new SlotInput(transmutationInventory, 7, 43, 77));
		this.addSlotToContainer(new SlotLock(transmutationInventory, 8, 158, 50));
		this.addSlotToContainer(new SlotConsume(transmutationInventory, 9, 107, 97));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 10, 123, 30));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 11, 140, 13));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 12, 158, 9));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 13, 176, 13));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 14, 193, 30));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 15, 199, 50));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 16, 193, 70));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 17, 176, 87));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 18, 158, 91));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 19, 140, 87));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 20, 123, 70));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 21, 116, 50));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 22, 158, 31));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 23, 139, 50));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 24, 177, 50));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 25, 158, 69));
		this.addSlotToContainer(new SlotUnlearn(transmutationInventory, 26, 89, 97));
		
		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++) 
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 35 + j * 18, 117 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 35 + i * 18, 175));
		
		transmutationInventory.openInventory();
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
			
			while (transmutationInventory.emc >= emc && stackSize < newStack.getMaxStackSize() && ItemHelper.hasSpace(player.inventory.mainInventory, newStack))
			{
				transmutationInventory.removeEmc(emc);
				ItemHelper.pushStackInInv(player.inventory, ItemHelper.getNormalizedStack(newStack));
				stackSize++;
			}
			
			transmutationInventory.updateOutputs();
		}
		else if (slotIndex >= 26)
		{
			int emc = EMCHelper.getEmcValue(stack);
			
			if (emc == 0 && stack.getItem() != ObjHandler.tome)
			{
				return null;
			}
			
			while(!transmutationInventory.hasMaxedEmc() && stack.stackSize > 0)
			{
				transmutationInventory.addEmc(emc);
				--stack.stackSize;
			}
			
			transmutationInventory.handleKnowledge(newStack);

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
		super.onContainerClosed(player);
		transmutationInventory.closeInventory();
	}
	
	@Override
	public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
	{
		if (slot >= 0 && getSlot(slot) != null)
		{
			if (getSlot(slot).getStack() != null && getSlot(slot).getStack().getItem() == ObjHandler.transmutationTablet
				&& getSlot(slot).getStack() == player.getHeldItem())
			{
				return null;
			}
		}

		return super.slotClick(slot, button, flag, player);
	}
	
	@Override
	public boolean canDragIntoSlot(Slot slot) 
	{
		if (slot instanceof SlotConsume || slot instanceof SlotUnlearn || slot instanceof SlotInput || slot instanceof SlotLock||slot instanceof SlotOutput) return false;
		return true;
	}
}
