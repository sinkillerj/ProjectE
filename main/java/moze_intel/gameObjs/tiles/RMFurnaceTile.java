package moze_intel.gameObjs.tiles;

import moze_intel.gameObjs.blocks.MatterFurnace;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RMFurnaceTile extends TileEmc implements IInventory
{
	public ItemStack[] inventory = new ItemStack[27];
	public int outputSlot = 14;
	public int[] inputStorage = new int[] {2, 13};
	public int[] outputStorage = new int[] {15, 26};
	public int ticksBeforeSmelt = 3;
	public int efficiencyBonus = 4;
	public int furnaceBurnTime;
    public int currentItemBurnTime;
    public int furnaceCookTime;
    
    @Override
    public void updateEntity()
    {
    	PushSmeltStack();
    	
    	boolean flag = furnaceBurnTime > 0;
    	boolean flag1 = false;
    	
    	if (furnaceBurnTime > 0)
    		--furnaceBurnTime;
    	
    	if (!worldObj.isRemote)
    	{
    		if (furnaceBurnTime == 0 && CanSmelt())
    		{
    			currentItemBurnTime = furnaceBurnTime = getItemBurnTime(inventory[0]);
    		
    			if (furnaceBurnTime > 0)
    			{
    				flag1 = true;
    				
    				if (inventory[0] != null)
    				{
    					--inventory[0].stackSize;
    					if (inventory[0].stackSize == 0)
    						inventory[0] = inventory[0].getItem().getContainerItem(inventory[0]);
    				}
    			}
    		}
    	
    		if (furnaceBurnTime > 0 && CanSmelt())
    		{
    			++furnaceCookTime;
    		
    			if (furnaceCookTime == ticksBeforeSmelt)
    			{
    				furnaceCookTime = 0;
    				SmeltItem();
    				flag1 = true;
    			}
    		}
    		else furnaceCookTime = 0;
    		
        	if (flag != furnaceBurnTime > 0)
            {
                flag1 = true;
                Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
                if (block instanceof MatterFurnace)
                	((MatterFurnace) block).updateFurnaceBlockState(furnaceBurnTime > 0, worldObj, xCoord, yCoord, zCoord);
            }
    	}
    	
    	if (flag1) markDirty();
    	
    	ItemStack output = inventory[outputSlot];
    	if (output != null && output.stackSize == output.getMaxStackSize())
    		PushOutput();
    }
    
    public boolean isBurning()
    {
    	return furnaceBurnTime > 0;
    }
    
    private void PushSmeltStack()
    {
    	for (int i = inputStorage[0]; i <= inputStorage[1]; i++)
    	{
    		ItemStack stack = getStackInSlot(1);
    		if (stack != null) return;
    		ItemStack slotStack = getStackInSlot(i);
    		if (slotStack == null) continue;
    		inventory[1] = slotStack;
    		inventory[i] = null;
    		return;
    	}
    }
    
    private void PushOutput()
    {
    	ItemStack output = inventory[outputSlot];
    	
    	for (int i = outputStorage[0]; i <= outputStorage[1]; i++)
    	{
    		ItemStack stack = inventory[i];
    		
    		if (stack == null)
    		{
    			inventory[i] = output;
    			inventory[outputSlot] = null;
    			return;
    		}
    	}
    }
    
    private void SmeltItem()
    {
    	ItemStack toSmelt = inventory[1];
    	ItemStack smeltResult = FurnaceRecipes.smelting().getSmeltingResult(toSmelt);
    	ItemStack currentSmelted = getStackInSlot(outputSlot);
    	
    	if (currentSmelted == null) 
    		setInventorySlotContents(outputSlot, smeltResult.copy());
    	else
    		currentSmelted.stackSize += smeltResult.stackSize;
    	
    	decrStackSize(1, 1);
    }
    
    private boolean CanSmelt() 
    {
    	ItemStack toSmelt = inventory[1];
    	
    	if (toSmelt == null) return false;
    	
    	ItemStack smeltResult = FurnaceRecipes.smelting().getSmeltingResult(toSmelt);
    	if (smeltResult == null) return false;
    	
    	ItemStack currentSmelted = getStackInSlot(outputSlot);
    	
    	if (currentSmelted == null) return true;
    	if (!smeltResult.isItemEqual(currentSmelted)) return false;
    	
    	int result = currentSmelted.stackSize + smeltResult.stackSize;
    	return result <= currentSmelted.getMaxStackSize();
    }
    
    private int getItemBurnTime(ItemStack stack)
    {
    	int val = TileEntityFurnace.getItemBurnTime(stack);
    	return (val * ticksBeforeSmelt) / 200 * efficiencyBonus;
    }
    
    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int value)
    {
    	return (furnaceCookTime + (isBurning() && CanSmelt() ? 1 : 0)) * value / ticksBeforeSmelt;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int value)
    {
        if (this.currentItemBurnTime == 0)
            this.currentItemBurnTime = ticksBeforeSmelt;

        return furnaceBurnTime * value / currentItemBurnTime;
    }
    
    @Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.SetEmcValue(nbt.getDouble("EMC"));
		furnaceBurnTime = nbt.getShort("BurnTime");
        furnaceCookTime = nbt.getShort("CookTime");
        currentItemBurnTime = getItemBurnTime(inventory[0]);
		
		NBTTagList list = nbt.getTagList("Items", 10);
		inventory = new ItemStack[getSizeInventory()];
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNBT = list.getCompoundTagAt(i);
			byte slot = subNBT.getByte("Slot");
			if (slot >= 0 && slot < getSizeInventory())
				inventory[slot] = ItemStack.loadItemStackFromNBT(subNBT);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("EMC", this.GetStoredEMC());
		nbt.setShort("BurnTime", (short) furnaceBurnTime);
		nbt.setShort("CookTime", (short) furnaceCookTime);
		
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++)
		{
			if (inventory[i] == null) continue;
			NBTTagCompound subNBT = new NBTTagCompound();
			subNBT.setByte("Slot", (byte) i);
			inventory[i].writeToNBT(subNBT);
			list.appendTag(subNBT);
		}
		nbt.setTag("Items", list);
	}

	@Override
	public int getSizeInventory()
	{
		return 27;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) 
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int qty) 
	{
		ItemStack stack = inventory[slot];
		if (stack != null)
		{
			if (stack.stackSize <= qty)
				inventory[slot] = null;
			else
			{
				stack = stack.splitStack(qty);
				if (stack.stackSize == 0)
					inventory[slot] = null;
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) 
	{
		if (inventory[slot] != null)
		{
			ItemStack stack = inventory[slot];
			inventory[slot] = null;
			return stack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) 
	{
		inventory[slot] = stack;
		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
			stack.stackSize = this.getInventoryStackLimit();
		this.markDirty();
	}

	@Override
	public String getInventoryName() 
	{
		return "Transmutation Stone";
	}

	@Override
	public boolean hasCustomInventoryName() 
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) 
	{
		return true;
	}

	@Override
	public void openInventory() 
	{
		
	}

	@Override
	public void closeInventory() 
	{
		
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return false;
	}
}
