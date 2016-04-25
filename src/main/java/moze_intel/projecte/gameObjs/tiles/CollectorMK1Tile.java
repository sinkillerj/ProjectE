package moze_intel.projecte.gameObjs.tiles;

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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CollectorMK1Tile extends TileEmc implements IEmcProvider
{
	private ItemStackHandler input = new StackHandler(getInvSize()) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
		{
			if (FuelMapper.isStackFuel(stack))
				return super.insertItem(slot, stack, simulate);
			else return stack;
		}
	};
	private ItemStackHandler auxSlots = new StackHandler(3);
	private IItemHandler public_auxSlots = new WrappedItemHandler(auxSlots, WrappedItemHandler.WriteMode.OUT) {
		@Override
		public ItemStack extractItem(int slot, int count, boolean simulate)
		{
			if (slot == UPGRADE_SLOT)
				return super.extractItem(slot, count, simulate);
			else return null;
		}
	};
	public static final int KLEIN_SLOT = 0;
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

	public IItemHandler getAux()
	{
		return auxSlots;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (side != null && side.getAxis().isVertical())
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(public_auxSlots);
			} else
			{
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(input);
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

	protected ItemStack getKlein()
	{
		return auxSlots.getStackInSlot(KLEIN_SLOT);
	}

	@Override
	public void update()
	{
		if (worldObj.isRemote) 
		{
			return;
		}
		
		sortInventory();
		checkFuelOrKlein();
		updateEmc();
	}
	
	private void sortInventory()
	{
		if (getUpgraded() != null)
		{
			if (!(getLock() != null
					&& getUpgraded().getItem() == getLock().getItem()
					&& getUpgraded().stackSize < getUpgraded().getMaxStackSize())) {
				auxSlots.setStackInSlot(UPGRADE_SLOT, ItemHandlerHelper.insertItemStacked(input, getUpgraded().copy(), false));
			}
		}
		 
		// todo compact input handler here
	}
	
	private void checkFuelOrKlein()
	{
		if (getKlein() != null && getKlein().getItem() instanceof IItemEmc)
		{
			IItemEmc itemEmc = ((IItemEmc) getKlein().getItem());
			if(itemEmc.getStoredEmc(getKlein()) != itemEmc.getMaximumEmc(getKlein()))
			{
				hasChargeableItem = true;
				hasFuel = false;
			}
			else
			{
				hasChargeableItem = false;
			}
		}
		else if (getKlein() != null)
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
			
			double starEmc = ItemPE.getEmc(getKlein());
			int maxStarEmc = EMCHelper.getKleinStarMaxEmc(getKlein());
			
			if ((starEmc + toSend) > maxStarEmc)
			{
				toSend = maxStarEmc - starEmc;
			}
			
			ItemPE.addEmcToStack(getKlein(), toSend);
			this.removeEMC(toSend);
		}
		else if (hasFuel)
		{
			if (FuelMapper.getFuelUpgrade(getKlein()) == null)
			{
				auxSlots.setStackInSlot(KLEIN_SLOT, null);
			}

			ItemStack result = getLock() == null ? FuelMapper.getFuelUpgrade(getKlein()) : getLock().copy();
			
			int upgradeCost = EMCHelper.getEmcValue(result) - EMCHelper.getEmcValue(getKlein());
			
			if (upgradeCost > 0 && this.getStoredEmc() >= upgradeCost)
			{
				ItemStack upgrade = getUpgraded();

				if (getUpgraded() == null)
				{
					this.removeEMC(upgradeCost);
					auxSlots.setStackInSlot(UPGRADE_SLOT, result);
					getKlein().stackSize--;
					if (getKlein().stackSize == 0)
						auxSlots.setStackInSlot(KLEIN_SLOT, null);
				}
				else if (ItemHelper.basicAreStacksEqual(result, upgrade) && upgrade.stackSize < upgrade.getMaxStackSize())
				{
					this.removeEMC(upgradeCost);
					getUpgraded().stackSize++;
					getKlein().stackSize--;
					if (getKlein().stackSize == 0)
						auxSlots.setStackInSlot(KLEIN_SLOT, null);
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
			return EMCHelper.getEmcValue(getLock()) - EMCHelper.getEmcValue(getKlein());
		}
		else
		{
			return EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(getKlein())) - EMCHelper.getEmcValue(getKlein());
		}
	}

	public double getItemCharge()
	{
		if (getKlein() != null && getKlein().getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) getKlein().getItem()).getStoredEmc(getKlein());
		}

		return -1;
	}

	public double getItemChargeProportion()
	{
		double charge = getItemCharge();

		if (getKlein() == null || charge <= 0 || !(getKlein().getItem() instanceof IItemEmc))
		{
			return -1;
		}

		return charge / ((IItemEmc) getKlein().getItem()).getMaximumEmc(getKlein());
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
		if (getKlein() == null || !FuelMapper.isStackFuel(getKlein()))
		{
			return 0;
		}

		int reqEmc;

		if (getLock() != null)
		{
			reqEmc = EMCHelper.getEmcValue(getLock()) - EMCHelper.getEmcValue(getKlein());

			if (reqEmc < 0)
			{
				return 0;
			}
		}
		else
		{
			if (FuelMapper.getFuelUpgrade(getKlein()) == null)
			{
				auxSlots.setStackInSlot(KLEIN_SLOT, null);
				return 0;
			}
			else
			{
				reqEmc = EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(getKlein())) - EMCHelper.getEmcValue(getKlein());
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
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("FuelEMC", storedFuelEmc);
		nbt.setTag("Input", input.serializeNBT());
		nbt.setTag("AuxSlots", auxSlots.serializeNBT());
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
	public double provideEMC(EnumFacing side, double toExtract)
	{
		double toRemove = Math.min(currentEMC, toExtract);
		removeEMC(toRemove);
		return toRemove;
	}
}
