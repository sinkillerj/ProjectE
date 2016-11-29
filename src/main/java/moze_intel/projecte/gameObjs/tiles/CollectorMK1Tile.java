package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.items.ItemPE;
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
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			return SlotPredicates.COLLECTOR_INV.test(stack)
					? super.insertItem(slot, stack, simulate)
					: stack;
		}
	};
	private final IItemHandler automationAuxSlots = new WrappedItemHandler(auxSlots, WrappedItemHandler.WriteMode.OUT) {
		@Override
		public ItemStack extractItem(int slot, int count, boolean simulate)
		{
			if (slot == UPGRADE_SLOT)
				return super.extractItem(slot, count, simulate);
			else return null;
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
	public boolean hasCapability(@Nonnull Capability<?> cap, @Nonnull EnumFacing side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Nonnull
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, @Nonnull EnumFacing side) {
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
		if (worldObj.isRemote)
			return;

		ItemHelper.compactInventory(toSort);
		checkFuelOrKlein();
		updateEmc();
		rotateUpgraded();
	}

	private void rotateUpgraded()
	{
		if (getUpgraded() != null)
		{
			if (getLock() == null
					|| getUpgraded().getItem() != getLock().getItem()
					|| getUpgraded().stackSize >= getUpgraded().getMaxStackSize()) {
				auxSlots.setStackInSlot(UPGRADE_SLOT, ItemHandlerHelper.insertItemStacked(input, getUpgraded().copy(), false));
			}
		}
	}
	
	private void checkFuelOrKlein()
	{
		if (getUpgrading() != null && getUpgrading().getItem() instanceof IItemEmc)
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
		else if (getUpgrading() != null)
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
			
			double starEmc = ItemPE.getEmc(getUpgrading());
			int maxStarEmc = EMCHelper.getKleinStarMaxEmc(getUpgrading());
			
			if ((starEmc + toSend) > maxStarEmc)
			{
				toSend = maxStarEmc - starEmc;
			}
			
			ItemPE.addEmcToStack(getUpgrading(), toSend);
			this.removeEMC(toSend);
		}
		else if (hasFuel)
		{
			if (FuelMapper.getFuelUpgrade(getUpgrading()) == null)
			{
				auxSlots.setStackInSlot(UPGRADING_SLOT, null);
			}

			ItemStack result = getLock() == null ? FuelMapper.getFuelUpgrade(getUpgrading()) : getLock().copy();
			
			int upgradeCost = EMCHelper.getEmcValue(result) - EMCHelper.getEmcValue(getUpgrading());
			
			if (upgradeCost > 0 && this.getStoredEmc() >= upgradeCost)
			{
				ItemStack upgrade = getUpgraded();

				if (getUpgraded() == null)
				{
					this.removeEMC(upgradeCost);
					auxSlots.setStackInSlot(UPGRADE_SLOT, result);
					getUpgrading().stackSize--;
					if (getUpgrading().stackSize == 0)
						auxSlots.setStackInSlot(UPGRADING_SLOT, null);
				}
				else if (ItemHelper.basicAreStacksEqual(result, upgrade) && upgrade.stackSize < upgrade.getMaxStackSize())
				{
					this.removeEMC(upgradeCost);
					getUpgraded().stackSize++;
					getUpgrading().stackSize--;
					if (getUpgrading().stackSize == 0)
						auxSlots.setStackInSlot(UPGRADING_SLOT, null);
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
		if (getLock() != null)
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
		if (getUpgrading() != null && getUpgrading().getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) getUpgrading().getItem()).getStoredEmc(getUpgrading());
		}

		return -1;
	}

	public double getItemChargeProportion()
	{
		double charge = getItemCharge();

		if (getUpgrading() == null || charge <= 0 || !(getUpgrading().getItem() instanceof IItemEmc))
		{
			return -1;
		}

		return charge / ((IItemEmc) getUpgrading().getItem()).getMaximumEmc(getUpgrading());
	}
	
	public int getSunLevel()
	{
		if (worldObj.provider.doesWaterVaporize())
		{
			return 16;
		}
		return worldObj.getLight(getPos().up()) + 1;
	}

	public double getFuelProgress()
	{
		if (getUpgrading() == null || !FuelMapper.isStackFuel(getUpgrading()))
		{
			return 0;
		}

		int reqEmc;

		if (getLock() != null)
		{
			reqEmc = EMCHelper.getEmcValue(getLock()) - EMCHelper.getEmcValue(getUpgrading());

			if (reqEmc < 0)
			{
				return 0;
			}
		}
		else
		{
			if (FuelMapper.getFuelUpgrade(getUpgrading()) == null)
			{
				auxSlots.setStackInSlot(UPGRADING_SLOT, null);
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
		for (Map.Entry<EnumFacing, TileEntity> entry: WorldHelper.getAdjacentTileEntitiesMapped(worldObj, this).entrySet())
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
