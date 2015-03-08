package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.inventory.MercurialEyeInventory;
import moze_intel.projecte.gameObjs.container.slots.mercurial.SlotMercurialKlein;
import moze_intel.projecte.gameObjs.container.slots.mercurial.SlotMercurialTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class MercurialEyeContainer extends Container
{
	private MercurialEyeInventory inventory;
	
	public MercurialEyeContainer(InventoryPlayer invPlayer, MercurialEyeInventory mercEyeInv)
	{
		inventory = mercEyeInv;
		
		//Klein Star
		this.addSlotToContainer(new SlotMercurialKlein(inventory, 0, 50, 26));
		
		//Target
		this.addSlotToContainer(new SlotMercurialTarget(inventory, 1, 104, 26));
		
		//Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 6 + j * 18, 56 + i * 18));
		
		//Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 6 + i * 18, 114));
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
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
		
		return super.slotClick(slot, button, flag, player);
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

		if (slotIndex < 2) // Moving to player inventory
		{
			if (!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true))
				return null;
			slot.onSlotChanged();
		}
		else // Moving from player inventory
		{
			if (((Slot)inventorySlots.get(0)).isItemValid(stack) && ((Slot)inventorySlots.get(0)).getStack() == null)
			{ // Is a valid klein star and the slot is empty?
				((Slot)inventorySlots.get(0)).putStack(stack.splitStack(1));
			}
			else if (((Slot)inventorySlots.get(1)).isItemValid(stack) && ((Slot)inventorySlots.get(1)).getStack() == null)
			{ // Is a valid target block and the slot is empty?
				((Slot)inventorySlots.get(1)).putStack(stack.splitStack(1));
			}
			else // Is neither, ignore
			{
				return null;
			}

		}
		if (stack.stackSize == 0)
		{
			slot.putStack((ItemStack) null);
		}
		else
		{
			slot.onSlotChanged();
		}

		slot.onPickupFromSlot(player, newStack);
		return newStack;
	}
}
