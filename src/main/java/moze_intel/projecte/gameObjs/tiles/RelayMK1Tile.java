package moze_intel.projecte.gameObjs.tiles;

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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Arrays;

public class RelayMK1Tile extends TileEmc implements IEmcAcceptor, IEmcProvider
{
	private final ItemStackHandler input;
	private final ItemStackHandler output = new StackHandler(1, false, true)
	{
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
				}
			}

			return super.extractItem(slot, amount, simulate);
		}
	};
	private final int chargeRate;
	public int displayEmc;
	public double displayChargingEmc;
	public double displayRawEmc;

	public RelayMK1Tile()
	{
		this(6, Constants.RELAY_MK1_MAX, Constants.RELAY_MK1_OUTPUT);
	}
	
	public RelayMK1Tile(int sizeInv, int maxEmc, int chargeRate)
	{
		super(maxEmc);
		this.chargeRate = chargeRate;
		input = new StackHandler(sizeInv, true, false) {
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
			{
				if (EMCHelper.doesItemHaveEmc(stack))
					return super.insertItem(slot, stack, simulate);
				else return stack;
			}
		};
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (side == EnumFacing.DOWN)
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(output);
			} else return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(input);
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
	
	@Override
	public void update()
	{	
		if (worldObj.isRemote) 
		{
			return;
		}

		sendEmc();
		sortInventory();
		
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
				int emcVal = EMCHelper.getEmcValue(stack);
				
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
		
		displayEmc = (int) this.getStoredEmc();
		displayChargingEmc = getChargingEMC();
		displayRawEmc = getRawEmc();
		
//		if (numUsing > 0) todo 1.9
//		{
//			PacketHandler.sendToAllAround(new RelaySyncPKT(displayEmc, displayChargingEmc, displayRawEmc, this),
//					new TargetPoint(this.worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 8));
//		}
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
		// todo compact input
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
		if (getCharging() != null && getCharging().getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) getCharging().getItem()).getStoredEmc(getCharging());
		}
		
		return 0;
	}
	
	public int getChargingEMCScaled(int i)
	{
		if (getCharging() != null && getCharging().getItem() instanceof IItemEmc)
		{
			return ((int) Math.round(displayChargingEmc * i / ((IItemEmc) getCharging().getItem()).getMaximumEmc(getCharging())));
		}
		
		return 0;
	}
	
	private double getRawEmc()
	{
		if (getBurn() == null)
		{
			return 0;
		}
		
		if (getBurn().getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) getBurn().getItem()).getStoredEmc(getBurn());
		}
		
		return EMCHelper.getEmcValue(getBurn()) * getBurn().stackSize;
	}
	
	public int getRawEmcScaled(int i)
	{
		if (getBurn() == null)
		{
			return 0;
		}
		
		if (getBurn().getItem() instanceof IItemEmc)
		{
			return (int) Math.round(displayRawEmc * i / ((IItemEmc) getBurn().getItem()).getMaximumEmc(getBurn()));
		}
		
		int emc = EMCHelper.getEmcValue(getBurn());
		
		return MathHelper.floor_double(displayRawEmc * i / (emc * getBurn().getMaxStackSize()));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		input.deserializeNBT(nbt.getCompoundTag("Input"));
		output.deserializeNBT(nbt.getCompoundTag("Output"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setTag("Input", input.serializeNBT());
		nbt.setTag("Output", output.serializeNBT());
	}

	@Override
	public double acceptEMC(EnumFacing side, double toAccept)
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
	public double provideEMC(EnumFacing side, double toExtract)
	{
		double toRemove = Math.min(currentEMC, toExtract);
		currentEMC -= toRemove;
		return toRemove;
	}
}
