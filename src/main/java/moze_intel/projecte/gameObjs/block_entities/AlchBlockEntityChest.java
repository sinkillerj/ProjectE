package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.gameObjs.container.AlchChestContainer;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlchBlockEntityChest extends EmcChestBlockEntity {

	public static final ICapabilityProvider<AlchBlockEntityChest, @Nullable Direction, IItemHandler> INVENTORY_PROVIDER = (chest, side) -> chest.inventory;

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
		super(PEBlockEntityTypes.ALCHEMICAL_CHEST, pos, state, 1_000);
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.merge(inventory.serializeNBT());
	}

	public static void tickClient(Level level, BlockPos pos, BlockState state, AlchBlockEntityChest alchChest) {
		for (int i = 0; i < alchChest.inventory.getSlots(); i++) {
			ItemStack stack = alchChest.inventory.getStackInSlot(i);
			if (!stack.isEmpty()) {
				IAlchChestItem alchChestItem = stack.getCapability(PECapabilities.ALCH_CHEST_ITEM_CAPABILITY);
				if (alchChestItem != null) {
					alchChestItem.updateInAlchChest(level, pos, stack);
				}
			}
		}
		EmcChestBlockEntity.lidAnimateTick(level, pos, state, alchChest);
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, AlchBlockEntityChest alchChest) {
		for (int i = 0; i < alchChest.inventory.getSlots(); i++) {
			ItemStack stack = alchChest.inventory.getStackInSlot(i);
			if (!stack.isEmpty()) {
				IAlchChestItem alchChestItem = stack.getCapability(PECapabilities.ALCH_CHEST_ITEM_CAPABILITY);
				if (alchChestItem != null && alchChestItem.updateInAlchChest(level, pos, stack)) {
					alchChest.inventory.onContentsChanged(i);
				}
			}
		}
		if (alchChest.inventoryChanged) {
			//If the inventory changed, resync so that the client can tick things properly
			alchChest.inventoryChanged = false;
			level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
		}
		alchChest.updateComparators();
	}

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerIn) {
		return new AlchChestContainer(windowId, playerInventory, this);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return TextComponentUtil.build(PEBlocks.ALCHEMICAL_CHEST);
	}
}