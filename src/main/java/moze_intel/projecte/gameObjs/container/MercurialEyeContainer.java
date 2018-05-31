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

	@Nonnull
	@Override
	public ItemStack slotClick(int slot, int button, ClickType flag, EntityPlayer player)
	{
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == inventory.invItem)
		{
			return ItemStack.EMPTY;
		}

		if (slot == 1 && !inventory.getStackInSlot(slot).isEmpty())
		{
			inventory.setStackInSlot(1, ItemStack.EMPTY);
		}
		
		return super.slotClick(slot, button, flag, player);
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

		if (slotIndex < 2) // Moving to player inventory
		{
			if (!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true))
				return ItemStack.EMPTY;
			slot.onSlotChanged();
		}
		else // Moving from player inventory
		{
			if (inventorySlots.get(0).isItemValid(stack) && inventorySlots.get(0).getStack().isEmpty())
			{ // Is a valid klein star and the slot is empty?
				inventorySlots.get(0).putStack(stack.splitStack(1));
			}
			else if (inventorySlots.get(1).isItemValid(stack) && inventorySlots.get(1).getStack().isEmpty())
			{ // Is a valid target block and the slot is empty?
				inventorySlots.get(1).putStack(stack.splitStack(1));
			}
			else // Is neither, ignore
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

		return slot.onTake(player, newStack);
	}
}
