package moze_intel.projecte.gameObjs.tiles;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.RelaySyncPKT;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class RelayMK1Tile extends TileEmc implements IInventory, ISidedInventory, IEmcAcceptor, IEmcProvider
{
	private ItemStack[] inventory;
	private int invBufferSize;
	private final int chargeRate;
	public int displayEmc;
	public double displayChargingEmc;
	public double displayRawEmc;
	private int numUsing;
	
	public RelayMK1Tile()
	{
		super(Constants.RELAY_MK1_MAX);
		chargeRate = Constants.RELAY_MK1_OUTPUT;
		inventory = new ItemStack[8];
		invBufferSize = 6;
	}
	
	public RelayMK1Tile(int sizeInv, int maxEmc, int chargeRate)
	{
		super(maxEmc);
		this.chargeRate = chargeRate;
		inventory = new ItemStack[sizeInv + 2];
		invBufferSize = sizeInv;
	}
	
	@Override
	public void updateEntity()
	{	
		if (worldObj.isRemote) 
		{
			return;
		}

		sendEmc();
		sortInventory();
		
		ItemStack stack = inventory[0];
		
		if (stack != null)
		{
			if(stack.getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) stack.getItem());
				double emcVal = itemEmc.getStoredEmc(stack);
				
				if (emcVal > chargeRate)
				{
					emcVal = chargeRate;
				}
			
				if (emcVal > 0 && this.getStoredEmc() + emcVal <= this.getMaximumEmc())
				{
					this.addEMC(emcVal);
					itemEmc.extractEmc(stack, emcVal);
				}
			}
			else
			{
				int emcVal = EMCHelper.getEmcValue(stack);
				
				if (emcVal > 0 && (this.getStoredEmc() + emcVal) <= this.getMaximumEmc())
				{
					this.addEMC(emcVal);
					decrStackSize(0, 1);
				}
			}
		}
		
		ItemStack chargeable = inventory[getSizeInventory() - 1];
		
		if (chargeable != null && this.getStoredEmc() > 0 && chargeable.getItem() instanceof IItemEmc)
		{
			chargeItem(chargeable);
		}
		
		displayEmc = (int) this.getStoredEmc();
		displayChargingEmc = getChargingEMC();
		displayRawEmc = getRawEmc();
		
		if (numUsing > 0)
		{
			PacketHandler.sendToAllAround(new RelaySyncPKT(displayEmc, displayChargingEmc, displayRawEmc, this.xCoord, this.yCoord, this.zCoord),
					new TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 8));
		}
	}
	
	private void sendEmc()
	{
		if (this.getStoredEmc() == 0) return;

		if (this.getStoredEmc() <= chargeRate)
		{
			this.sendToAllAcceptors(this.getStoredEmc());
		}
		else 
		{
			this.sendToAllAcceptors(chargeRate);
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
				decrStackSize(i, current.stackSize);
			}
			else if (ItemHelper.areItemStacksEqual(current, following) && following.stackSize < following.getMaxStackSize())
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
			}
		}
	}
	
	private void chargeItem(ItemStack chargeable)
	{
		IItemEmc itemEmc = ((IItemEmc) chargeable.getItem());
		double starEmc = itemEmc.getStoredEmc(chargeable);
		double maxStarEmc = itemEmc.getMaximumEmc(chargeable);
		double toSend = this.getStoredEmc() < chargeRate ? this.getStoredEmc() : chargeRate;
			
		if ((starEmc + toSend) <= maxStarEmc)
		{
			itemEmc.addEmc(chargeable, toSend);
			this.removeEMC(toSend);
		}
		else
		{
			toSend = maxStarEmc - starEmc;
			itemEmc.addEmc(chargeable, toSend);
			this.removeEMC(toSend);
		}
	}
	
	public int getEmcScaled(int i)
	{
		return (int) Math.round(displayEmc * i / this.getMaximumEmc());
	}
	
	private double getChargingEMC()
	{
		int index = getSizeInventory() - 1;
		if (inventory[index] != null && inventory[index].getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) inventory[index].getItem()).getStoredEmc(inventory[index]);
		}
		
		return 0;
	}
	
	public int getChargingEMCScaled(int i)
	{
		int index = getSizeInventory() - 1;
		if (inventory[index] != null && inventory[index].getItem() instanceof IItemEmc)
		{
			return ((int) Math.round(displayChargingEmc * i / ((IItemEmc) inventory[index].getItem()).getMaximumEmc(inventory[index])));
		}
		
		return 0;
	}
	
	private double getRawEmc()
	{
		if (inventory[0] == null)
		{
			return 0;
		}
		
		if (inventory[0].getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) inventory[0].getItem()).getStoredEmc(inventory[0]);
		}
		
		return EMCHelper.getEmcValue(inventory[0]) * inventory[0].stackSize;
	}
	
	public int getRawEmcScaled(int i)
	{
		if (inventory[0] == null)
		{
			return 0;
		}
		
		if (inventory[0].getItem() instanceof IItemEmc)
		{
			return (int) Math.round(displayRawEmc * i / ((IItemEmc) inventory[0].getItem()).getMaximumEmc(inventory[0]));
		}
		
		int emc = EMCHelper.getEmcValue(inventory[0]);
		
		return MathHelper.floor_double(displayRawEmc * i / (emc * inventory[0].getMaxStackSize()));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

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
		return "pe.relay.mk1";
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
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : var1.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
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
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		int indexes[] = new int[inventory.length - 2];
		byte counter = 0;

		for (int i = 1; i < inventory.length - 1; i++)
		{
			indexes[counter] = i;
			counter++;
		}

		return indexes;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side)
	{
		return EMCHelper.doesItemHaveEmc(stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side)
	{
		return false;
	}

	@Override
	public double acceptEMC(ForgeDirection side, double toAccept)
	{
		if (worldObj.getTileEntity(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ) instanceof RelayMK1Tile)
		{
			return 0; // Do not accept from other relays - avoid infinite loop / thrashing
		}
		else
		{
			double toAdd = Math.min(maximumEMC - currentEMC, toAccept);
			currentEMC += toAdd;
			return toAdd;
		}
	}

	@Override
	public double provideEMC(ForgeDirection side, double toExtract)
	{
		double toRemove = Math.min(currentEMC, toExtract);
		currentEMC -= toRemove;
		return toRemove;
	}
}
