package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class CollectorMK1BlockEntity extends EmcBlockEntity implements MenuProvider {

	public static final ICapabilityProvider<CollectorMK1BlockEntity, @Nullable Direction, IItemHandler> INVENTORY_PROVIDER = (collector, side) -> {
		if (side == null) {
			return collector.joined;
		} else if (side.getAxis().isVertical()) {
			return collector.automationAuxSlots;
		}
		return collector.automationInput;
	};

	private final ItemStackHandler input = new StackHandler(getInvSize()) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			needsCompacting = true;
		}
	};
	private final StackHandler auxSlots = new StackHandler(3) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			if (slot == UPGRADING_SLOT) {
				needsCompacting = true;
			}
		}
	};
	private final CombinedInvWrapper toSort = new CombinedInvWrapper(new RangedWrapper(auxSlots, UPGRADING_SLOT, UPGRADING_SLOT + 1), input);
	public static final int UPGRADING_SLOT = 0;
	public static final int UPGRADE_SLOT = 1;
	public static final int LOCK_SLOT = 2;

	private final IItemHandlerModifiable automationAuxSlots;
	private final IItemHandlerModifiable automationInput;
	private final IItemHandler joined;

	private final long emcGen;

	private boolean hasChargeableItem;
	private boolean hasFuel;
	private double unprocessedEMC;
	//Start as needing to check for compacting when loaded
	private boolean needsCompacting = true;

	public CollectorMK1BlockEntity(BlockPos pos, BlockState state) {
		this(PEBlockEntityTypes.COLLECTOR, pos, state, EnumCollectorTier.MK1);
	}

	public CollectorMK1BlockEntity(BlockEntityTypeRegistryObject<? extends CollectorMK1BlockEntity> type, BlockPos pos, BlockState state, EnumCollectorTier tier) {
		super(type, pos, state, tier.getStorage());
		this.emcGen = tier.getGenRate();

		//TODO - 1.20.4: Re-evaluate and test this
		this.automationInput = new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN) {
			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				return SlotPredicates.COLLECTOR_INV.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
			}
		};
		this.automationAuxSlots = new WrappedItemHandler(auxSlots, WrappedItemHandler.WriteMode.OUT) {
			@NotNull
			@Override
			public ItemStack extractItem(int slot, int count, boolean simulate) {
				if (slot == UPGRADE_SLOT) {
					return super.extractItem(slot, count, simulate);
				}
				return ItemStack.EMPTY;
			}
		};
		this.joined = new CombinedInvWrapper(automationInput, automationAuxSlots);
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

	public void clearLocked() {
		auxSlots.setStackInSlot(LOCK_SLOT, ItemStack.EMPTY);
	}

	@Override
	protected boolean emcAffectsComparators() {
		return true;
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, CollectorMK1BlockEntity collector) {
		if (collector.needsCompacting) {
			ItemHelper.compactInventory(collector.toSort);
			collector.needsCompacting = false;
		}
		collector.checkFuelOrKlein();
		collector.updateEmc();
		collector.rotateUpgraded();
		collector.updateComparators();
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
			IItemEmcHolder emcHolder = upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				if (emcHolder.getNeededEmc(upgrading) > 0) {
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
			//Note: We don't need to recheck comparators because it doesn't take the unprocessed emc into account
			markDirty(false);
		}

		if (this.getStoredEmc() > 0) {
			ItemStack upgrading = getUpgrading();
			if (hasChargeableItem) {
				IItemEmcHolder emcHolder = upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
				if (emcHolder != null) {
					long actualInserted = emcHolder.insertEmc(upgrading, Math.min(getStoredEmc(), emcGen), EmcAction.EXECUTE);
					forceExtractEmc(actualInserted, EmcAction.EXECUTE);
				}
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
						auxSlots.onContentsChanged(UPGRADE_SLOT);
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

	@Range(from = 0, to = Long.MAX_VALUE)
	public long getEmcToNextGoal() {
		ItemStack lock = getLock();
		ItemStack upgrading = getUpgrading();
		long targetEmc;
		if (lock.isEmpty()) {
			targetEmc = EMCHelper.getEmcValue(FuelMapper.getFuelUpgrade(upgrading));
		} else {
			targetEmc = EMCHelper.getEmcValue(lock);
		}
		return Math.max(targetEmc - EMCHelper.getEmcValue(upgrading), 0);
	}

	public long getItemCharge() {
		ItemStack upgrading = getUpgrading();
		if (!upgrading.isEmpty()) {
			IItemEmcHolder emcHolder = upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				return emcHolder.getStoredEmc(upgrading);
			}
		}
		return -1;
	}

	public double getItemChargeProportion() {
		ItemStack upgrading = getUpgrading();
		long charge = getItemCharge();
		if (upgrading.isEmpty() || charge <= 0) {
			return -1;
		}
		IItemEmcHolder emcHolder = upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (emcHolder != null) {
			long max = emcHolder.getMaximumEmc(upgrading);
			if (charge >= max) {
				return 1;
			}
			return (double) charge / max;
		}
		return -1;
	}

	public int getSunLevel() {
		if (level.dimensionType().ultraWarm()) {
			return 16;
		}
		return level.getMaxLocalRawBrightness(worldPosition.above()) + 1;
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
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		input.deserializeNBT(nbt.getCompound("Input"));
		auxSlots.deserializeNBT(nbt.getCompound("AuxSlots"));
		unprocessedEMC = nbt.getDouble(Constants.NBT_KEY_UNPROCESSED_EMC);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Input", input.serializeNBT());
		tag.put("AuxSlots", auxSlots.serializeNBT());
		tag.putDouble(Constants.NBT_KEY_UNPROCESSED_EMC, unprocessedEMC);
	}

	private void sendRelayBonus() {
		for (Direction dir : Direction.values()) {
			RelayMK1BlockEntity relay = WorldHelper.getBlockEntity(RelayMK1BlockEntity.class, level, worldPosition.relative(dir));
			if (relay != null) {
				//The other tiers of relay extend RelayMK1BlockEntity and add the correct bonus
				relay.addBonus();
			}
		}
	}

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerIn) {
		return new CollectorMK1Container(windowId, playerInventory, this);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return TextComponentUtil.build(PEBlocks.COLLECTOR);
	}
}