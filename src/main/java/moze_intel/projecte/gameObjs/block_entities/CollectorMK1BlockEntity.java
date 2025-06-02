package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.api.block_entity.IRelay;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

	private double unprocessedEMC;
	private boolean hasChargeableItem;
	private boolean hasFuel;
	//Start as needing to check for compacting when loaded
	private boolean needsCompacting = true;

	public CollectorMK1BlockEntity(BlockPos pos, BlockState state) {
		this(PEBlockEntityTypes.COLLECTOR, pos, state, EnumCollectorTier.MK1);
	}

	public CollectorMK1BlockEntity(BlockEntityTypeRegistryObject<? extends CollectorMK1BlockEntity> type, BlockPos pos, BlockState state, EnumCollectorTier tier) {
		super(type, pos, state, tier.getStorage());
		this.emcGen = tier.getGenRate();
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
				return slot == UPGRADE_SLOT ? super.extractItem(slot, count, simulate) : ItemStack.EMPTY;
			}
		};
		this.joined = new CombinedInvWrapper(automationInput, automationAuxSlots);
	}

	@Override
	protected boolean canAcceptEmc() {
		//Collector accepts EMC from providers if it has fuel/chargeable. Otherwise, it sends it to providers
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
		collector.updateEmc(level, pos);
		collector.rotateUpgraded();
		collector.updateComparators(level, pos);
	}

	private void rotateUpgraded() {
		ItemStack upgraded = getUpgraded();
		if (!upgraded.isEmpty()) {
			ItemStack lock = getLock();
			if (lock.isEmpty() || upgraded.getItem() != lock.getItem() || upgraded.getCount() >= upgraded.getMaxStackSize()) {
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
				hasFuel = FuelMapper.isStackFuel(upgrading);
				hasChargeableItem = false;
			}
		} else {
			hasFuel = false;
			hasChargeableItem = false;
		}
	}

	private void updateEmc(@NotNull Level level, @NotNull BlockPos pos) {
		if (!this.hasMaxedEmc()) {
			unprocessedEMC += emcGen * (getSunLevel(level, pos) / 320.0f);
			if (unprocessedEMC >= 1) {
				//Force add the EMC regardless of if we can receive EMC from external sources
				unprocessedEMC -= forceInsertEmc((long) unprocessedEMC, EmcAction.EXECUTE);
			}
			//Note: We don't need to recheck comparators because it doesn't take the unprocessed emc into account
			markDirty(level, pos, false);
		}

		if (this.getStoredEmc() > 0) {
			ItemStack upgrading = getUpgrading();
			if (hasChargeableItem) {
				IItemEmcHolder emcHolder = upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
				if (emcHolder != null) {
					long actualInserted = emcHolder.insertEmc(upgrading, Math.min(getStoredEmc(), emcGen), EmcAction.EXECUTE);
					forceExtractEmc(actualInserted, EmcAction.EXECUTE);
				}
				return;
			} else if (hasFuel) {
				ItemStack fuelUpgrade = FuelMapper.getFuelUpgrade(upgrading);
				if (!fuelUpgrade.isEmpty()) {
					ItemStack lock = getLock();
					ItemStack result = lock.isEmpty() ? fuelUpgrade : lock.copy();

					long upgradeCost = IEMCProxy.INSTANCE.getValue(result) - IEMCProxy.INSTANCE.getValue(upgrading);

					if (upgradeCost >= 0 && this.getStoredEmc() >= upgradeCost) {
						ItemStack upgrade = getUpgraded();

						if (upgrade.isEmpty()) {
							forceExtractEmc(upgradeCost, EmcAction.EXECUTE);
							auxSlots.setStackInSlot(UPGRADE_SLOT, result);
							upgrading.shrink(1);
						} else if (result.is(upgrade.getItem()) && upgrade.getCount() < upgrade.getMaxStackSize()) {
							forceExtractEmc(upgradeCost, EmcAction.EXECUTE);
							upgrade.grow(1);
							upgrading.shrink(1);
							auxSlots.onContentsChanged(UPGRADE_SLOT);
						}
					}
					return;
				}
			}
			//Only send EMC when we are not upgrading fuel or charging an item
			long toSend = this.getStoredEmc() < emcGen ? this.getStoredEmc() : emcGen;
			this.sendToAllAcceptors(level, pos, toSend);
			sendRelayBonus(level, pos);
		}
	}

	@Range(from = 0, to = Long.MAX_VALUE)
	public long getEmcToNextGoal() {
		ItemStack lock = getLock();
		ItemStack upgrading = getUpgrading();
		long targetEmc;
		if (lock.isEmpty()) {
			targetEmc = IEMCProxy.INSTANCE.getValue(FuelMapper.getFuelUpgrade(upgrading));
		} else {
			targetEmc = IEMCProxy.INSTANCE.getValue(lock);
		}
		return Math.max(targetEmc - IEMCProxy.INSTANCE.getValue(upgrading), 0);
	}

	public long getItemCharge() {
		ItemStack upgrading = getUpgrading();
		IItemEmcHolder emcHolder = upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (emcHolder != null) {
			return emcHolder.getStoredEmc(upgrading);
		}
		return -1;
	}

	public double getItemChargeProportion() {
		long charge = getItemCharge();
		if (charge <= 0) {
			return -1;
		}
		ItemStack upgrading = getUpgrading();
		IItemEmcHolder emcHolder = upgrading.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (emcHolder == null) {
			return -1;
		}
		long max = emcHolder.getMaximumEmc(upgrading);
		if (charge >= max) {
			return 1;
		}
		return (double) charge / max;
	}

	public int getSunLevel() {
		return level == null ? 0 : getSunLevel(level, worldPosition);
	}

	public static int getSunLevel(@NotNull Level level, @NotNull BlockPos pos) {
		if (level.dimensionType().ultraWarm()) {
			return 16;
		}
		return level.getMaxLocalRawBrightness(pos.above()) + 1;
	}

	public double getFuelProgress() {
		ItemStack upgrading = getUpgrading();
		if (!FuelMapper.isStackFuel(upgrading)) {
			return 0;
		}
		long reqEmc;
		ItemStack lock = getLock();
		if (!lock.isEmpty()) {
			reqEmc = IEMCProxy.INSTANCE.getValue(lock) - IEMCProxy.INSTANCE.getValue(upgrading);
			if (reqEmc < 0) {
				return 0;
			}
		} else {
			ItemStack fuelUpgrade = FuelMapper.getFuelUpgrade(upgrading);
			if (fuelUpgrade.isEmpty()) {
				return 0;
			}
			reqEmc = IEMCProxy.INSTANCE.getValue(fuelUpgrade) - IEMCProxy.INSTANCE.getValue(upgrading);
		}
		if (getStoredEmc() >= reqEmc) {
			return 1;
		}
		return (double) getStoredEmc() / reqEmc;
	}

	@Override
	public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		unprocessedEMC = tag.getDouble("unprocessed_emc");
		input.deserializeNBT(registries, tag.getCompound("input"));
		auxSlots.deserializeNBT(registries, tag.getCompound("aux_slots"));
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putDouble("unprocessed_emc", unprocessedEMC);
		tag.put("input", input.serializeNBT(registries));
		tag.put("aux_slots", auxSlots.serializeNBT(registries));
	}

	private static void sendRelayBonus(@NotNull Level level, @NotNull BlockPos pos) {
		for (Direction dir : Constants.DIRECTIONS) {
			BlockPos relayPos = pos.relative(dir);
			BlockEntity blockEntity = WorldHelper.getBlockEntity(level, relayPos);
			if (blockEntity instanceof IRelay relay) {
				relay.addBonus(level, relayPos);
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