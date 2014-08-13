package moze_intel.gameObjs.tiles;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.items.ItemBase;
import moze_intel.utils.Constants;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class RelayMK1Tile extends TileEmcProducer implements IInventory
{
	private ItemStack[] inventory;
	private int invBufferSize;
	private final int chargeRate;
	public int displayEmc;
	
	public RelayMK1Tile()
	{
		super(Constants.RELAY_MK1_MAX);
		chargeRate = Constants.RELAY_MK1_OUTPUT;
		inventory = new ItemStack[8];
		invBufferSize = 6;
		this.isRequestingEmc = true;
	}
	
	public RelayMK1Tile(int sizeInv, int maxEmc, int chargeRate)
	{
		super(maxEmc);
		this.chargeRate = chargeRate;
		inventory = new ItemStack[sizeInv + 2];
		invBufferSize = sizeInv;
		this.isRequestingEmc = true;
	}
	
	@Override
	public void updateEntity()
	{	
		if (worldObj.isRemote) return;
		this.CheckSurroundingBlocks(true);
		
		SendEmc();
		sortInventory();
		
		ItemStack stack = inventory[0];
		if (stack != null)
		{
			if(stack.getItem().equals(ObjHandler.kleinStars))
			{
				double emcVal = ItemBase.getEmc(stack);
				
				if (emcVal > chargeRate)
				{
					emcVal = chargeRate;
				}
			
				if (emcVal > 0 && this.GetStoredEMC() + emcVal <= this.GetMaxEmc())
				{
					this.AddEmc(emcVal);
					ItemBase.removeEmc(stack, emcVal);
				}
			}
			else
			{
				int emcVal = Utils.GetEmcValue(stack);
				
				if (emcVal > 0 && (this.GetStoredEMC() + emcVal) <= this.GetMaxEmc())
				{
					this.AddEmc(emcVal);
					decrStackSize(0, 1);
				}
			}
		}
		
		ItemStack star = inventory[getSizeInventory() - 1]; 
		
		if (star != null && this.GetStoredEMC() > 0 && star.getItem().equals(ObjHandler.kleinStars))
		{
			chargeKleinStars(star);
		}
		
		displayEmc = (int) this.GetStoredEMC();
	}
	
	private void SendEmc()
	{
		if (this.GetStoredEMC() == 0) return;
		
		int numRequesting = this.GetNumRequesting();
		if (numRequesting == 0) return;
		
		if (this.GetStoredEMC() <= chargeRate)
		{
			this.SendEmcToRequesting(this.GetStoredEMC() / numRequesting);
			this.SetEmcValue(0);
		}
		else 
		{
			this.SendEmcToRequesting(chargeRate / numRequesting);
			this.RemoveEmc(chargeRate);
		}
	}
	
	private void sortInventory()
	{
		for (int i = 1; i <= invBufferSize; i++)
		{
			ItemStack current = getStackInSlot(i);
			
			if (current == null)
			{
				continue;
			}
			
			int nextIndex = i < invBufferSize ? i + 1 : 0;
			
			ItemStack following = inventory[nextIndex];
			
			if (following == null)
			{
				inventory[nextIndex] = current;
				inventory[i] = null;
				break;
			}
			else if (Utils.AreItemStacksEqual(current, following) && following.stackSize < following.getMaxStackSize())
			{
				int missingForFullStack = following.getMaxStackSize() - following.stackSize;
				
				if (current.stackSize <= missingForFullStack)
				{
					inventory[nextIndex].stackSize += current.stackSize;
					inventory[i] = null;
				}
				else
				{
					inventory[nextIndex].stackSize += missingForFullStack;
					decrStackSize(i, missingForFullStack);
				}
				
				break;
			}
		}
	}
	
	private void chargeKleinStars(ItemStack star)
	{
		double starEmc = ItemBase.getEmc(star);
		int maxStarEmc = Utils.GetKleinStarMaxEmc(star);
		double toSend = this.GetStoredEMC() < chargeRate ? this.GetStoredEMC() : chargeRate;
			
		if ((starEmc + toSend) <= maxStarEmc)
		{
			ItemBase.addEmc(star, toSend);
			this.RemoveEmc(toSend);
		}
		else
		{
			toSend = maxStarEmc - starEmc;
			ItemBase.addEmc(star, toSend);
			this.RemoveEmc(toSend);
		}
	}
	
	public int GetEmcScaled(int i)
	{
		return displayEmc * i / this.GetMaxEmc();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.SetEmcValue(nbt.getDouble("EMC"));
		
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
		return inventory.length;
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
		return "Collector MK1";
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
