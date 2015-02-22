package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientSyncPedestalPKT;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class DMPedestalTile extends TileEntity implements IInventory
{
	private boolean isActive = false;
	private ItemStack[] inventory = new ItemStack[1];
	private AxisAlignedBB effectBounds;
	private int particleCooldown = 20;
	public double centeredX, centeredY, centeredZ;

	public DMPedestalTile()
	{
		super();
	}

	@Override
	public void updateEntity()
	{
		centeredX = xCoord + 0.5;
		centeredY = yCoord + 0.5;
		centeredZ = zCoord + 0.5;

		if (effectBounds == null)
		{
			effectBounds = AxisAlignedBB.getBoundingBox(centeredX - 4, centeredY - 4, centeredZ - 4, centeredX + 5, centeredY + 5, centeredZ + 5);
		}

		if (getActive())
		{
			if (getItemStack() != null)
			{
				Item item = getItemStack().getItem();
				if (item instanceof IPedestalItem)
				{
					((IPedestalItem) item).updateInPedestal(worldObj, xCoord, yCoord, zCoord);
				}

			}
			else
			{
				setActive(false);
			}
		}
	}

	public ItemStack getItemStack()
	{
		return getStackInSlot(0);
	}

	public AxisAlignedBB getEffectBounds()
	{
		return effectBounds;
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
		setActive(tag.getBoolean("isActive"));
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
		tag.setBoolean("isActive", getActive());
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
		return true;
	}

	@Override
	public void openInventory()
	{
	}

	@Override
	public void closeInventory()
	{
		this.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
	{
		return true;
	}

	@Override
	public Packet getDescriptionPacket()
	{
		return PacketHandler.getMCPacket(new ClientSyncPedestalPKT(this));
	}

	public boolean getActive()
	{
		return isActive;
	}

	public void setActive(boolean newState)
	{
		if (newState != this.getActive() && worldObj != null)
		{
			if (newState)  // Turning on
			{
				worldObj.playSoundEffect(centeredX, centeredY, centeredZ, "projecte:item.pecharge", 1.0F, 1.0F);
				if (worldObj.isRemote) // Particles are clientside but sounds are serverside...
				{
					worldObj.spawnParticle("flame", centeredX, yCoord, centeredZ, 0, 0.005, 0);
				}
			}
			else // Turning off
			{
				worldObj.playSoundEffect(centeredX, centeredY, centeredZ, "projecte:item.peuncharge", 1.0F, 1.0F);
			}
		}
		this.isActive = newState;
	}
}
