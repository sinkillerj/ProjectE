package moze_intel.gameObjs.container;

import moze_intel.gameObjs.container.slots.SlotCondenserInput;
import moze_intel.gameObjs.container.slots.SlotCondenserLock;
import moze_intel.gameObjs.tiles.CondenserTile;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CondenserContainer extends Container
{	
	private CondenserTile tile;
	private int storedEmc;
	private int requiredEmc;
	
	public CondenserContainer(InventoryPlayer invPlayer, CondenserTile condenser)
	{
		tile = condenser;
		tile.openInventory();
		
		//Item Lock Slot
		this.addSlotToContainer(new SlotCondenserLock(tile, 0, 12, 6));
		
		//Condenser Inventory
		for (int i = 0; i < 7; i++) 
		      for (int j = 0; j < 13; j++)
		    	  this.addSlotToContainer(new SlotCondenserInput(tile, 1 + j + i * 13, 12 + j * 18, 26 + i * 18));
		    	  
		//Player Inventory
		for(int i = 0; i < 3; i++)
			  for(int j = 0; j < 9; j++) 
			        this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 154 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 48 + i * 18, 212));
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
		
		if (slotIndex < 105)
		{
			if (!this.mergeItemStack(stack, 105, this.inventorySlots.size(), false))
				return null;
		}
		else if (!Utils.DoesItemHaveEmc(stack) || !this.mergeItemStack(stack, 1, 105, false))
		{
			return null;
		}
		
		if (stack.stackSize == 0)
		{
			slot.putStack((ItemStack) null);
		}
		
		else slot.onSlotChanged();
		slot.onPickupFromSlot(player, stack);
		return newStack;
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting)
    {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, tile.displayEmc);
        par1ICrafting.sendProgressBarUpdate(this, 1, tile.requiredEmc);
    }
	
	@Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        
        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (storedEmc != tile.GetStoredEMC())
            {
            	icrafting.sendProgressBarUpdate(this, 0, (int) tile.GetStoredEMC());
            }
            
            if (requiredEmc != tile.requiredEmc)
            {
            	icrafting.sendProgressBarUpdate(this, 1, tile.requiredEmc);
            }
        }
        
        storedEmc = (int) tile.GetStoredEMC();
        requiredEmc = tile.requiredEmc;
    }
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            tile.displayEmc = par2;
        }
        
        if (par1 == 1)
        {
        	tile.requiredEmc = par2;
        }
    }

	@Override
	public boolean canInteractWith(EntityPlayer var1) 
	{
		return true;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		tile.closeInventory();
	}
}
