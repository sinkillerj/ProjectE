package moze_intel.projecte.gameObjs.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.container.slots.collector.SlotCollectorInv;
import moze_intel.projecte.gameObjs.container.slots.collector.SlotCollectorLock;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CollectorMK2Container extends Container
{
	private CollectorMK2Tile tile;
	private int sunLevel;
	
	public CollectorMK2Container(InventoryPlayer invPlayer, CollectorMK2Tile collector)
	{
		this.tile = collector;
		tile.openInventory();
		
		//Klein Star Slot
		this.addSlotToContainer(new SlotCollectorInv(tile, 0, 140, 58));
				
		//Fuel Upgrade Slot
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				this.addSlotToContainer(new SlotCollectorInv(tile, i * 4 + j + 1, 18 + i * 18, 8 + j * 18));
				
		//Upgrade Result
		this.addSlotToContainer(new Slot(tile, 13, 140, 13));
				
		//Upgrade Target
		this.addSlotToContainer(new SlotCollectorLock(tile, 14, 169, 36));
			
		//Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 20 + j * 18, 84 + i * 18));
				
		//Player hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 20 + i * 18, 142));
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting)
	{
		super.addCraftingToCrafters(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, tile.displaySunLevel);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		
		for (int i = 0; i < this.crafters.size(); ++i)
		{
			ICrafting icrafting = (ICrafting)this.crafters.get(i);

			if(sunLevel != tile.getSunLevel())
				icrafting.sendProgressBarUpdate(this, 1, tile.getSunLevel());
		}
		
		sunLevel = tile.getSunLevel();
	}
	
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		tile.displaySunLevel = par2;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		tile.closeInventory();
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
		
		if (slotIndex <= 14)
		{
			if (!this.mergeItemStack(stack, 15, 50, false))
			{
				return null;
			}
		}
		else if (slotIndex >= 15 && slotIndex <= 50)
		{
			if (!FuelMapper.isStackFuel(stack) || FuelMapper.isStackMaxFuel(stack) || !this.mergeItemStack(stack, 1, 12, false))
			{
				return null;
			}
		}
		else
		{
			return null;
		}
		
		if (stack.stackSize == 0)
		{
			slot.putStack(null);
		}
		else
		{
			slot.onSlotChanged();
		}
		
		slot.onPickupFromSlot(player, stack);
		return newStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5) <= 64.0;
	}
}
