package moze_intel.projecte.gameObjs.tiles;

import java.util.Optional;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.LazyOptionalHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class CollectorMK1Tile extends TileEmc implements INamedContainerProvider {

	private final ItemStackHandler input = new StackHandler(getInvSize());
	private final ItemStackHandler auxSlots = new StackHandler(3);
	private final CombinedInvWrapper toSort = new CombinedInvWrapper(new RangedWrapper(auxSlots, UPGRADING_SLOT, UPGRADING_SLOT + 1), input);
	private final LazyOptional<IItemHandler> automationInput = LazyOptional.of(() -> new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return SlotPredicates.COLLECTOR_INV.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	});
	private final LazyOptional<IItemHandler> automationAuxSlots = LazyOptional.of(() -> new WrappedItemHandler(auxSlots, WrappedItemHandler.WriteMode.OUT) {
		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int count, boolean simulate) {
			if (slot == UPGRADE_SLOT) {
				return super.extractItem(slot, count, simulate);
			}
			return ItemStack.EMPTY;
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

	public CollectorMK1Tile() {
		this(ObjHandler.COLLECTOR_MK1_TILE, EnumCollectorTier.MK1);
	}

	public CollectorMK1Tile(TileEntityType<?> type, EnumCollectorTier tier) {
		super(type, tier.getStorage());
		this.emcGen = tier.getGenRate();
	}

	@Override
	protected boolean canAcceptEmc() {
		//Collector accepts EMC from providers if it has fuel/chargeable. Otherwise it sends it to providers
		return hasFuel || hasChargeableItem;
	}

	public IItemHandler getInput() {
		return input;
	}

	public IItemHandler getAux() {
		return auxSlots;
	}

	@Override
	public void remove() {
		super.remove();
		automationInput.invalidate();
		automationAuxSlots.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side != null && side.getAxis().isVertical()) {
				return automationAuxSlots.cast();
			}
			return automationInput.cast();
		}
		return super.getCapability(cap, side);
	}

	protected int getInvSize() {
		return 8;
	}

	private ItemStack getUpgraded() {
		return auxSlots.getStackInSlot(UPGRADE_SLOT);
	}

	private ItemStack getLock() {
		return auxSlots.getStackInSlot(LOCK_SLOT);
	}

	private ItemStack getUpgrading() {
		return auxSlots.getStackInSlot(UPGRADING_SLOT);
	}

	@Override
	public void tick() {
		if (!world.isRemote) {
			ItemHelper.compactInventory(toSort);
			checkFuelOrKlein();
			updateEmc();
			rotateUpgraded();
		}
	}

	private void rotateUpgraded() {
		ItemStack upgraded = getUpgraded();
		if (!upgraded.isEmpty()) {
			if (getLock().isEmpty() || upgraded.getItem() != getLock().getItem() || upgraded.getCount() >= upgraded.getMaxStackSize()) {
				auxSlots.setStackInSlot(UPGRADE_SLOT, ItemHandlerHelper.insertItemStacked(input, upgraded.copy(), false));
			}
		}
	}

	private void checkFuelOrKlein() {
		ItemStack upgrading = getUpgrading();
		if (!upgrading.isEmpty()) {
			Optional<IItemEmcHolder> emcHolder = LazyOptionalHelper.toOptional(upgrading.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
			if (emcHolder.isPresent()) {
				if (emcHolder.get().getNeededEmc(upgrading) > 0) {
					hasChargeableItem = true;
					hasFuel = false;
				} else {
					hasChargeableItem = false;
				}
			} else {
				hasFuel = true;
				hasChargeableItem = false;
			}
		} else {
			hasFuel = false;
			hasChargeableItem = false;
		}
	}

	private void updateEmc() {
		if (!this.hasMaxedEmc()) {
			unprocessedEMC += emcGen * (getSunLevel() / 320.0f);
			if (unprocessedEMC >= 1) {
				//Force add the EMC regardless of if we can receive EMC from external sources
				unprocessedEMC -= forceInsertEmc((long) unprocessedEMC, EmcAction.EXECUTE);
			}
		}

		if (this.getStoredEmc() > 0) {
			ItemStack upgrading = getUpgrading();
			if (hasChargeableItem) {
				upgrading.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(emcHolder -> {
					long actualInserted = emcHolder.insertEmc(upgrading, Math.min(getStoredEmc(), emcGen), EmcAction.EXECUTE);
					forceExtractEmc(actualInserted, EmcAction.EXECUTE);
				});
			} else if (hasFuel) {
				if (FuelMapper.getFuelUpgrade(upgrading).isEmpty()) {
					auxSlots.setStackInSlot(UPGRADING_SLOT, ItemStack.EMPTY);
				}

				ItemStack result = getLock().isEmpty() ? FuelMapper.getFuelUpgrade(upgrading) : getLock().copy();

				long upgradeCost = EMCHelper.getEmcValue(result) - EMCHelper.getEmcValue(upgrading);

				if (upgradeCost >= 0 && this.getStoredEmc() >= upgradeCost) {
					ItemStack upgrade = getUpgraded();

					if (getUpgraded().isEmpty()) {
						forceExtractEmc(upgradeCost, EmcAction.EXECUTE);
						auxSlots.setStackInSlot(UPGRADE_SLOT, result);
						upgrading.shrink(1);
					} else if (result.getItem() == upgrade.getItem() && upgrade.getCount() < upgrade.getMaxStackSize()) {
						forceExtractEmc(upgradeCost, EmcAction.EXECUTE);
						getUpgraded().grow(1);
						upgrading.shrink(1);
					}
				}
			} else {
				//Only send EMC when we are not upgrading fuel or charging an item
				long toSend = this.getStoredEmc() < emcGen ? this.getStoredEmc() : emcGen;
				this.sendToAllAcceptors(toSend);
				this.sendRelayBonus();
			}
		}
	}

	public long getEmcToNextGoal() {
		if (!getLock().isEmpty()) {
			return EMCHelper.getEmcValue(getLock()) - EMCHelper.getEmcValue(getUpgrading());
		}
		return EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(getUpgrading())) - EMCHelper.getEmcValue(getUpgrading());
	}

	public long getItemCharge() {
		ItemStack upgrading = getUpgrading();
		if (!upgrading.isEmpty()) {
			return LazyOptionalHelper.toOptional(upgrading.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY)).map(emcHolder ->
					emcHolder.getStoredEmc(upgrading)).orElse(-1L);
		}
		return -1;
	}

	public double getItemChargeProportion() {
		ItemStack upgrading = getUpgrading();
		long charge = getItemCharge();
		if (upgrading.isEmpty() || charge <= 0) {
			return -1;
		}
		Optional<IItemEmcHolder> emcHolder = LazyOptionalHelper.toOptional(upgrading.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
		if (emcHolder.isPresent()) {
			long max = emcHolder.get().getMaximumEmc(upgrading);
			if (charge >= max) {
				return 1;
			}
			return (double) charge / max;
		}
		return -1;
	}

	public int getSunLevel() {
		if (world.dimension.doesWaterVaporize()) {
			return 16;
		}
		return world.getLight(getPos().up()) + 1;
	}

	public double getFuelProgress() {
		if (getUpgrading().isEmpty() || !FuelMapper.isStackFuel(getUpgrading())) {
			return 0;
		}
		long reqEmc;
		if (!getLock().isEmpty()) {
			reqEmc = EMCHelper.getEmcValue(getLock()) - EMCHelper.getEmcValue(getUpgrading());
			if (reqEmc < 0) {
				return 0;
			}
		} else {
			if (FuelMapper.getFuelUpgrade(getUpgrading()).isEmpty()) {
				auxSlots.setStackInSlot(UPGRADING_SLOT, ItemStack.EMPTY);
				return 0;
			}
			reqEmc = EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(getUpgrading())) - EMCHelper.getEmcValue(getUpgrading());
		}
		if (getStoredEmc() >= reqEmc) {
			return 1;
		}
		return (double) getStoredEmc() / reqEmc;
	}

	@Override
	public void read(@Nonnull CompoundNBT nbt) {
		super.read(nbt);
		storedFuelEmc = nbt.getLong("FuelEMC");
		input.deserializeNBT(nbt.getCompound("Input"));
		auxSlots.deserializeNBT(nbt.getCompound("AuxSlots"));
		unprocessedEMC = nbt.getDouble("UnprocessedEMC");
	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.putLong("FuelEMC", storedFuelEmc);
		nbt.put("Input", input.serializeNBT());
		nbt.put("AuxSlots", auxSlots.serializeNBT());
		nbt.putDouble("UnprocessedEMC", unprocessedEMC);
		return nbt;
	}

	private void sendRelayBonus() {
		for (Direction dir : Direction.values()) {
			TileEntity tile = world.getTileEntity(getPos().offset(dir));
			if (tile instanceof RelayMK1Tile) {
				//The other tiers of relay extend RelayMK1Tile and add the correct bonus
				((RelayMK1Tile) tile).addBonus();
			}
		}
	}

	@Nonnull
	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
		return new CollectorMK1Container(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(getType().getRegistryName().toString());
	}
}