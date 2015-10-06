package moze_intel.projecte.gameObjs.tiles;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.CollectorSyncPKT;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Map;

public class CollectorMK1Tile extends TileEmc implements IInventory, ISidedInventory, IEmcProvider
{
	private ItemStack[] inventory;
	private int[] accessibleSlots;
	private final int invBufferSize;
	private final int emcGen;
	private final int lockSlot;
	private final int upgradedSlot;
	private boolean hasChargeableItem;
	private boolean hasFuel;
	public double storedFuelEmc;
	public int displayEmc;
	public int displaySunLevel;
	public double displayItemCharge;
	private int numUsing;
	
	public CollectorMK1Tile()
	{
		super(Constants.COLLECTOR_MK1_MAX);
		inventory = new ItemStack[11];
		invBufferSize = 8;
		
		accessibleSlots = new int[invBufferSize];
		
		for (int i = 0; i < invBufferSize; i++)
		{
			accessibleSlots[i] = i + 1;
		}
		
		emcGen = Constants.COLLECTOR_MK1_GEN;
		upgradedSlot = 9;
		lockSlot = 10;
	}
	
	public CollectorMK1Tile(int maxEmc, int emcGen, int upgradedSlot, int lockSlot)
	{
		super(maxEmc);
		inventory = new ItemStack[lockSlot + 1];
		invBufferSize = lockSlot - 2;
		
		accessibleSlots = new int[invBufferSize];
		
		for (int i = 0; i < invBufferSize; i++)
		{
			accessibleSlots[i] = i + 1;
		}
		
		this.emcGen = emcGen;
		this.upgradedSlot = upgradedSlot;
		this.lockSlot = lockSlot;
	}
	
