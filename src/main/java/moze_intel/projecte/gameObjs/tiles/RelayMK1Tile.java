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
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		{
			return SlotPredicates.IITEMEMC.test(stack)
					? super.insertItem(slot, stack, simulate)
					: stack;
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate)
		{
			ItemStack stack = getStackInSlot(slot);
			if (!stack.isEmpty() && stack.getItem() instanceof IItemEmc)
			{
				IItemEmc item = ((IItemEmc) stack.getItem());
				if (item.getStoredEmc(stack) >= item.getMaximumEmc(stack))
				{
					return super.extractItem(slot, amount, simulate);
				} else
				{
					return ItemStack.EMPTY;
				}
			}

			return super.extractItem(slot, amount, simulate);
		}
	};
	private final long chargeRate;
	private double bonusEMC;

	public RelayMK1Tile()
	{
		this(7, Constants.RELAY_MK1_MAX, Constants.RELAY_MK1_OUTPUT);
	}
	
	RelayMK1Tile(int sizeInv, long maxEmc, long chargeRate)
	{
		super(maxEmc);
		this.chargeRate = chargeRate;
		input = new StackHandler(sizeInv)
		{
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
			{
				return SlotPredicates.RELAY_INV.test(stack)
						? super.insertItem(slot, stack, simulate)
						: stack;
			}
		};
		automationInput = new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side)
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
		if (world.isRemote)
		{
			return;
		}

		sendEmc();
		ItemHelper.compactInventory(input);
		
		ItemStack stack = getBurn();
		
		if (!stack.isEmpty())
		{
			if(stack.getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) stack.getItem());
				long emcVal = itemEmc.getStoredEmc(stack);
				
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
				long emcVal = EMCHelper.getEmcSellValue(stack);
				
				if (emcVal > 0 && (this.getStoredEmc() + emcVal) <= this.getMaximumEmc())
				{
					this.addEMC(emcVal);
					getBurn().shrink(1);
				}
			}
		}
		
		ItemStack chargeable = getCharging();
		
		if (!chargeable.isEmpty() && this.getStoredEmc() > 0 && chargeable.getItem() instanceof IItemEmc)
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
		long starEmc = itemEmc.getStoredEmc(chargeable);
		long maxStarEmc = itemEmc.getMaximumEmc(chargeable);
		long toSend = this.getStoredEmc() < chargeRate ? this.getStoredEmc() : chargeRate;
			
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
		if (!getCharging().isEmpty() && getCharging().getItem() instanceof IItemEmc)
		{
			return (double) ((IItemEmc) getCharging().getItem()).getStoredEmc(getCharging()) / ((IItemEmc) getCharging().getItem()).getMaximumEmc(getCharging());
		}

		return 0;
	}

	public double getInputBurnProportion()
	{
		if (getBurn().isEmpty())
		{
			return 0;
		}

		if (getBurn().getItem() instanceof IItemEmc)
		{
			return (double) ((IItemEmc) getBurn().getItem()).getStoredEmc(getBurn()) / ((IItemEmc) getBurn().getItem()).getMaximumEmc(getBurn());
		}

		return getBurn().getCount() / (double) getBurn().getMaxStackSize();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		input.deserializeNBT(nbt.getCompoundTag("Input"));
		output.deserializeNBT(nbt.getCompoundTag("Output"));
		bonusEMC = nbt.getDouble("BonusEMC");
	}
	
	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt = super.writeToNBT(nbt);
		nbt.setTag("Input", input.serializeNBT());
		nbt.setTag("Output", output.serializeNBT());
		nbt.setDouble("BonusEMC", bonusEMC);
		return nbt;
	}

	@Override
	public long acceptEMC(@Nonnull EnumFacing side, long toAccept)
	{
		if (world.getTileEntity(pos.offset(side)) instanceof RelayMK1Tile)
		{
			return 0; // Do not accept from other relays - avoid infinite loop / thrashing
		}
		else
		{
			long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
			currentEMC += toAdd;
			return toAdd;
		}
	}

	public void addBonus(@Nonnull EnumFacing side, double bonus) {
		if (world.getTileEntity(pos.offset(side)) instanceof RelayMK1Tile)
		{
			return; // Do not accept from other relays - avoid infinite loop / thrashing
		}
		bonusEMC += bonus;
		if (bonusEMC >= 1) {
			long extraEMC = (long) bonusEMC;
			bonusEMC -= extraEMC;
			currentEMC += Math.min(maximumEMC - currentEMC, extraEMC);
		}
	}

	@Override
	public long provideEMC(@Nonnull EnumFacing side, long toExtract)
	{
		long toRemove = Math.min(currentEMC, toExtract);
		currentEMC -= toRemove;
		return toRemove;
	}
}
