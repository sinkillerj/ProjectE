package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.container.CondenserMK2Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import moze_intel.projecte.utils.PECombinedItemStacks;
import org.jetbrains.annotations.NotNull;

public class CondenserMK2BlockEntity extends CondenserBlockEntity {

	public CondenserMK2BlockEntity(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.CONDENSER_MK2, pos, state);
	}

	@NotNull
	@Override
	protected ResourceHandler<ItemResource> createAutomationInventory() {
		ResourceHandler<ItemResource> input = new WrappedItemHandler(getInput(), WrappedItemHandler.WriteMode.IN) {
			@Override
			public int insert(int slot, ItemResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
				ItemStack stack = resource.toStack();
				return SlotPredicates.HAS_EMC.test(stack) && !isStackEqualToLock(stack) ? super.insert(slot, resource, amount, transaction) : 0;
			}
		};
		return new PECombinedItemStacks(input, new WrappedItemHandler(getOutput(), WrappedItemHandler.WriteMode.OUT));
	}

	@Override
	protected PEItemStacksHandler createInput() {
		return new StackHandler(42);
	}

	@Override
	protected PEItemStacksHandler createOutput() {
		return new StackHandler(42);
	}

	@Override
	protected void condense() {
		while (this.hasSpace() && this.getStoredEmc() >= requiredEmc) {
			pushStack();
			forceExtractEmc(requiredEmc, EmcAction.EXECUTE);
		}
		if (this.hasSpace()) {
			for (int i = 0, slots = getInput().getSlots(); i < slots; i++) {
				ItemStack stack = getInput().getStackInSlot(i);
				if (!stack.isEmpty()) {
					forceInsertEmc(IEMCProxy.INSTANCE.getSellValue(stack) * stack.getCount(), EmcAction.EXECUTE);
					getInput().setStackInSlot(i, ItemStack.EMPTY);
					break;
				}
			}
		}
	}

	@Override
	public void loadAdditional(@NotNull ValueInput input) {
		super.loadAdditional(input);
		getOutput().deserialize(input.childOrEmpty("output"));
	}

	@Override
	protected void saveAdditional(@NotNull ValueOutput output) {
		super.saveAdditional(output);
		getOutput().serialize(output.child("output"));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInv, @NotNull Player player) {
		return new CondenserMK2Container(windowId, playerInv, this);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return TextComponentUtil.build(PEBlocks.CONDENSER_MK2);
	}
}
