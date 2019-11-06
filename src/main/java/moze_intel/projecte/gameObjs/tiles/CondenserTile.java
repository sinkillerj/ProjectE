package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.event.PlayerAttemptCondenserSetEvent;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class CondenserTile extends ChestTileEmc implements INamedContainerProvider {

	protected final ItemStackHandler inputInventory = createInput();
	private final ItemStackHandler outputInventory = createOutput();
	private final LazyOptional<IItemHandler> automationInventory = LazyOptional.of(this::createAutomationInventory);
	@Nullable
	private ItemInfo lockInfo;
	private boolean isAcceptingEmc;
	public long displayEmc;
	public long requiredEmc;

	public CondenserTile() {
		this(ObjHandler.CONDENSER_TILE);
	}

	protected CondenserTile(TileEntityType<?> type) {
		super(type);
	}

	@Override
	protected boolean canAcceptEmc() {
		return isAcceptingEmc;
	}

	@Override
	protected boolean canProvideEmc() {
		return false;
	}

	@Nullable
	public ItemInfo getLockInfo() {
		return lockInfo;
	}

	public ItemStackHandler getInput() {
		return inputInventory;
	}

	public ItemStackHandler getOutput() {
		return outputInventory;
	}

	protected ItemStackHandler createInput() {
		return new StackHandler(91);
	}

	protected ItemStackHandler createOutput() {
		return inputInventory;
	}

	@Nonnull
	protected IItemHandler createAutomationInventory() {
		return new WrappedItemHandler(inputInventory, WrappedItemHandler.WriteMode.IN_OUT) {
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return SlotPredicates.HAS_EMC.test(stack) && !isStackEqualToLock(stack) ? super.insertItem(slot, stack, simulate) : stack;
			}

			@Nonnull
			@Override
			public ItemStack extractItem(int slot, int max, boolean simulate) {
				if (!getStackInSlot(slot).isEmpty() && isStackEqualToLock(getStackInSlot(slot))) {
					return super.extractItem(slot, max, simulate);
				}
				return ItemStack.EMPTY;
			}
		};
	}

	@Override
	public void remove() {
		super.remove();
		automationInventory.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return automationInventory.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		updateChest();
		if (!getWorld().isRemote) {
			checkLockAndUpdate();
			displayEmc = this.getStoredEmc();
			if (lockInfo != null && requiredEmc != 0) {
				condense();
			}
		}
	}

	private void checkLockAndUpdate() {
		if (lockInfo == null) {
			displayEmc = 0;
			requiredEmc = 0;
			this.isAcceptingEmc = false;
			return;
		}

		long lockEmc = EMCHelper.getEmcValue(lockInfo);
		if (lockEmc > 0) {
			if (requiredEmc != lockEmc) {
				requiredEmc = lockEmc;
				this.isAcceptingEmc = true;
			}
		} else {
			lockInfo = null;
			displayEmc = 0;
			requiredEmc = 0;
			this.isAcceptingEmc = false;
		}
	}

	protected void condense() {
		for (int i = 0; i < inputInventory.getSlots(); i++) {
			ItemStack stack = inputInventory.getStackInSlot(i);

			if (stack.isEmpty() || isStackEqualToLock(stack)) {
				continue;
			}

			inputInventory.extractItem(i, 1, false);
			forceInsertEmc(EMCHelper.getEmcSellValue(stack), EmcAction.EXECUTE);
			break;
		}

		if (this.getStoredEmc() >= requiredEmc && this.hasSpace()) {
			forceExtractEmc(requiredEmc, EmcAction.EXECUTE);
			pushStack();
		}
	}

	protected void pushStack() {
		if (lockInfo != null) {
			ItemHandlerHelper.insertItemStacked(outputInventory, lockInfo.createStack(), false);
		}
	}

	protected boolean hasSpace() {
		for (int i = 0; i < outputInventory.getSlots(); i++) {
			ItemStack stack = outputInventory.getStackInSlot(i);
			if (stack.isEmpty() || (isStackEqualToLock(stack) && stack.getCount() < stack.getMaxStackSize())) {
				return true;
			}
		}
		return false;
	}

	public boolean isStackEqualToLock(ItemStack stack) {
		if (lockInfo == null || stack.isEmpty()) {
			return false;
		}
		//Compare our lock to the persistent info that the stack would have
		return lockInfo.equals(NBTManager.getPersistentInfo(ItemInfo.fromStack(stack)));
	}

	public boolean attemptCondenserSet(PlayerEntity player) {
		if (world == null || world.isRemote) {
			return false;
		}
		if (lockInfo == null) {
			ItemStack stack = player.inventory.getItemStack();
			if (!stack.isEmpty()) {
				ItemInfo sourceInfo = ItemInfo.fromStack(stack);
				ItemInfo reducedInfo = NBTManager.getPersistentInfo(sourceInfo);
				if (!MinecraftForge.EVENT_BUS.post(new PlayerAttemptCondenserSetEvent(player, sourceInfo, reducedInfo))) {
					lockInfo = reducedInfo;
					markDirty();
					return true;
				}
			}
			return false;
		}
		lockInfo = null;
		markDirty();
		return true;
	}

	@Override
	public void read(@Nonnull CompoundNBT nbt) {
		super.read(nbt);
		inputInventory.deserializeNBT(nbt.getCompound("Input"));
		lockInfo = ItemInfo.read(nbt.getCompound("LockInfo"));
	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.put("Input", inputInventory.serializeNBT());
		if (lockInfo != null) {
			nbt.put("LockInfo", lockInfo.write(new CompoundNBT()));
		}
		return nbt;
	}

	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
		return new CondenserContainer(ObjHandler.CONDENSER_CONTAINER, windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ObjHandler.condenser.getTranslationKey());
	}
}