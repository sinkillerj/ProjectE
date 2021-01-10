package moze_intel.projecte.gameObjs.tiles;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.capability.managing.ICapabilityResolver;
import moze_intel.projecte.capability.managing.SidedItemHandlerResolver;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class RelayMK1Tile extends CapabilityTileEMC implements INamedContainerProvider {

	private final ItemStackHandler input;
	private final ItemStackHandler output = new StackHandler(1);
	private final long chargeRate;
	private double bonusEMC;

	public RelayMK1Tile() {
		this(PETileEntityTypes.RELAY.get(), 7, EnumRelayTier.MK1);
	}

	RelayMK1Tile(TileEntityType<?> type, int sizeInv, EnumRelayTier tier) {
		super(type, tier.getStorage());
		this.chargeRate = tier.getChargeRate();
		input = new StackHandler(sizeInv) {
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
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
	public void tick() {
		if (world.isRemote) {
			return;
		}
		sendEmc();
		ItemHelper.compactInventory(input);
		ItemStack stack = getBurn();
		if (!stack.isEmpty()) {
			Optional<IItemEmcHolder> holderCapability = stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
			if (holderCapability.isPresent()) {
				IItemEmcHolder emcHolder = holderCapability.get();
				long simulatedVal = forceInsertEmc(emcHolder.extractEmc(stack, chargeRate, EmcAction.SIMULATE), EmcAction.SIMULATE);
				if (simulatedVal > 0) {
					forceInsertEmc(emcHolder.extractEmc(stack, simulatedVal, EmcAction.EXECUTE), EmcAction.EXECUTE);
				}
			} else {
				long emcVal = EMCHelper.getEmcSellValue(stack);
				if (emcVal > 0 && emcVal <= getNeededEmc()) {
					forceInsertEmc(emcVal, EmcAction.EXECUTE);
					getBurn().shrink(1);
				}
			}
		}
		ItemStack chargeable = getCharging();
		if (!chargeable.isEmpty() && this.getStoredEmc() > 0) {
			chargeable.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(emcHolder -> {
				long actualSent = emcHolder.insertEmc(chargeable, Math.min(getStoredEmc(), chargeRate), EmcAction.EXECUTE);
				forceExtractEmc(actualSent, EmcAction.EXECUTE);
			});
		}
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
			Optional<IItemEmcHolder> holderCapability = charging.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
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
		Optional<IItemEmcHolder> holderCapability = burn.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
		if (holderCapability.isPresent()) {
			IItemEmcHolder emcHolder = holderCapability.get();
			return (double) emcHolder.getStoredEmc(burn) / emcHolder.getMaximumEmc(burn);
		}
		return burn.getCount() / (double) burn.getMaxStackSize();
	}

	@Override
	public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
		super.read(state, nbt);
		input.deserializeNBT(nbt.getCompound("Input"));
		output.deserializeNBT(nbt.getCompound("Output"));
		bonusEMC = nbt.getDouble("BonusEMC");
	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.put("Input", input.serializeNBT());
		nbt.put("Output", output.serializeNBT());
		nbt.putDouble("BonusEMC", bonusEMC);
		return nbt;
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
	}

	@Nonnull
	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
		return new RelayMK1Container(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(PEBlocks.RELAY.getBlock().getTranslationKey());
	}

	private class RelayItemHandlerProvider extends SidedItemHandlerResolver {

		private final ICapabilityResolver<IItemHandler> automationOutput;
		private final ICapabilityResolver<IItemHandler> automationInput;
		private final ICapabilityResolver<IItemHandler> joined;

		protected RelayItemHandlerProvider() {
			NonNullLazy<IItemHandler> automationInput = NonNullLazy.of(() -> new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN));
			NonNullLazy<IItemHandler> automationOutput = NonNullLazy.of(() -> new WrappedItemHandler(output, WrappedItemHandler.WriteMode.IN_OUT) {
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
					return SlotPredicates.EMC_HOLDER.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
				}

				@Nonnull
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					ItemStack stack = getStackInSlot(slot);
					if (!stack.isEmpty()) {
						Optional<IItemEmcHolder> holderCapability = stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
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