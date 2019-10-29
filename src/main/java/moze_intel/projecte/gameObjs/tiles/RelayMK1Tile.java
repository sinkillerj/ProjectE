package moze_intel.projecte.gameObjs.tiles;

import java.util.Optional;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.LazyOptionalHelper;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class RelayMK1Tile extends TileEmc implements INamedContainerProvider {

	private final ItemStackHandler input;
	private final ItemStackHandler output = new StackHandler(1);
	private final LazyOptional<IItemHandler> automationInput;
	private final LazyOptional<IItemHandler> automationOutput = LazyOptional.of(() -> new WrappedItemHandler(output, WrappedItemHandler.WriteMode.IN_OUT) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return SlotPredicates.IITEMEMC.test(stack) ? super.insertItem(slot, stack, simulate) : stack;
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			ItemStack stack = getStackInSlot(slot);
			if (!stack.isEmpty()) {
				Optional<IItemEmcHolder> holderCapability = LazyOptionalHelper.toOptional(stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
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
	private final long chargeRate;
	private double bonusEMC;

	public RelayMK1Tile() {
		this(ObjHandler.RELAY_MK1_TILE, 7, Constants.RELAY_MK1_MAX, Constants.RELAY_MK1_OUTPUT);
	}

	RelayMK1Tile(TileEntityType<?> type, int sizeInv, long maxEmc, long chargeRate) {
		super(type, maxEmc);
		this.chargeRate = chargeRate;
		input = new StackHandler(sizeInv) {
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return SlotPredicates.RELAY_INV.test(stack)
					   ? super.insertItem(slot, stack, simulate)
					   : stack;
			}
		};
		automationInput = LazyOptional.of(() -> new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN));
	}

	@Override
	public boolean isRelay() {
		return true;
	}

	@Override
	public void remove() {
		super.remove();
		automationInput.invalidate();
		automationOutput.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == Direction.DOWN) {
				return automationOutput.cast();
			}
			return automationInput.cast();
		}
		return super.getCapability(cap, side);
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
			Optional<IItemEmcHolder> holderCapability = LazyOptionalHelper.toOptional(stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
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
			Optional<IItemEmcHolder> holderCapability = LazyOptionalHelper.toOptional(charging.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
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
		Optional<IItemEmcHolder> holderCapability = LazyOptionalHelper.toOptional(burn.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY));
		if (holderCapability.isPresent()) {
			IItemEmcHolder emcHolder = holderCapability.get();
			return (double) emcHolder.getStoredEmc(burn) / emcHolder.getMaximumEmc(burn);
		}
		return burn.getCount() / (double) burn.getMaxStackSize();
	}

	@Override
	public void read(@Nonnull CompoundNBT nbt) {
		super.read(nbt);
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

	//TODO: Clean this up
	public void addBonus(@Nonnull Direction side, double bonus) {
		if (world.getTileEntity(pos.offset(side)) instanceof RelayMK1Tile) {
			return; // Do not accept from other relays - avoid infinite loop / thrashing
		}
		bonusEMC += bonus;
		if (bonusEMC >= 1) {
			bonusEMC -= forceInsertEmc((long) bonusEMC, EmcAction.EXECUTE);
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
		return new TranslationTextComponent(ObjHandler.relay.getTranslationKey());
	}
}