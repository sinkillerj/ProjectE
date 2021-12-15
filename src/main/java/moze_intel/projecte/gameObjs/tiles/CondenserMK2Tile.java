package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class CondenserMK2Tile extends CondenserTile {

	public CondenserMK2Tile() {
		super(PETileEntityTypes.CONDENSER_MK2.get());
	}

	@Nonnull
	@Override
	protected IItemHandler createAutomationInventory() {
		IItemHandlerModifiable automationInput = new WrappedItemHandler(getInput(), WrappedItemHandler.WriteMode.IN) {
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return SlotPredicates.HAS_EMC.test(stack) && !isStackEqualToLock(stack) ? super.insertItem(slot, stack, simulate) : stack;
			}
		};
		IItemHandlerModifiable automationOutput = new WrappedItemHandler(getOutput(), WrappedItemHandler.WriteMode.OUT);
		return new CombinedInvWrapper(automationInput, automationOutput);
	}

	@Override
	protected ItemStackHandler createInput() {
		return new StackHandler(42);
	}

	@Override
	protected ItemStackHandler createOutput() {
		return new StackHandler(42);
	}

	@Override
	protected void condense() {
		while (this.hasSpace() && this.getStoredEmc() >= requiredEmc) {
			pushStack();
			forceExtractEmc(requiredEmc, EmcAction.EXECUTE);
		}
		if (this.hasSpace()) {
			for (int i = 0; i < getInput().getSlots(); i++) {
				ItemStack stack = getInput().getStackInSlot(i);
				if (!stack.isEmpty()) {
					forceInsertEmc(EMCHelper.getEmcSellValue(stack) * stack.getCount(), EmcAction.EXECUTE);
					getInput().setStackInSlot(i, ItemStack.EMPTY);
					break;
				}
			}
		}
	}

	@Override
	public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
		super.load(state, nbt);
		getOutput().deserializeNBT(nbt.getCompound("Output"));
	}

	@Nonnull
	@Override
	public CompoundNBT save(@Nonnull CompoundNBT nbt) {
		nbt = super.save(nbt);
		nbt.put("Output", getOutput().serializeNBT());
		return nbt;
	}

	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory playerInv, @Nonnull PlayerEntity player) {
		return new CondenserMK2Container(windowId, playerInv, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return TextComponentUtil.build(PEBlocks.CONDENSER_MK2);
	}
}