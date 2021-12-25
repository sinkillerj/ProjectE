package moze_intel.projecte.gameObjs.tiles;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.capability.managing.ICapabilityResolver;
import moze_intel.projecte.capability.managing.SidedItemHandlerResolver;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.container.CollectorMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class CollectorMK1Tile extends CapabilityTileEMC implements INamedContainerProvider {

	private final ItemStackHandler input = new StackHandler(getInvSize());
	private final ItemStackHandler auxSlots = new StackHandler(3);
	private final CombinedInvWrapper toSort = new CombinedInvWrapper(new RangedWrapper(auxSlots, UPGRADING_SLOT, UPGRADING_SLOT + 1), input);
	public static final int UPGRADING_SLOT = 0;
	public static final int UPGRADE_SLOT = 1;
	public static final int LOCK_SLOT = 2;

	private final long emcGen;
	private boolean hasChargeableItem;
	private boolean hasFuel;
	private long storedFuelEmc;
	private double unprocessedEMC;

	public CollectorMK1Tile() {
		this(PETileEntityTypes.COLLECTOR.get(), EnumCollectorTier.MK1);
	}

	public CollectorMK1Tile(TileEntityType<?> type, EnumCollectorTier tier) {
		super(type, tier.getStorage());
		this.emcGen = tier.getGenRate();
		itemHandlerResolver = new CollectorItemHandlerProvider();
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
	public void tick() {
		if (level != null && !level.isClientSide) {
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
			Optional<IItemEmcHolder> emcHolder = upgrading.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
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
			return upgrading.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).map(emcHolder -> emcHolder.getStoredEmc(upgrading)).orElse(-1L);
		}
		return -1;
	}

	public double getItemChargeProportion() {
		ItemStack upgrading = getUpgrading();
		long charge = getItemCharge();
		if (upgrading.isEmpty() || charge <= 0) {
			return -1;
		}
		Optional<IItemEmcHolder> emcHolder = upgrading.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
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
	public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
		super.load(state, nbt);
		storedFuelEmc = nbt.getLong("FuelEMC");
		input.deserializeNBT(nbt.getCompound("Input"));
		auxSlots.deserializeNBT(nbt.getCompound("AuxSlots"));
		unprocessedEMC = nbt.getDouble(Constants.NBT_KEY_UNPROCESSED_EMC);
	}

	@Nonnull
	@Override
	public CompoundNBT save(@Nonnull CompoundNBT nbt) {
		nbt = super.save(nbt);
		nbt.putLong("FuelEMC", storedFuelEmc);
		nbt.put("Input", input.serializeNBT());
		nbt.put("AuxSlots", auxSlots.serializeNBT());
		nbt.putDouble(Constants.NBT_KEY_UNPROCESSED_EMC, unprocessedEMC);
		return nbt;
	}

	private void sendRelayBonus() {
		for (Direction dir : Direction.values()) {
			RelayMK1Tile tile = WorldHelper.getTileEntity(RelayMK1Tile.class, level, worldPosition.relative(dir));
			if (tile != null) {
				//The other tiers of relay extend RelayMK1Tile and add the correct bonus
				tile.addBonus();
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
		return TextComponentUtil.build(PEBlocks.COLLECTOR);
	}

	private class CollectorItemHandlerProvider extends SidedItemHandlerResolver {

		private final ICapabilityResolver<IItemHandler> automationAuxSlots;
		private final ICapabilityResolver<IItemHandler> automationInput;
		private final ICapabilityResolver<IItemHandler> joined;

		protected CollectorItemHandlerProvider() {
			NonNullLazy<IItemHandler> automationInput = NonNullLazy.of(() -> new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN) {
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
					return SlotPredicates.COLLECTOR_INV.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
				}
			});
			NonNullLazy<IItemHandler> automationAuxSlots = NonNullLazy.of(() -> new WrappedItemHandler(auxSlots, WrappedItemHandler.WriteMode.OUT) {
				@Nonnull
				@Override
				public ItemStack extractItem(int slot, int count, boolean simulate) {
					if (slot == UPGRADE_SLOT) {
						return super.extractItem(slot, count, simulate);
					}
					return ItemStack.EMPTY;
				}
			});
			this.automationInput = BasicCapabilityResolver.getBasicItemHandlerResolver(automationInput);
			this.automationAuxSlots = BasicCapabilityResolver.getBasicItemHandlerResolver(automationAuxSlots);
			this.joined = BasicCapabilityResolver.getBasicItemHandlerResolver(() -> new CombinedInvWrapper((IItemHandlerModifiable) automationInput.get(),
					(IItemHandlerModifiable) automationAuxSlots.get()));
		}

		@Override
		protected ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction side) {
			if (side == null) {
				return joined;
			} else if (side.getAxis().isVertical()) {
				return automationAuxSlots;
			}
			return automationInput;
		}

		@Override
		public void invalidateAll() {
			joined.invalidateAll();
			automationInput.invalidateAll();
			automationAuxSlots.invalidateAll();
		}
	}
}