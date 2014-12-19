package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.trasmute.SlotTableConsume;
import moze_intel.projecte.gameObjs.container.slots.trasmute.SlotTableInput;
import moze_intel.projecte.gameObjs.container.slots.trasmute.SlotTableLock;
import moze_intel.projecte.gameObjs.container.slots.trasmute.SlotTableOutput;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class TransmuteContainer extends Container
{
	public TransmuteTile tile;

	public TransmuteContainer(InventoryPlayer invPlayer, TransmuteTile tile)
	{
		this.tile = tile;

		//Tablet Inventory
		this.addSlotToContainer(new SlotTableInput(this.tile, 0, 43, 29));
		this.addSlotToContainer(new SlotTableInput(this.tile, 1, 34, 47));
		this.addSlotToContainer(new SlotTableInput(this.tile, 2, 52, 47));
		this.addSlotToContainer(new SlotTableInput(this.tile, 3, 16, 56));
		this.addSlotToContainer(new SlotTableInput(this.tile, 4, 70, 56));
		this.addSlotToContainer(new SlotTableInput(this.tile, 5, 34, 65));
		this.addSlotToContainer(new SlotTableInput(this.tile, 6, 52, 65));
		this.addSlotToContainer(new SlotTableInput(this.tile, 7, 43, 83));
		this.addSlotToContainer(new SlotTableLock(this.tile, 8, 158, 56));
		this.addSlotToContainer(new SlotTableConsume(this.tile, 9, 107, 103));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 10, 123, 36));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 11, 140, 19));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 12, 158, 15));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 13, 176, 19));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 14, 193, 36));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 15, 199, 56));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 16, 193, 76));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 17, 176, 93));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 18, 158, 97));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 19, 140, 93));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 20, 123, 76));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 21, 116, 56));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 22, 158, 37));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 23, 139, 56));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 24, 177, 56));
		this.addSlotToContainer(new SlotTableOutput(this.tile, 25, 158, 75));

		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 35 + j * 18, 123 + i * 18));

		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 35 + i * 18, 181));

		this.tile.openInventory();
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
			
			while (tile.getStoredEmc() >= emc && stackSize < newStack.getMaxStackSize() && Utils.hasSpace(player.inventory, newStack))
			{
				tile.removeEmc(emc);
				Utils.pushStackInInv(player.inventory, Utils.getNormalizedStack(newStack));
				stackSize++;
			}
			
			tile.updateOutputs();
		}
		else if (slotIndex >= 26)
		{
			int emc = Utils.getEmcValue(stack);
			
			if (emc == 0)
			{
				return null;
			}
			
			while(!tile.hasMaxedEmc() && stack.stackSize > 0)
			{
				tile.addEmc(emc);
				--stack.stackSize;
			}
			
			tile.handleKnowledge(newStack);
			
			if (stack.getItem() == ObjHandler.kleinStars)
			{
				tile.addEmc(KleinStar.getEmc(stack));
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
		super.onContainerClosed(player);
		tile.closeInventory();
	}
	
	@Override
	public boolean canDragIntoSlot(Slot slot) 
	{
		return false;
	}
}
