package moze_intel.projecte.gameObjs.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class DMFurnaceContainer extends Container
{
	private DMFurnaceTile tile;
	private int lastCookTime;
	private int lastBurnTime;
	private int lastItemBurnTime;
	
	public DMFurnaceContainer(InventoryPlayer invPlayer, DMFurnaceTile tile)	
	{
		this.tile = tile;
		
		//Fuel Slot
		this.addSlotToContainer(new Slot(tile, 0, 49, 53));
		
		//Input(0)
		this.addSlotToContainer(new Slot(tile, 1, 49, 17));
		
		//Input Storage
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++)
				this.addSlotToContainer(new Slot(tile, i * 4 + j + 2, 13 + i * 18, 8 + j * 18));
		
		//Output
		this.addSlotToContainer(new Slot(tile, 10, 109, 35));
		
		//OutputStorage
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 4; j++)
				this.addSlotToContainer(new Slot(tile, i * 4 + j + 11, 131 + i * 18, 8 + j * 18));
		
		//Player Inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		
		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return player.getDistanceSq(tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5) <= 64.0;
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting)
	{
		super.addCraftingToCrafters(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, tile.furnaceCookTime);
		par1ICrafting.sendProgressBarUpdate(this, 1, tile.furnaceBurnTime);
		par1ICrafting.sendProgressBarUpdate(this, 2, tile.currentItemBurnTime);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < this.crafters.size(); ++i)
		{
			ICrafting icrafting = (ICrafting)this.crafters.get(i);

			if (lastCookTime != tile.furnaceCookTime)
				icrafting.sendProgressBarUpdate(this, 0, tile.furnaceCookTime);

			if (lastBurnTime != tile.furnaceBurnTime)
				icrafting.sendProgressBarUpdate(this, 1, tile.furnaceBurnTime);

			if (lastItemBurnTime != tile.currentItemBurnTime)
				icrafting.sendProgressBarUpdate(this, 2, tile.currentItemBurnTime);
		}

		lastCookTime = tile.furnaceCookTime;
		lastBurnTime = tile.furnaceBurnTime;
		lastItemBurnTime = tile.currentItemBurnTime;
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0)
			tile.furnaceCookTime = par2;

		if (par1 == 1)
			tile.furnaceBurnTime = par2;

		if (par1 == 2)
			tile.currentItemBurnTime = par2;
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
		
		if (slotIndex <= 18)
		{
			if (!this.mergeItemStack(stack, 19, 55, false))
			{
				return null;
			}
		}
		else
		{
			
			if (TileEntityFurnace.isItemFuel(newStack) || newStack.getItem() instanceof IItemEmc)
			{
				if (!this.mergeItemStack(stack, 0, 1, false))
				{
					return null;
				}
			}
			else if (FurnaceRecipes.smelting().getSmeltingResult(newStack) != null)
			{
				if (!this.mergeItemStack(stack, 1, 10, false))
				{
					return null;
				}
			}
			else
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
		
		return newStack;
	}
}
