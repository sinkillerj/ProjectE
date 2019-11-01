package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class CondenserTile extends TileEmc implements INamedContainerProvider {

	protected final ItemStackHandler inputInventory = createInput();
	private final ItemStackHandler outputInventory = createOutput();
	private final LazyOptional<IItemHandler> automationInventory = LazyOptional.of(this::createAutomationInventory);
	private final ItemStackHandler lock = new StackHandler(1);
	private boolean isAcceptingEmc;
	private int ticksSinceSync;
	public long displayEmc;
	public float lidAngle;
	public float prevLidAngle;
	public int numPlayersUsing;
	public long requiredEmc;

	public CondenserTile() {
		this(ObjHandler.CONDENSER_TILE);
	}

	CondenserTile(TileEntityType<?> type) {
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

	public ItemStackHandler getLock() {
		return lock;
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

		if (this.getWorld().isRemote) {
			return;
		}

		checkLockAndUpdate();

		displayEmc = this.getStoredEmc();

		if (!lock.getStackInSlot(0).isEmpty() && requiredEmc != 0) {
			condense();
		}
	}

	private void checkLockAndUpdate() {
		if (lock.getStackInSlot(0).isEmpty()) {
			displayEmc = 0;
			requiredEmc = 0;
			this.isAcceptingEmc = false;
			return;
		}

		if (EMCHelper.doesItemHaveEmc(lock.getStackInSlot(0))) {
			long lockEmc = EMCHelper.getEmcValue(lock.getStackInSlot(0));

			if (requiredEmc != lockEmc) {
				requiredEmc = lockEmc;
				this.isAcceptingEmc = true;
			}
		} else {
			lock.setStackInSlot(0, ItemStack.EMPTY);

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
		ItemStack lockCopy = lock.getStackInSlot(0).copy();

		if (lockCopy.hasTag() && !ItemHelper.shouldDupeWithNBT(lockCopy)) {
			lockCopy.setTag(new CompoundNBT());
		}

		ItemHandlerHelper.insertItemStacked(outputInventory, lockCopy, false);
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
		if (lock.getStackInSlot(0).isEmpty()) {
			return false;
		}

		if (ItemHelper.shouldDupeWithNBT(lock.getStackInSlot(0))) {
			return ItemHelper.areItemStacksEqual(lock.getStackInSlot(0), stack);
		}

		return lock.getStackInSlot(0).getItem() == stack.getItem();
	}

	@Override
	public void read(@Nonnull CompoundNBT nbt) {
		super.read(nbt);
		inputInventory.deserializeNBT(nbt.getCompound("Input"));
		lock.deserializeNBT(nbt.getCompound("LockSlot"));
	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT nbt) {
		nbt = super.write(nbt);
		nbt.put("Input", inputInventory.serializeNBT());
		nbt.put("LockSlot", lock.serializeNBT());
		return nbt;
	}

	private void updateChest() {
		if (++ticksSinceSync % 20 * 4 == 0) {
			world.addBlockEvent(pos, getBlockState().getBlock(), 1, numPlayersUsing);
		}

		if (numPlayersUsing > 0 && lidAngle == 0.0F) {
			world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F) {
			prevLidAngle = lidAngle;
			float angleIncrement = 0.1F;
			if (numPlayersUsing > 0) {
				lidAngle += angleIncrement;
			} else {
				lidAngle -= angleIncrement;
			}

			if (lidAngle > 1.0F) {
				lidAngle = 1.0F;
			}

			if (lidAngle < 0.5F && prevLidAngle >= 0.5F) {
				world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (lidAngle < 0.0F) {
				lidAngle = 0.0F;
			}
		}
	}

	@Override
	public boolean receiveClientEvent(int number, int arg) {
		if (number == 1) {
			numPlayersUsing = arg;
			return true;
		}
		return super.receiveClientEvent(number, arg);
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