package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotConsume;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotInput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotLock;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotOutput;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotUnlearn;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class TransmutationContainer extends Container
{
	public final TransmutationInventory transmutationInventory;

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
		this.addSlotToContainer(new SlotUnlearn(transmutationInventory, 10, 89, 97));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 11, 123, 30));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 12, 140, 13));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 13, 158, 9));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 14, 176, 13));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 15, 193, 30));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 16, 199, 50));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 17, 193, 70));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 18, 176, 87));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 19, 158, 91));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 20, 140, 87));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 21, 123, 70));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 22, 116, 50));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 23, 158, 31));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 24, 139, 50));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 25, 177, 50));
		this.addSlotToContainer(new SlotOutput(transmutationInventory, 26, 158, 69));

		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++) 
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 35 + j * 18, 117 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 35 + i * 18, 175));
		
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer var1)
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
		
		if (slotIndex <= 7) //Input Slots
		{
			return null;
		}
		else if (slotIndex >= 10 && slotIndex <= 25) // Output Slots
		{	
			int emc = EMCHelper.getEmcValue(newStack);
			
			int stackSize = 0;

			IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

			while (transmutationInventory.provider.getEmc() >= emc && stackSize < newStack.getMaxStackSize() && ItemHelper.hasSpace(player.inventory.mainInventory, newStack))
			{
				transmutationInventory.removeEmc(emc);
				ItemHandlerHelper.insertItemStacked(inv, ItemHelper.getNormalizedStack(stack), false);
				stackSize++;
			}
			
			transmutationInventory.updateClientTargets();
		}
		else if (slotIndex >= 26) //Unlearn Slot and Player Inventory
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
	public ItemStack slotClick(int slot, int button, ClickType flag, EntityPlayer player)
	{
		if (player.worldObj.isRemote && transmutationInventory.getHandlerForSlot(slot) == transmutationInventory.outputs)
		{
			PacketHandler.sendToServer(new SearchUpdatePKT(transmutationInventory.getIndexFromSlot(slot), getSlot(slot).getStack()));
		}

		if (slot >= 0 && getSlot(slot) != null)
		{
			if (getSlot(slot).getStack() != null && getSlot(slot).getStack().getItem() == ObjHandler.transmutationTablet
				&& getSlot(slot).getStack() == transmutationInventory.invItem)
			{
				return null;
			}
		}

		return super.slotClick(slot, button, flag, player);
	}
	
	@Override
	public boolean canDragIntoSlot(Slot slot) 
	{
		return !(slot instanceof SlotConsume || slot instanceof SlotUnlearn || slot instanceof SlotInput || slot instanceof SlotLock || slot instanceof SlotOutput);
	}
}
