package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import java.util.Map;

public class CollectorMK1Tile extends TileEmc implements IEmcProvider
{
	private final ItemStackHandler input = new StackHandler(getInvSize());
	private final ItemStackHandler auxSlots = new StackHandler(3);
	private final CombinedInvWrapper toSort = new CombinedInvWrapper(new RangedWrapper(auxSlots, UPGRADING_SLOT, UPGRADING_SLOT + 1), input);
	private final IItemHandler automationInput = new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN)
	{
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		{
			return SlotPredicates.COLLECTOR_INV.test(stack)
					? super.insertItem(slot, stack, simulate)
					: stack;
		}
	};
	private final IItemHandler automationAuxSlots = new WrappedItemHandler(auxSlots, WrappedItemHandler.WriteMode.OUT) {
		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int count, boolean simulate)
		{
			if (slot == UPGRADE_SLOT)
				return super.extractItem(slot, count, simulate);
			else return ItemStack.EMPTY;
		}
	};
	public static final int UPGRADING_SLOT = 0;
	public static final int UPGRADE_SLOT = 1;
	public static final int LOCK_SLOT = 2;

	private final int emcGen;
	private boolean hasChargeableItem;
	private boolean hasFuel;
	private double storedFuelEmc;

	public CollectorMK1Tile()
	{
		super(Constants.COLLECTOR_MK1_MAX);
		emcGen = Constants.COLLECTOR_MK1_GEN;
	}
	
	public CollectorMK1Tile(int maxEmc, int emcGen)
	{
		super(maxEmc);
		this.emcGen = emcGen;
	}

	public IItemHandler getInput()
	{
		return input;
	}

	public IItemHandler getAux()
	{
		return auxSlots;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (side != null && side.getAxis().isVertical())
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationAuxSlots);
			} else
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automationInput);
			}
		}
		return super.getCapability(cap, side);
	}

	protected int getInvSize()
	{
		return 8;
	}

	private ItemStack getUpgraded()
	{
		return auxSlots.getStackInSlot(UPGRADE_SLOT);
	}

	private ItemStack getLock()
	{
		return auxSlots.getStackInSlot(LOCK_SLOT);
	}

	private ItemStack getUpgrading()
	{
		return auxSlots.getStackInSlot(UPGRADING_SLOT);
	}

	@Override
	public void update()
	{
		if (world.isRemote)
			return;

		ItemHelper.compactInventory(toSort);
		checkFuelOrKlein();
		updateEmc();
		rotateUpgraded();
	}

	private void rotateUpgraded()
	{
		if (!getUpgraded().isEmpty())
		{
			if (getLock().isEmpty()
					|| getUpgraded().getItem() != getLock().getItem()
					|| getUpgraded().getCount() >= getUpgraded().getMaxStackSize()) {
				auxSlots.setStackInSlot(UPGRADE_SLOT, ItemHandlerHelper.insertItemStacked(input, getUpgraded().copy(), false));
			}
		}
	}
	
	private void checkFuelOrKlein()
	{
		if (!getUpgrading().isEmpty() && getUpgrading().getItem() instanceof IItemEmc)
		{
			IItemEmc itemEmc = ((IItemEmc) getUpgrading().getItem());
			if(itemEmc.getStoredEmc(getUpgrading()) != itemEmc.getMaximumEmc(getUpgrading()))
			{
				hasChargeableItem = true;
				hasFuel = false;
			}
			else
			{
				hasChargeableItem = false;
			}
		}
		else if (!getUpgrading().isEmpty())
		{
			hasFuel = true;
			hasChargeableItem = false;
		} else
		{
			hasFuel = false;
			hasChargeableItem = false;
		}
	}
	
	private void updateEmc()
	{
		if (!this.hasMaxedEmc())
		{
			this.addEMC(getSunRelativeEmc(emcGen) / 20.0f);
		}

		if (this.getStoredEmc() == 0)
		{
			return;
		}
		else if (hasChargeableItem)
		{
			double toSend = this.getStoredEmc() < emcGen ? this.getStoredEmc() : emcGen;
			IItemEmc item = (IItemEmc) getUpgrading().getItem();
			
			double itemEmc = item.getStoredEmc(getUpgrading());
			double maxItemEmc = item.getMaximumEmc(getUpgrading());
			
			if ((itemEmc + toSend) > maxItemEmc)
			{
				toSend = maxItemEmc - itemEmc;
			}
			
			item.addEmc(getUpgrading(), toSend);
			this.removeEMC(toSend);
		}
		else if (hasFuel)
		{
			if (FuelMapper.getFuelUpgrade(getUpgrading()).isEmpty())
			{
				auxSlots.setStackInSlot(UPGRADING_SLOT, ItemStack.EMPTY);
			}

			ItemStack result = getLock().isEmpty() ? FuelMapper.getFuelUpgrade(getUpgrading()) : getLock().copy();
			
			long upgradeCost = EMCHelper.getEmcValue(result) - EMCHelper.getEmcValue(getUpgrading());
			
			if (upgradeCost > 0 && this.getStoredEmc() >= upgradeCost)
			{
				ItemStack upgrade = getUpgraded();

				if (getUpgraded().isEmpty())
				{
					this.removeEMC(upgradeCost);
					auxSlots.setStackInSlot(UPGRADE_SLOT, result);
					getUpgrading().shrink(1);
				}
				else if (ItemHelper.basicAreStacksEqual(result, upgrade) && upgrade.getCount() < upgrade.getMaxStackSize())
				{
					this.removeEMC(upgradeCost);
					getUpgraded().grow(1);
					getUpgrading().shrink(1);
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

	public double getEmcToNextGoal()
	{
		if (!getLock().isEmpty())
		{
			return EMCHelper.getEmcValue(getLock()) - EMCHelper.getEmcValue(getUpgrading());
		}
		else
		{
			return EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(getUpgrading())) - EMCHelper.getEmcValue(getUpgrading());
		}
	}

	public double getItemCharge()
	{
		if (!getUpgrading().isEmpty() && getUpgrading().getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) getUpgrading().getItem()).getStoredEmc(getUpgrading());
		}

		return -1;
	}

	public double getItemChargeProportion()
	{
		double charge = getItemCharge();

		if (getUpgrading().isEmpty() || charge <= 0 || !(getUpgrading().getItem() instanceof IItemEmc))
		{
			return -1;
		}

		return charge / ((IItemEmc) getUpgrading().getItem()).getMaximumEmc(getUpgrading());
	}
	
	public int getSunLevel()
	{
		if (world.provider.doesWaterVaporize())
		{
			return 16;
		}
		return world.getLight(getPos().up()) + 1;
	}

	public double getFuelProgress()
	{
		if (getUpgrading().isEmpty() || !FuelMapper.isStackFuel(getUpgrading()))
		{
			return 0;
		}

		long reqEmc;

		if (!getLock().isEmpty())
		{
			reqEmc = EMCHelper.getEmcValue(getLock()) - EMCHelper.getEmcValue(getUpgrading());

			if (reqEmc < 0)
			{
				return 0;
			}
		}
		else
		{
			if (FuelMapper.getFuelUpgrade(getUpgrading()).isEmpty())
			{
				auxSlots.setStackInSlot(UPGRADING_SLOT, ItemStack.EMPTY);
				return 0;
			}
			else
			{
				reqEmc = EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(getUpgrading())) - EMCHelper.getEmcValue(getUpgrading());
			}

		}

		if (getStoredEmc() >= reqEmc)
		{
			return 1;
		}

		return getStoredEmc() / reqEmc;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		storedFuelEmc = nbt.getDouble("FuelEMC");
		input.deserializeNBT(nbt.getCompoundTag("Input"));
		auxSlots.deserializeNBT(nbt.getCompoundTag("AuxSlots"));
	}
	
	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt = super.writeToNBT(nbt);
		nbt.setDouble("FuelEMC", storedFuelEmc);
		nbt.setTag("Input", input.serializeNBT());
		nbt.setTag("AuxSlots", auxSlots.serializeNBT());
		return nbt;
	}

	private void sendRelayBonus()
	{
		for (Map.Entry<EnumFacing, TileEntity> entry: WorldHelper.getAdjacentTileEntitiesMapped(world, this).entrySet())
		{
			EnumFacing dir = entry.getKey();
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
	public double provideEMC(@Nonnull EnumFacing side, double toExtract)
	{
		double toRemove = Math.min(currentEMC, toExtract);
		removeEMC(toRemove);
		return toRemove;
	}
}
