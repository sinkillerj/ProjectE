package moze_intel.gameObjs.tiles;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.utils.Constants;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CondenserTile extends TileEmcConsumerDirection implements IInventory, ISidedInventory
{
	private ItemStack[] inventory = new ItemStack[105];
	private int[] validInventorySlots = new int[104];
	private ItemStack lock;
	private int ticksSinceSync;
	private int timer = 0;
	public int displayEmc;
	public float lidAngle;
    public float prevLidAngle;
    public int numPlayersUsing;
	public int requiredEmc;
	
	public CondenserTile()
	{
		super(100000000);
		
		for (int i = 1; i < 105; i++)
		{
			validInventorySlots[i - 1] = i;
		}
	}
	
	@Override
	public void updateEntity()
	{
		updateChest();
		
		if (worldObj.isRemote) 
		{
			return;
		}
		
		displayEmc = (int) this.GetStoredEMC();
		lock = getStackInSlot(0);
		
		/*System.out.println("STORED: "+this.GetStoredEMC());
		System.out.println("DISPLAY: "+displayEmc);
		System.out.println("REQUIRED: "+this.requiredEmc);*/
		
		if (lock == null)
		{
			if (requiredEmc != 0)
			{
				requiredEmc = 0;
				this.isRequestingEmc = false;
			}
			return;
		}
		else if (requiredEmc != Utils.GetEmcValue(lock))
		{
			requiredEmc = Utils.GetEmcValue(lock);
			this.isRequestingEmc = true;
		}
		
		if (this.GetStoredEMC() > requiredEmc)
		{
			HandleMassCondense();
		}
		
		Condense();
	}
	
	private void HandleMassCondense()
	{
		while(HasSpace() && this.GetStoredEMC() > requiredEmc)
		{
			double result = this.GetStoredEMC() - requiredEmc;
			PushStack();
			this.SetEmcValue(result);
		}
	}
	
	private void Condense()
	{
		if (!HasSpace()) 
		{
			this.isRequestingEmc = false;
			return;
		}
		for (int i = 1; i < 105; i++)
		{
			ItemStack stack = getStackInSlot(i);
			if (stack == null || IsStackEqualToLock(stack)) continue;
			decrStackSize(i, 1);
			this.AddEmc(Utils.GetEmcValue(stack));
			break;
		}
		
		if (this.GetStoredEMC() >= requiredEmc)
		{
			double result = this.GetStoredEMC() - requiredEmc;
			PushStack();
			this.SetEmcValue(result);
		}
	}
	
	private void PushStack()
	{
		int slot = GetSlotForStack();
		
		if (slot == 0) 
		{
			return;
		}
		
		ItemStack stack = getStackInSlot(slot);
		
		if (stack == null)
		{
			setInventorySlotContents(slot, lock.copy());
		}
		else
		{
			stack.stackSize += 1;
			setInventorySlotContents(slot, stack);
		}
	}
	
	private int GetSlotForStack()
	{
		for (int i = 1; i < 105; i++)
		{
			ItemStack stack = getStackInSlot(i);
			if (stack == null) return i;
			if (IsStackEqualToLock(stack) && stack.stackSize < stack.getMaxStackSize()) return i;
		}
		return 0;
	}
	
	private boolean HasSpace()
	{
		for (int i = 1; i < 105; i++)
		{
			ItemStack stack = getStackInSlot(i);
			if (stack == null) return true;
			if (IsStackEqualToLock(stack) && stack.stackSize < stack.getMaxStackSize()) return true;
		}
		return false;
	}
	
	private boolean IsStackEqualToLock(ItemStack stack)
	{
		if (lock == null) 
		{
			return false;
		}
		
		boolean flag = true;
		ItemStack compare = stack.copy();
		compare.stackSize = 1;
		if (compare.hasTagCompound() && !lock.hasTagCompound()) 
		{
			return false;
		}
		else if (!compare.hasTagCompound() && lock.hasTagCompound()) 
		{
			return false;
		}
		else if (compare.hasTagCompound() && lock.hasTagCompound())
		{
			flag = compare.stackTagCompound.equals(lock.stackTagCompound);
		}
		return compare.getItem() == lock.getItem() && compare.getItemDamage() == lock.getItemDamage() && flag;
	}
	
	public int GetProgressScaled()
	{
		if (requiredEmc == 0) 
		{
			return 0;
		}
		if (displayEmc >= requiredEmc) 
		{
			return Constants.maxCondenserProgress;
		}
		
		System.out.println("DISPLAY EMC: "+this.displayEmc);
		System.out.println("REQUIRED EMC: "+this.requiredEmc);
		System.out.println("STORED EMC: "+this.GetStoredEMC());
		
		return (displayEmc * Constants.maxCondenserProgress) / requiredEmc;
	}
	
	public void SendUpdate()
    {
    	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.SetEmcValue(nbt.getDouble("EMC"));
		NBTTagList list = nbt.getTagList("Items", 10);
		inventory = new ItemStack[105];
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNBT = list.getCompoundTagAt(i);
			byte slot = subNBT.getByte("Slot");
			if (slot >= 0 && slot < 105)
				inventory[slot] = ItemStack.loadItemStackFromNBT(subNBT);
		}	
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("EMC", this.GetStoredEMC());
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < 105; i++)
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
		return 105;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int qnt) 
	{
		ItemStack stack = inventory[slot];
		if (stack != null)
		{
			if (stack.stackSize <= qnt)
				inventory[slot] = null;
			else
			{
				stack = stack.splitStack(qnt);
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
		return "Condenser";
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

	public void updateChest()
    {
        if (++ticksSinceSync % 20 * 4 == 0)
            worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.alchChest, 1, numPlayersUsing);

        prevLidAngle = lidAngle;
        float angleIncrement = 0.1F;
        double adjustedXCoord, adjustedZCoord;
        
        if (numPlayersUsing > 0 && lidAngle == 0.0F)
        {
            adjustedXCoord = xCoord + 0.5D;
            adjustedZCoord = zCoord + 0.5D;
            worldObj.playSoundEffect(adjustedXCoord, yCoord + 0.5D, adjustedZCoord, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F)
        {
            float var8 = lidAngle;

            if (numPlayersUsing > 0)
                lidAngle += angleIncrement;
            else
                lidAngle -= angleIncrement;

            if (lidAngle > 1.0F)
                lidAngle = 1.0F;

            if (lidAngle < 0.5F && var8 >= 0.5F)
            {
                adjustedXCoord = xCoord + 0.5D;
                adjustedZCoord = zCoord + 0.5D;
                worldObj.playSoundEffect(adjustedXCoord, yCoord + 0.5D, adjustedZCoord, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (lidAngle < 0.0F)
                lidAngle = 0.0F;
        }
    }
	
	@Override
    public boolean receiveClientEvent(int number, int arg)
    {
        if (number == 1)
        {
            numPlayersUsing = arg;
            return true;
        }
        else return super.receiveClientEvent(number, arg);
    }
	
	@Override
    public void openInventory()
    {
    	++numPlayersUsing;
        worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.alchChest, 1, numPlayersUsing);
    }
	
	@Override
    public void closeInventory()
    {
    	--numPlayersUsing;
    	worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.alchChest, 1, numPlayersUsing);
    }

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		if (slot == 0) return false;
		return !IsStackEqualToLock(stack) && Utils.DoesItemHaveEmc(stack);
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) 
	{
		return validInventorySlots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) 
	{
		return isItemValidForSlot(slot, item);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) 
	{
		return IsStackEqualToLock(item);
	}
}
