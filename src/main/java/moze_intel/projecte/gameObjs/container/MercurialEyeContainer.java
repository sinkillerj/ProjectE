package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.inventory.MercurialEyeInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class MercurialEyeContainer extends Container
{
	private final MercurialEyeInventory inventory;
	
	public MercurialEyeContainer(InventoryPlayer invPlayer, MercurialEyeInventory mercEyeInv)
	{
		inventory = mercEyeInv;

		//Klein Star
		this.addSlotToContainer(new ValidatedSlot(inventory, 0, 50, 26, SlotPredicates.IITEMEMC));

		//Target
		this.addSlotToContainer(new SlotGhost(inventory, 1, 104, 26, SlotPredicates.MERCURIAL_TARGET));
		
		//Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 6 + j * 18, 56 + i * 18));
		
		//Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 6 + i * 18, 114));
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer var1)
	{
		return true;
	}
	
	@Override
	public ItemStack slotClick(int slot, int button, ClickType flag, EntityPlayer player)
	{
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == inventory.invItem)
		{
			return null;
		}

		if (slot == 1 && inventory.getStackInSlot(slot) != null)
		{
			inventory.setStackInSlot(1, null);
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
			if (inventorySlots.get(0).isItemValid(stack) && inventorySlots.get(0).getStack() == null)
			{ // Is a valid klein star and the slot is empty?
				inventorySlots.get(0).putStack(stack.splitStack(1));
			}
			else if (inventorySlots.get(1).isItemValid(stack) && inventorySlots.get(1).getStack() == null)
			{ // Is a valid target block and the slot is empty?
				inventorySlots.get(1).putStack(stack.splitStack(1));
			}
			else // Is neither, ignore
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

		slot.onPickupFromSlot(player, newStack);
		return newStack;
	}
}
