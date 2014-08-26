package moze_intel.gameObjs.tiles;

import moze_intel.MozeCore;
import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.items.ItemBase;
import moze_intel.network.packets.RelaySyncPKT;
import moze_intel.utils.Constants;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class RelayMK1Tile extends TileEmcProducer implements IInventory
{
	private ItemStack[] inventory;
	private int invBufferSize;
	private final int chargeRate;
	public int displayEmc;
	public int displayKleinEmc;
	public int displayRawEmc;
	private int numUsing;
	
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
		if (worldObj.isRemote) 
		{
			return;
		}
		
		this.checkSurroundingBlocks(true);
		
		sendEmc();
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
			
				if (emcVal > 0 && this.getStoredEMC() + emcVal <= this.getMaxEmc())
				{
					this.addEmc(emcVal);
					ItemBase.removeEmc(stack, emcVal);
				}
			}
			else
			{
				int emcVal = Utils.getEmcValue(stack);
				
				if (emcVal > 0 && (this.getStoredEMC() + emcVal) <= this.getMaxEmc())
				{
					this.addEmc(emcVal);
					decrStackSize(0, 1);
				}
			}
		}
		
		ItemStack star = inventory[getSizeInventory() - 1]; 
		
		if (star != null && this.getStoredEMC() > 0 && star.getItem().equals(ObjHandler.kleinStars))
		{
			chargeKleinStars(star);
		}
		
		displayEmc = (int) this.getStoredEMC();
		displayKleinEmc = getKleinStarEmc();
		displayRawEmc = getRawEmc();
		
		if (numUsing > 0)
		{
			MozeCore.pktHandler.sendToAllAround(new RelaySyncPKT(displayEmc, displayKleinEmc, displayRawEmc, this.xCoord, this.yCoord, this.zCoord),
					new TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 6));
		}
	}
	
	private void sendEmc()
	{
		if (this.getStoredEMC() == 0) return;
		
		int numRequesting = this.getNumRequesting();
		if (numRequesting == 0) return;
		
		if (this.getStoredEMC() <= chargeRate)
		{
			this.sendEmcToRequesting(this.getStoredEMC() / numRequesting);
			this.setEmcValue(0);
		}
		else 
		{
			this.sendEmcToRequesting(chargeRate / numRequesting);
			this.removeEmc(chargeRate);
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
			else if (Utils.areItemStacksEqual(current, following) && following.stackSize < following.getMaxStackSize())
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
		int maxStarEmc = Utils.getKleinStarMaxEmc(star);
		double toSend = this.getStoredEMC() < chargeRate ? this.getStoredEMC() : chargeRate;
			
		if ((starEmc + toSend) <= maxStarEmc)
		{
			ItemBase.addEmc(star, toSend);
			this.removeEmc(toSend);
		}
		else
		{
			toSend = maxStarEmc - starEmc;
			ItemBase.addEmc(star, toSend);
			this.removeEmc(toSend);
		}
	}
	
	public int getEmcScaled(int i)
	{
		return displayEmc * i / this.getMaxEmc();
	}
	
	private int getKleinStarEmc()
	{
		if (inventory[getSizeInventory() - 1] != null)
		{
			return (int) ItemBase.getEmc(inventory[getSizeInventory() - 1]);
		}
		
		return 0;
	}
	
	public int getKleinEmcScaled(int i)
	{
		if (inventory[getSizeInventory() - 1] != null)
		{
			return displayKleinEmc * i / Utils.getKleinStarMaxEmc(inventory[getSizeInventory() - 1]);
		}
		
		return 0;
	}
	
	private int getRawEmc()
	{
		if (inventory[0] == null)
		{
			return 0;
		}
		
		if (inventory[0].getItem() == ObjHandler.kleinStars)
		{
			return (int) ItemBase.getEmc(inventory[0]);
		}
		
		return Utils.getEmcValue(inventory[0]) * inventory[0].stackSize;
	}
	
	public int getRawEmcScaled(int i)
	{
		if (inventory[0] == null)
		{
			return 0;
		}
		
		if (inventory[0].getItem() == ObjHandler.kleinStars)
		{
			return displayRawEmc * i / Utils.getKleinStarMaxEmc(inventory[0]);
		}
		
		int emc = Utils.getEmcValue(inventory[0]);
		
		return displayRawEmc * i / (emc * inventory[0].getMaxStackSize());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		this.setEmcValue(nbt.getDouble("EMC"));
		
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
		nbt.setDouble("EMC", this.getStoredEMC());
		
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
		numUsing++;
	}

	@Override
	public void closeInventory() 
	{
		numUsing--;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		return false;
	}
}
