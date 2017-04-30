package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.NBTWhitelist;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class CondenserTile extends TileEmc implements IEmcAcceptor
{
	private final ItemStackHandler inputInventory = createInput();
	private final ItemStackHandler outputInventory = createOutput();
	private final IItemHandler automationInventory = createAutomationInventory();
	private final ItemStackHandler lock = new StackHandler(1);
	private boolean isAcceptingEmc;
	private int ticksSinceSync;
	public int displayEmc;
	public float lidAngle;
	public float prevLidAngle;
	public int numPlayersUsing;
	public int requiredEmc;

	public ItemStackHandler getLock()
	{
		return lock;
	}

	public ItemStackHandler getInput()
	{
		return inputInventory;
	}

	public ItemStackHandler getOutput()
	{
		return outputInventory;
	}

	protected ItemStackHandler createInput()
	{
		return new StackHandler(91);
	}

	protected ItemStackHandler createOutput()
	{
		return inputInventory;
	}

	protected IItemHandler createAutomationInventory()
	{
		return new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN_OUT)
		{
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
			{
				return SlotPredicates.HAS_EMC.test(stack) && !isStackEqualToLock(stack)
						? super.insertItem(slot, stack, simulate)
						: stack;
			}

			@Override
			public ItemStack extractItem(int slot, int max, boolean simulate)
			{
				if (getStackInSlot(slot) != null && isStackEqualToLock(getStackInSlot(slot)))
				{
					return super.extractItem(slot, max, simulate);
				}
				else
				{
					return null;
				}
			}
		};
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, @Nonnull EnumFacing side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Nonnull
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, @Nonnull EnumFacing side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationInventory);
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void update()
	{
		updateChest();

		if (this.getWorld().isRemote)
		{
			return;
		}

		checkLockAndUpdate();

		displayEmc = (int) this.getStoredEmc();

		if (lock.getStackInSlot(0) != null && requiredEmc != 0)
		{
			condense();
		}
	}

	private void checkLockAndUpdate()
	{
		if (lock.getStackInSlot(0) == null)
		{
			displayEmc = 0;
			requiredEmc = 0;
			this.isAcceptingEmc = false;
			return;
		}

		if (EMCHelper.doesItemHaveEmc(lock.getStackInSlot(0)))
		{
			int lockEmc = EMCHelper.getEmcValue(lock.getStackInSlot(0));

			if (requiredEmc != lockEmc)
			{
				requiredEmc = lockEmc;
				this.isAcceptingEmc = true;
			}
		}
		else
		{
			lock.setStackInSlot(0, null);

			displayEmc = 0;
			requiredEmc = 0;
			this.isAcceptingEmc = false;
		}
	}
	
	protected void condense()
	{
		for (int i = 0; i < inputInventory.getSlots(); i++)
		{
			ItemStack stack = inputInventory.getStackInSlot(i);
			
			if (stack == null || isStackEqualToLock(stack)) 
			{
				continue;
			}

			inputInventory.extractItem(i, 1, false);
			this.addEMC(EMCHelper.getEmcSellValue(stack));
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
		ItemStack lockCopy = lock.getStackInSlot(0).copy();

		if (lockCopy.hasTagCompound() && !NBTWhitelist.shouldDupeWithNBT(lockCopy))
		{
			lockCopy.setTagCompound(new NBTTagCompound());
		}

		ItemHandlerHelper.insertItemStacked(outputInventory, lockCopy, false);
	}
	
	protected boolean hasSpace()
	{
		for (int i = 0; i < outputInventory.getSlots(); i++)
		{
			ItemStack stack = outputInventory.getStackInSlot(i);
			
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
	
	public boolean isStackEqualToLock(ItemStack stack)
	{
		if (lock.getStackInSlot(0) == null)
		{
			return false;
		}

		if (NBTWhitelist.shouldDupeWithNBT(lock.getStackInSlot(0)))
		{
			return ItemHelper.areItemStacksEqual(lock.getStackInSlot(0), stack);
		}

		return ItemHelper.areItemStacksEqualIgnoreNBT(lock.getStackInSlot(0), stack);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		inputInventory.deserializeNBT(nbt.getCompoundTag("Input"));
		lock.deserializeNBT(nbt.getCompoundTag("LockSlot"));
	}
	
	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt = super.writeToNBT(nbt);
		nbt.setTag("Input", inputInventory.serializeNBT());
		nbt.setTag("LockSlot", lock.serializeNBT());
		return nbt;
	}

	private void updateChest()
	{
		if (++ticksSinceSync % 20 * 4 == 0)
		{
			worldObj.addBlockEvent(pos, ObjHandler.condenser, 1, numPlayersUsing);
		}

		prevLidAngle = lidAngle;
		float angleIncrement = 0.1F;

		if (numPlayersUsing > 0 && lidAngle == 0.0F)
		{
			worldObj.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
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
				worldObj.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
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
	public double acceptEMC(@Nonnull EnumFacing side, double toAccept)
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
