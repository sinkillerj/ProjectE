package moze_intel.projecte.gameObjs.container;

import invtweaks.api.container.ChestContainer;
import moze_intel.projecte.gameObjs.container.inventory.AlchBagInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

@ChestContainer(isLargeChest = true, rowSize = 13)
public class AlchBagContainer extends Container
{
	public final AlchBagInventory inventory;
	
	public AlchBagContainer(InventoryPlayer invPlayer, AlchBagInventory invBag)
	{
		inventory = invBag;

		//Bag Inventory
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 13; j++)
				this.addSlotToContainer(new SlotItemHandler(inventory, j + i * 13, 12 + j * 18, 5 + i * 18));
				
		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++) 
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 152 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 48 + i * 18, 210));
	}
	
	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer player)
	{
		return true;
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
		
		if (slotIndex < 104)
		{
			if (!this.mergeItemStack(stack, 104, this.inventorySlots.size(), true))
				return ItemStack.EMPTY;
			slot.onSlotChanged();
		}
		else if (!this.mergeItemStack(stack, 0, 104, false))
		{
			return ItemStack.EMPTY;
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

	@Nonnull
	@Override
	public ItemStack slotClick(int slot, int button, ClickType flag, EntityPlayer player)
	{
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == inventory.invItem)
		{
			return ItemStack.EMPTY;
		}
		
		return super.slotClick(slot, button, flag, player);
	}
}