	@Override
	public void updateEntity()
	{
		if (worldObj.isRemote) 
		{
			return;
		}
		
		sortInventory();
		
		if (inventory[0] == null)
		{
			hasChargeableItem = false;
			hasFuel = false;
		}
		else 
		{
			checkFuelOrKlein();
		}
		
		if (!this.hasMaxedEmc())
		{
			this.addEMC(getSunRelativeEmc(emcGen) / 20.0f);
		}
		
		updateEmc();
		
		displayEmc = (int) this.getStoredEmc();
		displaySunLevel = getSunLevel();
		displayItemCharge = getItemCharge();
		
		if (numUsing > 0)
		{
			PacketHandler.sendToAllAround(new CollectorSyncPKT(displayEmc, displayItemCharge, this.xCoord, this.yCoord, this.zCoord),
					new TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 8));
		}
	}
	
	private void sortInventory()
	{
		if (inventory[upgradedSlot] != null)
		{
			if (!(inventory[lockSlot] != null
					&& inventory[upgradedSlot].getItem() == inventory[lockSlot].getItem()
					&& inventory[upgradedSlot].stackSize < inventory[upgradedSlot].getMaxStackSize())) {
				for (int i = 1; i < invBufferSize; i++)
				{
					if (inventory[i] == null)
					{
						inventory[i] = inventory[upgradedSlot];
						inventory[upgradedSlot] = null;
						break;
					}
					else if (ItemHelper.areItemStacksEqual(inventory[i], inventory[upgradedSlot]))
					{
						int remain = inventory[i].getMaxStackSize() - inventory[i].stackSize;

						if (remain >= inventory[upgradedSlot].stackSize)
						{
							inventory[i].stackSize += inventory[upgradedSlot].stackSize;
							inventory[upgradedSlot] = null;
							break;
						}
						else
						{
							inventory[i].stackSize += remain;
							inventory[upgradedSlot].stackSize -= remain;
						}
					}
				}
			}
		}
		
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
				
				continue;
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
				
				continue;
			}
		}
	}
	
	public void checkFuelOrKlein()
	{
		if (inventory[0] != null && inventory[0].getItem() instanceof IItemEmc)
		{
			IItemEmc itemEmc = ((IItemEmc) inventory[0].getItem());
			if(itemEmc.getStoredEmc(inventory[0]) != itemEmc.getMaximumEmc(inventory[0]))
			{
				hasChargeableItem = true;
				hasFuel = false;
			}
			else
			{
				hasChargeableItem = false;
			}
		}
		else
		{
			hasFuel = true;
			hasChargeableItem = false;
		}
	}
	
	public void updateEmc()
	{
		if (this.getStoredEmc() == 0)
		{
			return;
		}
		else if (hasChargeableItem)
		{
			double toSend = this.getStoredEmc() < emcGen ? this.getStoredEmc() : emcGen;
			
			double starEmc = ItemPE.getEmc(inventory[0]);
			int maxStarEmc = EMCHelper.getKleinStarMaxEmc(inventory[0]);
			
			if ((starEmc + toSend) > maxStarEmc)
			{
				toSend = maxStarEmc - starEmc;
			}
			
			ItemPE.addEmcToStack(inventory[0], toSend);
			this.removeEMC(toSend);
		}
		else if (hasFuel)
		{
			if (FuelMapper.getFuelUpgrade(inventory[0]) == null)
			{
				this.setInventorySlotContents(0, null);
			}

			ItemStack result = inventory[lockSlot] == null ? FuelMapper.getFuelUpgrade(inventory[0]) : inventory[lockSlot].copy();
			
			int upgradeCost = EMCHelper.getEmcValue(result) - EMCHelper.getEmcValue(inventory[0]);
			
			if (upgradeCost > 0 && this.getStoredEmc() >= upgradeCost)
			{
				ItemStack upgrade = inventory[upgradedSlot];

				if (inventory[upgradedSlot] == null)
				{
					this.removeEMC(upgradeCost);
					this.setInventorySlotContents(upgradedSlot, result);
					this.decrStackSize(0, 1);
				}
				else if (ItemHelper.basicAreStacksEqual(result, upgrade) && upgrade.stackSize < upgrade.getMaxStackSize())
				{
					this.removeEMC(upgradeCost);
					inventory[upgradedSlot].stackSize++;
					this.decrStackSize(0, 1);
				}
			}
		}
		else
		{
			double toSend = this.getStoredEmc() < emcGen ? this.getStoredEmc() : emcGen;
			this.sendToAllAcceptors(toSend);
			this.sendRelayBonus();
		}
	}
	
	private float getSunRelativeEmc(int emc)
	{
		return (float) getSunLevel() * emc / 16;
	}

	public ItemStack getChargingItem()
	{
		return inventory[0];
	}

	public double getEmcToNextGoal()
	{
		if (inventory[lockSlot] != null)
		{
			return EMCHelper.getEmcValue(inventory[lockSlot]) - EMCHelper.getEmcValue(inventory[0]);
		}
		else
		{
			return EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(inventory[0])) - EMCHelper.getEmcValue(inventory[0]);
		}
	}

	private double getItemCharge()
	{
		if (inventory[0] != null && inventory[0].getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) inventory[0].getItem()).getStoredEmc(inventory[0]);
		}
		
		return -1;
	}
	
	public int getKleinStarChargeScaled(int i)
	{
		if (inventory[0] == null || displayItemCharge <= 0)
		{
			return 0;
		}
		
		return ((int) Math.round(displayItemCharge * i / ((IItemEmc) inventory[0].getItem()).getMaximumEmc(inventory[0])));
	}
	
	public int getSunLevel()
	{
		if (worldObj.provider.isHellWorld)
		{
			return 16;
		}
		return worldObj.getBlockLightValue(xCoord, yCoord + 1, zCoord) + 1;
	}
	
	public int getEmcScaled(int i)
	{
		if (displayEmc == 0) 
		{
			return 0;
		}
		return ((int) Math.round(displayEmc * i / this.getMaximumEmc()));
	}
	
	public int getSunLevelScaled(int i)
	{
		return displaySunLevel * i / 16;
	}
	
	public int getFuelProgressScaled(int i)
	{
		if (inventory[0] == null || !FuelMapper.isStackFuel(inventory[0]))
		{
			return 0;
		}
		
		int reqEmc = 0;
		
		if (inventory[lockSlot] != null)
		{
			reqEmc = EMCHelper.getEmcValue(inventory[lockSlot]) - EMCHelper.getEmcValue(inventory[0]);
			
			if (reqEmc < 0)
			{
				return 0;
			}
		}
		else
		{
			if (FuelMapper.getFuelUpgrade(inventory[0]) == null)
			{
				this.setInventorySlotContents(0, null);
				return 0;
			}
			else
			{
				reqEmc = EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(inventory[0])) - EMCHelper.getEmcValue(inventory[0]);
			}

		}
		
		if (displayEmc >= reqEmc)
		{
			return i;
		}
		
		return displayEmc * i / reqEmc;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		storedFuelEmc = nbt.getDouble("FuelEMC");
		
		NBTTagList list = nbt.getTagList("Items", 10);
		inventory = new ItemStack[getSizeInventory()];
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNBT = list.getCompoundTagAt(i);
			
			byte slot = subNBT.getByte("Slot");
			
			if (slot >= 0 && slot < getSizeInventory())
			{
				inventory[slot] = ItemStack.loadItemStackFromNBT(subNBT);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("EMC", this.getStoredEmc());
		nbt.setDouble("FuelEMC", storedFuelEmc);
		
		NBTTagList list = new NBTTagList();
		
		for (int i = 0; i < getSizeInventory(); i++)
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
			{
				inventory[slot] = null;
			}
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
		return "tile.pe_collector_MK1.name";
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
		if (side == 0 || side == 1)
		{
			return new int[] {upgradedSlot};
		}
		
		return accessibleSlots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) 
	{
		if (side == 0 || side == 1)
		{
			return false;
		}
		
		return FuelMapper.isStackFuel(stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) 
	{
		if (side == 0 || side == 1)
		{
			return slot == upgradedSlot;
		}
		
		return false;
	}

	private void sendRelayBonus()
	{
		for (Map.Entry<ForgeDirection, TileEntity> entry: WorldHelper.getAdjacentTileEntitiesMapped(worldObj, this).entrySet())
		{
			ForgeDirection dir = entry.getKey();
			TileEntity tile = entry.getValue();

			if (tile instanceof RelayMK3Tile)
			{
				((RelayMK3Tile) tile).acceptEMC(dir, 0.5);
			}
			else if (tile instanceof RelayMK2Tile)
			{
				((RelayMK2Tile) tile).acceptEMC(dir, 0.15);
			}
			else if (tile instanceof RelayMK1Tile)
			{
				((RelayMK1Tile) tile).acceptEMC(dir, 0.05);
			}
		}
	}

	@Override
	public double provideEMC(ForgeDirection side, double toExtract)
	{
		double toRemove = Math.min(currentEMC, toExtract);
		removeEMC(toRemove);
		return toRemove;
	}
}
