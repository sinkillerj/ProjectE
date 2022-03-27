package moze_intel.projecte.gameObjs.block_entities;

import java.util.Optional;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.capability.managing.ICapabilityResolver;
import moze_intel.projecte.capability.managing.SidedItemHandlerResolver;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.text.PELang;
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
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelayMK1BlockEntity extends CapabilityEmcBlockEntity implements MenuProvider {

	private final CompactableStackHandler input;
	private final ItemStackHandler output = new StackHandler(1);
	private final long chargeRate;
	private double bonusEMC;

	public RelayMK1BlockEntity(BlockPos pos, BlockState state) {
		this(PEBlockEntityTypes.RELAY, pos, state, 7, EnumRelayTier.MK1);
	}

	RelayMK1BlockEntity(BlockEntityTypeRegistryObject<? extends RelayMK1BlockEntity> type, BlockPos pos, BlockState state, int sizeInv, EnumRelayTier tier) {
		super(type, pos, state, tier.getStorage());
		this.chargeRate = tier.getChargeRate();
		input = new CompactableStackHandler(sizeInv) {
			@NotNull
			@Override
			public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				return SlotPredicates.RELAY_INV.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
			}
		};
		itemHandlerResolver = new RelayItemHandlerProvider();
	}

	@Override
	public boolean isRelay() {
		return true;
	}

	private ItemStack getCharging() {
		return output.getStackInSlot(0);
	}

	private ItemStack getBurn() {
		return input.getStackInSlot(0);
	}

	public IItemHandler getInput() {
		return input;
	}

	public IItemHandler getOutput() {
		return output;
	}

	@Override
	protected boolean emcAffectsComparators() {
		return true;
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, RelayMK1BlockEntity relay) {
		relay.sendEmc();
		relay.input.compact();
		ItemStack stack = relay.getBurn();
		if (!stack.isEmpty()) {
			Optional<IItemEmcHolder> holderCapability = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
			if (holderCapability.isPresent()) {
				IItemEmcHolder emcHolder = holderCapability.get();
				long simulatedVal = relay.forceInsertEmc(emcHolder.extractEmc(stack, relay.chargeRate, EmcAction.SIMULATE), EmcAction.SIMULATE);
				if (simulatedVal > 0) {
					relay.forceInsertEmc(emcHolder.extractEmc(stack, simulatedVal, EmcAction.EXECUTE), EmcAction.EXECUTE);
				}
			} else {
				long emcVal = EMCHelper.getEmcSellValue(stack);
				if (emcVal > 0 && emcVal <= relay.getNeededEmc()) {
					relay.forceInsertEmc(emcVal, EmcAction.EXECUTE);
					relay.getBurn().shrink(1);
				}
			}
		}
		ItemStack chargeable = relay.getCharging();
		if (!chargeable.isEmpty() && relay.getStoredEmc() > 0) {
			chargeable.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(emcHolder -> {
				long actualSent = emcHolder.insertEmc(chargeable, Math.min(relay.getStoredEmc(), relay.chargeRate), EmcAction.EXECUTE);
				relay.forceExtractEmc(actualSent, EmcAction.EXECUTE);
			});
		}
		relay.updateComparators();
	}

	private void sendEmc() {
		if (this.getStoredEmc() == 0) {
			return;
		}
		if (this.getStoredEmc() <= chargeRate) {
			this.sendToAllAcceptors(this.getStoredEmc());
		} else {
			this.sendToAllAcceptors(chargeRate);
		}
	}

	public double getItemChargeProportion() {
		ItemStack charging = getCharging();
		if (!charging.isEmpty()) {
			Optional<IItemEmcHolder> holderCapability = charging.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
			if (holderCapability.isPresent()) {
				IItemEmcHolder emcHolder = holderCapability.get();
				return (double) emcHolder.getStoredEmc(charging) / emcHolder.getMaximumEmc(charging);
			}
		}
		return 0;
	}

	public double getInputBurnProportion() {
		ItemStack burn = getBurn();
		if (burn.isEmpty()) {
			return 0;
		}
		Optional<IItemEmcHolder> holderCapability = burn.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
		if (holderCapability.isPresent()) {
			IItemEmcHolder emcHolder = holderCapability.get();
			return (double) emcHolder.getStoredEmc(burn) / emcHolder.getMaximumEmc(burn);
		}
		return burn.getCount() / (double) burn.getMaxStackSize();
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		input.deserializeNBT(nbt.getCompound("Input"));
		output.deserializeNBT(nbt.getCompound("Output"));
		bonusEMC = nbt.getDouble("BonusEMC");
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Input", input.serializeNBT());
		tag.put("Output", output.serializeNBT());
		tag.putDouble("BonusEMC", bonusEMC);
	}

	protected double getBonusToAdd() {
		return 0.05;
	}

	public void addBonus() {
		bonusEMC += getBonusToAdd();
		if (bonusEMC >= 1) {
			long emcToInsert = (long) bonusEMC;
			forceInsertEmc(emcToInsert, EmcAction.EXECUTE);
			//Don't subtract the actual amount we managed to insert so that we do not continue to grow to
			// an infinite amount of "bonus" emc if our buffer is full.
			bonusEMC -= emcToInsert;
		}
		markDirty(false);
	}

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
		return new RelayMK1Container(windowId, playerInventory, this);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return PELang.GUI_RELAY_MK1.translate();
	}

	private class RelayItemHandlerProvider extends SidedItemHandlerResolver {

		private final ICapabilityResolver<IItemHandler> automationOutput;
		private final ICapabilityResolver<IItemHandler> automationInput;
		private final ICapabilityResolver<IItemHandler> joined;

		protected RelayItemHandlerProvider() {
			NonNullLazy<IItemHandler> automationInput = NonNullLazy.of(() -> new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN));
			NonNullLazy<IItemHandler> automationOutput = NonNullLazy.of(() -> new WrappedItemHandler(output, WrappedItemHandler.WriteMode.IN_OUT) {
				@NotNull
				@Override
				public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
					return SlotPredicates.EMC_HOLDER.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
				}

				@NotNull
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					ItemStack stack = getStackInSlot(slot);
					if (!stack.isEmpty()) {
						Optional<IItemEmcHolder> holderCapability = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
						if (holderCapability.isPresent()) {
							IItemEmcHolder emcHolder = holderCapability.get();
							if (emcHolder.getNeededEmc(stack) == 0) {
								return super.extractItem(slot, amount, simulate);
							}
							return ItemStack.EMPTY;
						}
					}
					return super.extractItem(slot, amount, simulate);
				}
			});
			this.automationInput = BasicCapabilityResolver.getBasicItemHandlerResolver(automationInput);
			this.automationOutput = BasicCapabilityResolver.getBasicItemHandlerResolver(automationOutput);
			this.joined = BasicCapabilityResolver.getBasicItemHandlerResolver(() -> new CombinedInvWrapper((IItemHandlerModifiable) automationInput.get(),
					(IItemHandlerModifiable) automationOutput.get()));
		}

		@Override
		protected ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction side) {
			if (side == null) {
				return joined;
			} else if (side.getAxis().isVertical()) {
				return automationOutput;
			}
			return automationInput;
		}

		@Override
		public void invalidateAll() {
			joined.invalidateAll();
			automationInput.invalidateAll();
			automationOutput.invalidateAll();
		}
	}
}