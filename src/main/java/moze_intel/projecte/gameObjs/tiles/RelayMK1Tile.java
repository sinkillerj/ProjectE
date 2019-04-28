package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RelayMK1Tile extends TileEmc implements IEmcAcceptor, IEmcProvider, IInteractionObject
{
	private final ItemStackHandler input;
	private final ItemStackHandler output = new StackHandler(1);
	private final LazyOptional<IItemHandler> automationInput;
	private final LazyOptional<IItemHandler> automationOutput = LazyOptional.of(() -> new WrappedItemHandler(output, WrappedItemHandler.WriteMode.IN_OUT)
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
	});
	private final long chargeRate;

	public RelayMK1Tile()
	{
		this(ObjHandler.RELAY_MK1_TILE, 7, Constants.RELAY_MK1_MAX, Constants.RELAY_MK1_OUTPUT);
	}
	
	RelayMK1Tile(TileEntityType<?> type, int sizeInv, long maxEmc, long chargeRate)
	{
		super(type, maxEmc);
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
		automationInput = LazyOptional.of(() -> new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN));
	}

	@Override
	public void remove()
	{
		super.remove();
		automationInput.invalidate();
		automationOutput.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, EnumFacing side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (side == EnumFacing.DOWN)
			{
				return automationOutput.cast();
			} else return automationInput.cast();
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
	public void tick()
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
		if (!getCharging().isEmpty() && getCharging().getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) getCharging().getItem()).getStoredEmc(getCharging()) / ((IItemEmc) getCharging().getItem()).getMaximumEmc(getCharging());
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
			return ((IItemEmc) getBurn().getItem()).getStoredEmc(getBurn()) / ((IItemEmc) getBurn().getItem()).getMaximumEmc(getBurn());
		}

		return getBurn().getCount() / (double) getBurn().getMaxStackSize();
	}
	
	@Override
	public void read(NBTTagCompound nbt)
	{
		super.read(nbt);
		input.deserializeNBT(nbt.getCompound("Input"));
		output.deserializeNBT(nbt.getCompound("Output"));
	}
	
	@Nonnull
	@Override
	public NBTTagCompound write(NBTTagCompound nbt)
	{
		nbt = super.write(nbt);
		nbt.put("Input", input.serializeNBT());
		nbt.put("Output", output.serializeNBT());
		return nbt;
	}

	@Override
	public double acceptEMC(@Nonnull EnumFacing side, double toAccept)
	{
		if (world.getTileEntity(pos.offset(side)) instanceof RelayMK1Tile)
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

	@Nonnull
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
	{
		return new RelayMK1Container(playerInventory, this);
	}

	@Nonnull
	@Override
	public String getGuiID()
	{
		return getType().getRegistryName().toString();
	}

	@Nonnull
	@Override
	public ITextComponent getName()
	{
		return new TextComponentString(getGuiID());
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Nullable
	@Override
	public ITextComponent getCustomName()
	{
		return null;
	}
}
