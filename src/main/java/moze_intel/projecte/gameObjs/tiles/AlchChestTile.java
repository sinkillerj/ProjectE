package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IAlchChestItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AlchChestTile extends TileEmcDirection implements IInventory
{
	private ItemStack[] inventory = new ItemStack[104];
	public float lidAngle;
	public float prevLidAngle;
	public int numPlayersUsing;
	private int ticksSinceSync;

	public AlchChestTile()
	{
		super();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("Items", 10);
		inventory = new ItemStack[104];
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNBT = list.getCompoundTagAt(i);
			byte slot = subNBT.getByte("Slot");
			
			if (slot >= 0 && slot < 104)
			{
				inventory[slot] = ItemStack.loadItemStackFromNBT(subNBT);
			}
		}	
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < 104; i++)
		{
			if (inventory[i] == null) 
			{
				continue;
			}
			
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
		return 104;
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
			{
				inventory[slot] = null;
			}
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
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
		
		this.markDirty();
	}

	@Override
	public String getInventoryName() 
	{
		return "tile.pe_alchemy_chest.name";
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

	public ItemStack[] getBackingInventoryArray()
	{
		return inventory;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) 
	{
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : var1.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (++ticksSinceSync % 20 * 4 == 0)
		{
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.alchChest, 1, numPlayersUsing);
		}

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
			{
				lidAngle += angleIncrement;
			}
			else
			{
				lidAngle -= angleIncrement;
			}

			if (lidAngle > 1.0F)
			{
				lidAngle = 1.0F;
			}

			if (lidAngle < 0.5F && var8 >= 0.5F)
			{
				adjustedXCoord = xCoord + 0.5D;
				adjustedZCoord = zCoord + 0.5D;
				worldObj.playSoundEffect(adjustedXCoord, yCoord + 0.5D, adjustedZCoord, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (lidAngle < 0.0F)
			{
				lidAngle = 0.0F;
			}
		}

		if (worldObj.isRemote)
		{
			if (worldObj.getChunkFromBlockCoords(xCoord, zCoord).isEmpty())
			{
				// Handle condition where this method is called even after the clientside chunk has unloaded.
				// This will make IAlchChestItems below crash with an NPE since the TE they get back is null
				// Don't you love vanilla???
				return;
			}
		}

		for (ItemStack stack : inventory)
		{
			if (stack != null && stack.getItem() instanceof IAlchChestItem)
			{
				((IAlchChestItem) stack.getItem()).updateInAlchChest(worldObj, xCoord, yCoord, zCoord, stack);
			}
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
		return true;
	}
}
