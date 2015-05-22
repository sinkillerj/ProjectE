package moze_intel.projecte.gameObjs.container.inventory;

import moze_intel.projecte.playerData.AlchemicalBags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;

public class AlchBagInventory implements IInventory
{
	private final ItemStack invItem;
	private ItemStack[] inventory;
	private EntityPlayer player;
	
	public AlchBagInventory(EntityPlayer player, ItemStack stack)
	{
		invItem = stack;
		this.player = player;
		inventory = AlchemicalBags.get(player.getCommandSenderName(), (byte) stack.getItemDamage());
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
		
		if (stack != null)
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
		
		if (stack != null)
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
	public String getCommandSenderName()
	{
		return "item.pe_alchemical_bag_white.name";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentTranslation(getCommandSenderName());
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
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		if (!player.worldObj.isRemote)
		{
			AlchemicalBags.set(this.player.getCommandSenderName(), (byte) invItem.getItemDamage(), inventory);
			AlchemicalBags.sync(this.player);
		}
	}
	
	public ItemStack[] getInventory()
	{
		return inventory;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return true;
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		Arrays.fill(inventory, null);
	}
}
