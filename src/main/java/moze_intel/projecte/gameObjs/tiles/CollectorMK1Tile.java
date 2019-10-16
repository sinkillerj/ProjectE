package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcAcceptor;
import moze_intel.projecte.api.tile.IEmcProvider;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class CollectorMK1Tile extends TileEmc implements IEmcProvider, IEmcAcceptor, INamedContainerProvider
{
	private final ItemStackHandler input = new StackHandler(getInvSize());
	private final ItemStackHandler auxSlots = new StackHandler(3);
	private final CombinedInvWrapper toSort = new CombinedInvWrapper(new RangedWrapper(auxSlots, UPGRADING_SLOT, UPGRADING_SLOT + 1), input);
	private final LazyOptional<IItemHandler> automationInput = LazyOptional.of(() -> new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN)
	{
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		{
			return SlotPredicates.COLLECTOR_INV.test(stack)
					? super.insertItem(slot, stack, simulate)
					: stack;
		}
	});
	private final LazyOptional<IItemHandler> automationAuxSlots = LazyOptional.of(() -> new WrappedItemHandler(auxSlots, WrappedItemHandler.WriteMode.OUT) {
		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int count, boolean simulate)
		{
			if (slot == UPGRADE_SLOT)
				return super.extractItem(slot, count, simulate);
			else return ItemStack.EMPTY;
		}
	});
	public static final int UPGRADING_SLOT = 0;
	public static final int UPGRADE_SLOT = 1;
	public static final int LOCK_SLOT = 2;

	private final long emcGen;
	private boolean hasChargeableItem;
	private boolean hasFuel;
	private long storedFuelEmc;
	private double unprocessedEMC;

	public CollectorMK1Tile()
	{
		super(ObjHandler.COLLECTOR_MK1_TILE, Constants.COLLECTOR_MK1_MAX);
		emcGen = Constants.COLLECTOR_MK1_GEN;
	}
	
	public CollectorMK1Tile(TileEntityType<?> type, long maxEmc, long emcGen)
	{
		super(type, maxEmc);
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
	public void remove()
	{
		super.remove();
		automationInput.invalidate();
		automationAuxSlots.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if (side != null && side.getAxis().isVertical())
			{
				return automationAuxSlots.cast();
			} else
			{
				return automationInput.cast();
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
	public void tick()
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
			unprocessedEMC += emcGen * (getSunLevel() / 320.0f);
			if (unprocessedEMC >= 1) {
				long emcToAdd = (long) unprocessedEMC;
				this.addEMC(emcToAdd);
				unprocessedEMC -= emcToAdd;
			}
		}

		if (this.getStoredEmc() == 0)
		{
			return;
		}
		else if (hasChargeableItem)
		{
			long toSend = this.getStoredEmc() < emcGen ? this.getStoredEmc() : emcGen;
			IItemEmc item = (IItemEmc) getUpgrading().getItem();
			
			long itemEmc = item.getStoredEmc(getUpgrading());
			long maxItemEmc = item.getMaximumEmc(getUpgrading());
			
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
			
			if (upgradeCost >= 0 && this.getStoredEmc() >= upgradeCost)
			{
				ItemStack upgrade = getUpgraded();

				if (getUpgraded().isEmpty())
				{
					this.removeEMC(upgradeCost);
					auxSlots.setStackInSlot(UPGRADE_SLOT, result);
					getUpgrading().shrink(1);
				}
				else if (result.getItem() == upgrade.getItem() && upgrade.getCount() < upgrade.getMaxStackSize())
				{
					this.removeEMC(upgradeCost);
					getUpgraded().grow(1);
					getUpgrading().shrink(1);
				}
			}
		}
		else
		{
			//Only send EMC when we are not upgrading fuel or charging an item
			long toSend = this.getStoredEmc() < emcGen ? this.getStoredEmc() : emcGen;
			this.sendToAllAcceptors(toSend);
			this.sendRelayBonus();
		}
	}

	public long getEmcToNextGoal()
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

	public long getItemCharge()
	{
		if (!getUpgrading().isEmpty() && getUpgrading().getItem() instanceof IItemEmc)
		{
			return ((IItemEmc) getUpgrading().getItem()).getStoredEmc(getUpgrading());
		}

		return -1;
	}

	public double getItemChargeProportion()
	{
		long charge = getItemCharge();

		if (getUpgrading().isEmpty() || charge <= 0 || !(getUpgrading().getItem() instanceof IItemEmc))
		{
			return -1;
		}

		long max = ((IItemEmc) getUpgrading().getItem()).getMaximumEmc(getUpgrading());
		if (charge >= max)
		{
			return 1;
		}

		return (double) charge / max;
	}
	
	public int getSunLevel()
	{
		if (world.dimension.doesWaterVaporize())
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

		return (double) getStoredEmc() / reqEmc;
	}
	
	@Override
	public void read(CompoundNBT nbt)
	{
		super.read(nbt);
		storedFuelEmc = nbt.getLong("FuelEMC");
		input.deserializeNBT(nbt.getCompound("Input"));
		auxSlots.deserializeNBT(nbt.getCompound("AuxSlots"));
		unprocessedEMC = nbt.getDouble("UnprocessedEMC");
	}
	
	@Nonnull
	@Override
	public CompoundNBT write(CompoundNBT nbt)
	{
		nbt = super.write(nbt);
		nbt.putLong("FuelEMC", storedFuelEmc);
		nbt.put("Input", input.serializeNBT());
		nbt.put("AuxSlots", auxSlots.serializeNBT());
		nbt.putDouble("UnprocessedEMC", unprocessedEMC);
		return nbt;
	}

	private void sendRelayBonus()
	{
		for (Map.Entry<Direction, TileEntity> entry: WorldHelper.getAdjacentTileEntitiesMapped(world, this).entrySet())
		{
			Direction dir = entry.getKey();
			TileEntity tile = entry.getValue();

			if (tile instanceof RelayMK3Tile)
			{
				((RelayMK3Tile) tile).addBonus(dir, 0.5);
			}
			else if (tile instanceof RelayMK2Tile)
			{
				((RelayMK2Tile) tile).addBonus(dir, 0.15);
			}
			else if (tile instanceof RelayMK1Tile)
			{
				((RelayMK1Tile) tile).addBonus(dir, 0.05);
			}
		}
	}

	@Override
	public long provideEMC(@Nonnull Direction side, long toExtract)
	{
		long toRemove = Math.min(currentEMC, toExtract);
		removeEMC(toRemove);
		return toRemove;
	}

	@Nonnull
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerIn)
	{
		return new CollectorMK1Container(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName()
	{
		return new StringTextComponent(getType().getRegistryName().toString());
	}

	@Override
	public long acceptEMC(@Nonnull Direction side, long toAccept)
	{
		if (hasFuel || hasChargeableItem)
		{
			//Collector accepts EMC from providers if it has fuel/chargeable. Otherwise it sends it to providers
			long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
			currentEMC += toAdd;
			return toAdd;
		}
		return 0;
	}
}
