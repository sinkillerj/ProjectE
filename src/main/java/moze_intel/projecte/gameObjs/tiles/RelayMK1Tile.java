package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class RelayMK1Tile extends TileEmc implements IEmcAcceptor, IEmcProvider
{
	private final ItemStackHandler input;
	private final ItemStackHandler output = new StackHandler(1);
	private final IItemHandler automationInput;
	private final IItemHandler automationOutput = new WrappedItemHandler(output, WrappedItemHandler.WriteMode.IN_OUT)
	{
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			return SlotPredicates.IITEMEMC.test(stack)
					? super.insertItem(slot, stack, simulate)
					: stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			ItemStack stack = getStackInSlot(slot);
			if (stack != null && stack.getItem() instanceof IItemEmc)
			{
				IItemEmc item = ((IItemEmc) stack.getItem());
				if (item.getStoredEmc(stack) >= item.getMaximumEmc(stack))
				{
					return super.extractItem(slot, amount, simulate);
				} else
				{
					return null;
				}
			}

			return super.extractItem(slot, amount, simulate);
		}
	};
	private final int chargeRate;

	public RelayMK1Tile()
	{
		this(7, Constants.RELAY_MK1_MAX, Constants.RELAY_MK1_OUTPUT);
	}
	
	RelayMK1Tile(int sizeInv, int maxEmc, int chargeRate)
	{
		super(maxEmc);
		this.chargeRate = chargeRate;
		input = new StackHandler(sizeInv)
		{
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
			{
				return SlotPredicates.RELAY_INV.test(stack)
						? super.insertItem(slot, stack, simulate)
						: stack;
			}
		};
		automationInput = new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN);
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
			if (side == EnumFacing.DOWN)
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationOutput);
			} else return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationInput);
		}
		return super.getCapability(cap, side);
	}

	private ItemStack getCharging()
	{
		return output.getStackInSlot(0);
	}

	private ItemStack getBurn()
	{
		return input.getStackInSlot(0);
	}

	public IItemHandler getInput()
	{
		return input;
	}

	public IItemHandler getOutput()
	{
		return output;
	}

	@Override
	public void update()
	{	
		if (worldObj.isRemote) 
		{
			return;
		}

		sendEmc();
		ItemHelper.compactInventory(input);
		
		ItemStack stack = getBurn();
		
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
				int emcVal = EMCHelper.getEmcSellValue(stack);
				
				if (emcVal > 0 && (this.getStoredEmc() + emcVal) <= this.getMaximumEmc())
				{
					this.addEMC(emcVal);
					getBurn().stackSize--;
					if (getBurn().stackSize == 0)
						input.setStackInSlot(0, null);
				}
			}
		}
		
		ItemStack chargeable = getCharging();
		
		if (chargeable != null && this.getStoredEmc() > 0 && chargeable.getItem() instanceof IItemEmc)
		{
			chargeItem(chargeable);
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

	public double getItemChargeProportion()
	{
		if (getCharging() != null && getCharging().getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) getCharging().getItem()).getStoredEmc(getCharging()) / ((IItemEmc) getCharging().getItem()).getMaximumEmc(getCharging());
		}

		return 0;
	}

	public double getInputBurnProportion()
	{
		if (getBurn() == null)
		{
			return 0;
		}

		if (getBurn().getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) getBurn().getItem()).getStoredEmc(getBurn()) / ((IItemEmc) getBurn().getItem()).getMaximumEmc(getBurn());
		}

		return getBurn().stackSize / (double) getBurn().getMaxStackSize();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		input.deserializeNBT(nbt.getCompoundTag("Input"));
		output.deserializeNBT(nbt.getCompoundTag("Output"));
	}
	
	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt = super.writeToNBT(nbt);
		nbt.setTag("Input", input.serializeNBT());
		nbt.setTag("Output", output.serializeNBT());
		return nbt;
	}

	@Override
	public double acceptEMC(@Nonnull EnumFacing side, double toAccept)
	{
		if (worldObj.getTileEntity(pos.offset(side)) instanceof RelayMK1Tile)
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
	public double provideEMC(@Nonnull EnumFacing side, double toExtract)
	{
		double toRemove = Math.min(currentEMC, toExtract);
		currentEMC -= toRemove;
		return toRemove;
	}
}
