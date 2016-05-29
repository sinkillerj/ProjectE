package moze_intel.projecte.gameObjs.tiles;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.handlers.TileEntityHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.CondenserSyncPKT;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.NBTWhitelist;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

public class CondenserTile extends TileEmcDirection implements IInventory, ISidedInventory, IEmcAcceptor
{
	protected ItemStack[] inventory;
	private ItemStack lock;
	protected boolean loadChecks;
	protected boolean isAcceptingEmc;
	private int ticksSinceSync;
	public int displayEmc;
	public float lidAngle;
	public float prevLidAngle;
	public int numPlayersUsing;
	public int requiredEmc;

	public CondenserTile()
	{
		inventory = new ItemStack[92];
		loadChecks = false;
	}

	@Override
	public void updateEntity()
	{
		updateChest();

		if (this.worldObj.isRemote)
		{
			return;
		}

		if (!loadChecks)
		{
			TileEntityHandler.addCondenser(this);
			checkLockAndUpdate();
			loadChecks = true;
		}

		displayEmc = (int) this.getStoredEmc();

		if (lock != null && requiredEmc != 0)
		{
			condense();
		}
		
		if (numPlayersUsing > 0)
		{
			PacketHandler.sendToAllAround(new CondenserSyncPKT(displayEmc, requiredEmc, this.xCoord, this.yCoord, this.zCoord),
				new TargetPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 8));
		}
	}

	public void checkLockAndUpdate()
	{
		lock = inventory[0];

		if (lock == null)
		{
			displayEmc = 0;
			requiredEmc = 0;
			this.isAcceptingEmc = false;
			return;
		}

		if (EMCHelper.doesItemHaveEmc(lock))
		{
			int lockEmc = EMCHelper.getEmcValue(lock);

			if (requiredEmc != lockEmc)
			{
				requiredEmc = lockEmc;
				this.isAcceptingEmc = true;
			}
		}
		else
		{
			lock = null;
			inventory[0] = null;

			displayEmc = 0;
			requiredEmc = 0;
			this.isAcceptingEmc = false;
		}
	}
	
	protected void condense()
	{
		for (int i = 1; i < 92; i++)
		{
			ItemStack stack = getStackInSlot(i);
			
			if (stack == null || isStackEqualToLock(stack)) 
			{
				continue;
			}

			if (stack.stackSize <= 0)
			{
				inventory[i] = null;
				continue;
			}
			decrStackSize(i, 1);
			this.addEMC(EMCHelper.getEmcValue(stack));
			break;
		}
		
		if (this.getStoredEmc() >= requiredEmc && this.hasSpace())
		{
			this.removeEMC(requiredEmc);
			pushStack();
		}
	}
	
	protected void pushStack()
	{
		int slot = getSlotForStack();
		
		if (slot == 0) 
		{
			return;
		}
		if (inventory[slot] == null)
		{
			ItemStack lockCopy = lock.copy();
			
			if (lockCopy.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(lockCopy))
			{
				lockCopy.setTagCompound(new NBTTagCompound());
			}
			
			inventory[slot] = lockCopy;
		}
		else
		{
			inventory[slot].stackSize += 1;
		}
		this.markDirty();
	}
	
	protected int getSlotForStack()
	{
		for (int i = 1; i < inventory.length; i++)
		{
			ItemStack stack = inventory[i];

			if (stack == null) 
			{
				return i;
			}
			
			if (isStackEqualToLock(stack) && stack.stackSize < stack.getMaxStackSize())
			{
				return i;
			}
		}

		return 0;
	}
	
	protected boolean hasSpace()
	{
		for (int i = 1; i < inventory.length; i++)
		{
			ItemStack stack = inventory[i];
			
			if (stack == null) 
			{
				return true;
			}
			
			if (isStackEqualToLock(stack) && stack.stackSize < stack.getMaxStackSize()) 
			{
				return true;
			}
		}

		return false;
	}
	
	protected boolean isStackEqualToLock(ItemStack stack)
	{
		if (lock == null) 
		{
			return false;
		}

		if (NBTWhitelist.shouldDupeWithNBT(lock))
		{
			return ItemHelper.areItemStacksEqual(lock, stack);
		}

		return ItemHelper.areItemStacksEqualIgnoreNBT(lock, stack);
	}
	
	public int getProgressScaled()
	{
		if (requiredEmc == 0) 
		{
			return 0;
		}

		if (displayEmc >= requiredEmc) 
		{
			return Constants.MAX_CONDENSER_PROGRESS;
		}
		
		return (displayEmc * Constants.MAX_CONDENSER_PROGRESS) / requiredEmc;
	}

	@Override
	public void invalidate()
	{
		super.invalidate();

		loadChecks = false;

		if (!this.worldObj.isRemote)
		{
			TileEntityHandler.removeCondenser(this);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("Items", 10);

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNBT = list.getCompoundTagAt(i);
			inventory[subNBT.getByte("Slot")] = ItemStack.loadItemStackFromNBT(subNBT);
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagList list = new NBTTagList();

		for (int i = 0; i < inventory.length; i++)
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
				{
					inventory[slot] = null;
				}
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
		return "tile.pe_condenser.name";
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

	public void updateChest()
	{
		if (++ticksSinceSync % 20 * 4 == 0)
		{
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.condenser, 1, numPlayersUsing);
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
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.condenser, 1, numPlayersUsing);
	}
	
	@Override
	public void closeInventory()
	{
		--numPlayersUsing;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ObjHandler.condenser, 1, numPlayersUsing);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		if (slot == 0) 
		{
			return false;
		}
		
		return !isStackEqualToLock(stack) && EMCHelper.doesItemHaveEmc(stack);
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) 
	{
		int[] slots = new int[inventory.length - 1];
		
		for (int i = 1; i < inventory.length; i++)
		{
			slots[i - 1] = i;
		}
		
		return slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) 
	{
		if (slot == 0) 
		{
			return false;
		}

		return isItemValidForSlot(slot, item);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) 
	{
		if (slot == 0) 
		{
			return false;
		}

		return isStackEqualToLock(item);
	}

	@Override
	public double acceptEMC(ForgeDirection side, double toAccept)
	{
		if (isAcceptingEmc)
		{
			double toAdd = Math.min(maximumEMC - currentEMC, toAccept);
			addEMC(toAdd);
			return toAdd;
		}
		else
		{
			return 0;
		}
	}
}
