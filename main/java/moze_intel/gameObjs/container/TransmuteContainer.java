package moze_intel.gameObjs.container;

import moze_intel.gameObjs.container.slots.SlotTableConsume;
import moze_intel.gameObjs.container.slots.SlotTableInput;
import moze_intel.gameObjs.container.slots.SlotTableLock;
import moze_intel.gameObjs.container.slots.SlotTableOutput;
import moze_intel.gameObjs.tiles.TransmuteTile;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class TransmuteContainer extends Container
{
	private TransmuteTile tile;
	
	public TransmuteContainer(InventoryPlayer invPlayer, TransmuteTile tile)
	{
		this.tile = tile;
		
		//Tablet Inventory
		this.addSlotToContainer(new SlotTableInput(tile, 0, 43, 29));
	    this.addSlotToContainer(new SlotTableInput(tile, 1, 34, 47));
	    this.addSlotToContainer(new SlotTableInput(tile, 2, 52, 47));
	    this.addSlotToContainer(new SlotTableInput(tile, 3, 16, 56));
	    this.addSlotToContainer(new SlotTableInput(tile, 4, 70, 56));
	    this.addSlotToContainer(new SlotTableInput(tile, 5, 34, 65));
	    this.addSlotToContainer(new SlotTableInput(tile, 6, 52, 65));
	    this.addSlotToContainer(new SlotTableInput(tile, 7, 43, 83));
	    this.addSlotToContainer(new SlotTableLock(tile, 8, 158, 56));
	    this.addSlotToContainer(new SlotTableConsume(tile, 9, 107, 103));
	    this.addSlotToContainer(new SlotTableOutput(tile, 10, 123, 36));
	    this.addSlotToContainer(new SlotTableOutput(tile, 11, 140, 19));
	    this.addSlotToContainer(new SlotTableOutput(tile, 12, 158, 15));
	    this.addSlotToContainer(new SlotTableOutput(tile, 13, 176, 19));
	    this.addSlotToContainer(new SlotTableOutput(tile, 14, 193, 36));
	    this.addSlotToContainer(new SlotTableOutput(tile, 15, 199, 56));
	    this.addSlotToContainer(new SlotTableOutput(tile, 16, 193, 76));
	    this.addSlotToContainer(new SlotTableOutput(tile, 17, 176, 93));
	    this.addSlotToContainer(new SlotTableOutput(tile, 18, 158, 97));
	    this.addSlotToContainer(new SlotTableOutput(tile, 19, 140, 93));
	    this.addSlotToContainer(new SlotTableOutput(tile, 20, 123, 76));
	    this.addSlotToContainer(new SlotTableOutput(tile, 21, 116, 56));
	    this.addSlotToContainer(new SlotTableOutput(tile, 22, 158, 37));
	    this.addSlotToContainer(new SlotTableOutput(tile, 23, 139, 56));
	    this.addSlotToContainer(new SlotTableOutput(tile, 24, 177, 56));
	    this.addSlotToContainer(new SlotTableOutput(tile, 25, 158, 75));
		
		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++) 
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 35 + j * 18, 123 + i * 18));
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 35 + i * 18, 181));
		
		tile.openInventory();
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) 
	{
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
	{
		return null;
	}
	
	@Override
	public ItemStack slotClick(int index, int mouseBtn, int isShifting, EntityPlayer player)
	{
		/*System.out.println(index);
		if (isShifting == 1)
		{
			Slot slot = this.getSlot(index);
			ItemStack stack = slot.getStack();
			
			if (stack != null)
			{
				if (index <= 8)
				{
					return super.slotClick(index, 0, 0, player);
				}
				
				if (index > 9 && index <= 25)
				{
					int reqEmc = Utils.GetEmcValue(stack);
					boolean canExtract = false;
					
					do
					{
						canExtract = false;
						
						for (int i = 0; i < player.inventory.mainInventory.length; i++)
						{
							ItemStack invStack = player.inventory.getStackInSlot(i);
							
							if (invStack == null)
							{
								player.inventory.setInventorySlotContents(i, stack.copy());
								tile.RemoveEmc(reqEmc);
								canExtract = true;
								break;
							}
							else if (ItemStack.areItemStacksEqual(stack, Utils.getNormalizedStack(invStack)))
							{
								if (invStack.stackSize < invStack.getMaxStackSize())
								{
									invStack.stackSize++;
									tile.RemoveEmc(reqEmc);
									canExtract = true;
									break;
								}
							}
						}
						
					}while(canExtract && tile.GetStoredEMC() >= reqEmc);
					
					tile.updateOutputs();
					
					return null;
				}
				else if (index > 25)
				{
					SlotTableConsume consume = (SlotTableConsume) this.getSlot(9);
					
					while (stack != null && !tile.HasMaxedEmc())
					{
						consume.putStack(Utils.getNormalizedStack(stack));
						stack.stackSize--;
						
						if (stack.stackSize == 0)
						{
							stack = null;
							slot.putStack(null);
						}
					}
					
					return null;
				}
			}
		}*/
		
		return super.slotClick(index, mouseBtn, isShifting, player);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer p_75134_1_)
    {
		super.onContainerClosed(p_75134_1_);
		tile.closeInventory();
    }
}
