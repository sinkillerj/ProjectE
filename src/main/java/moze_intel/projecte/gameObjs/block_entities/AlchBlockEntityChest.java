package moze_intel.projecte.gameObjs.block_entities;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.gameObjs.container.AlchChestContainer;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AlchBlockEntityChest extends EmcChestBlockEntity implements MenuProvider {

	private final StackHandler inventory = new StackHandler(104) {
		@Override
		public void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			if (level != null && !level.isClientSide) {
				inventoryChanged = true;
			}
		}
	};
	private boolean inventoryChanged;

	public AlchBlockEntityChest(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.ALCHEMICAL_CHEST, pos, state);
		itemHandlerResolver = BasicCapabilityResolver.getBasicItemHandlerResolver(inventory);
	}

	@Override
	public void load(@Nonnull CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt);
	}

	@Override
	protected void saveAdditional(@Nonnull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.merge(inventory.serializeNBT());
	}

	public static void tickClient(Level level, BlockPos pos, BlockState state, AlchBlockEntityChest alchChest) {
		for (int i = 0; i < alchChest.inventory.getSlots(); i++) {
			ItemStack stack = alchChest.inventory.getStackInSlot(i);
			if (!stack.isEmpty()) {
				stack.getCapability(PECapabilities.ALCH_CHEST_ITEM_CAPABILITY).ifPresent(alchChestItem -> alchChestItem.updateInAlchChest(level, pos, stack));
			}
		}
		EmcChestBlockEntity.lidAnimateTick(level, pos, state, alchChest);
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, AlchBlockEntityChest alchChest) {
		for (int i = 0; i < alchChest.inventory.getSlots(); i++) {
			ItemStack stack = alchChest.inventory.getStackInSlot(i);
			if (!stack.isEmpty()) {
				int slotId = i;
				stack.getCapability(PECapabilities.ALCH_CHEST_ITEM_CAPABILITY).ifPresent(alchChestItem -> {
					if (alchChestItem.updateInAlchChest(level, pos, stack)) {
						alchChest.inventory.onContentsChanged(slotId);
					}
				});
			}
		}
		if (alchChest.inventoryChanged) {
			//If the inventory changed, resync so that the client can tick things properly
			alchChest.inventoryChanged = false;
			level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
		}
		alchChest.updateComparators();
	}

	@Nonnull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player playerIn) {
		return new AlchChestContainer(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return TextComponentUtil.build(PEBlocks.ALCHEMICAL_CHEST);
	}
}