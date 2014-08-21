package moze_intel.gameObjs.container.inventory;

import java.util.List;

import moze_intel.utils.PlayerBagInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AlchBagInventory implements IInventory
{
	private final ItemStack invItem;
	private ItemStack[] inventory = new ItemStack[104];
	private EntityPlayer player;
	
	public AlchBagInventory(EntityPlayer player, ItemStack stack)
	{
		invItem = stack;
		this.player = player;
		inventory = PlayerBagInventory.getPlayerBagData(player, stack.getItemDamage());
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
	public ItemStack decrStackSize(int slot, int qty)
	{
		ItemStack stack = getStackInSlot(slot);
		if(stack != null)
		{
			if(stack.stackSize > qty)
			{
				stack = stack.splitStack(qty);
				markDirty();
			}
			else
			{
				setInventorySlotContents(slot, null);
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) 
	{
		ItemStack stack = getStackInSlot(slot);
		
		if(stack != null)
		{
			setInventorySlotContents(slot, null);
		}
		
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) 
	{
		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}

		markDirty();
	}

	@Override
	public String getInventoryName() 
	{
		return "Alchemical Bag";
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
	public void markDirty() 
	{
		for (int i = 0; i < 104; ++i)
		{
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0)
			{
				inventory[i] = null;
			}
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) 
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
		if (!player.worldObj.isRemote)
		{
			PlayerBagInventory.setPlayerBagData(player, invItem.getItemDamage(), inventory);
			PlayerBagInventory.syncPlayerProps(player);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return true;
	}
}
