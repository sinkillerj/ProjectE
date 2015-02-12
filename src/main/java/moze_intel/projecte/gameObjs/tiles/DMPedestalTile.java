package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.IPedestalItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class DMPedestalTile extends TileEntity implements IInventory
{
	public boolean isActive = false;
	private ItemStack[] inventory;

	@Override
	public void updateEntity()
	{
		if (inventory == null || inventory[0] == null || (hasWorldObj() && worldObj.isRemote))
		{
			return;
		} else if (isActive)
		{
			Item item = inventory[0].getItem();
			if (item instanceof IPedestalItem)
			{
				((IPedestalItem) item).updateInPedestal(worldObj, xCoord, yCoord, zCoord);
			}
		}
	}

	public void toggleState()
	{
		if (inventory[0] != null)
		{
			isActive = !isActive;
		}
	}

	public ItemStack getItem()
	{
		return getStackInSlot(1);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		inventory = new ItemStack[getSizeInventory()];
		NBTTagList tagList = tag.getTagList("Items", 10);
		for (int i = 0; i < tagList.tagCount(); ++i)
		{
			NBTTagCompound compound = tagList.getCompoundTagAt(i);
			byte slot = compound.getByte("Slot");
			if (slot >= 0 && slot < inventory.length)
			{
				inventory[slot] = ItemStack.loadItemStackFromNBT(compound);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		NBTTagList tagList = new NBTTagList();

		for (int i = 0; i < this.inventory.length; ++i)
		{
			if (this.inventory[i] != null)
			{
				NBTTagCompound compound = new NBTTagCompound();
				compound.setByte("Slot", (byte)i);
				this.inventory[i].writeToNBT(compound);
				tagList.appendTag(compound);
			}
		}

		tag.setTag("Items", tagList);
	}

	@Override
	public int getSizeInventory()
	{
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt)
	{
		ItemStack result = inventory[slot];
		if (inventory[slot] != null)
		{
			if (amt > inventory[slot].stackSize)
			{
				setInventorySlotContents(slot, null);
			}
			else
			{
				result = inventory[slot].splitStack(amt);
				if (inventory[slot].stackSize <= 0)
				{
					setInventorySlotContents(slot, null);
				}
			}
		}
		return result;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return inventory[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack)
	{
		inventory[slot] = itemStack;

		if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit())
		{
			itemStack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public String getInventoryName()
	{
		return "DM Pedestal";
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
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
	{
		return false;
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
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
	{
		return true;
	}
}
