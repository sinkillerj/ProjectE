package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.block_entities.DMFurnaceBlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MatterFurnace extends AbstractFurnaceBlock implements IMatterBlock, PEEntityBlock<DMFurnaceBlockEntity> {

	private final EnumMatterType matterType;

	public MatterFurnace(Properties props, EnumMatterType type) {
		super(props);
		this.matterType = type;
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<? extends DMFurnaceBlockEntity> getType() {
		return matterType == EnumMatterType.RED_MATTER ? PEBlockEntityTypes.RED_MATTER_FURNACE : PEBlockEntityTypes.DARK_MATTER_FURNACE;
	}

	@Override
	protected void openContainer(Level level, @NotNull BlockPos pos, @NotNull Player player) {
		if (!level.isClientSide) {
			DMFurnaceBlockEntity furnace = WorldHelper.getBlockEntity(DMFurnaceBlockEntity.class, level, pos, true);
			if (furnace != null) {
				NetworkHooks.openGui((ServerPlayer) player, furnace, pos);
			}
		}
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity furnace = WorldHelper.getBlockEntity(level, pos);
			if (furnace != null) {
				furnace.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> WorldHelper.dropInventory(inv, level, pos));
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
		BlockEntity blockEntity = WorldHelper.getBlockEntity(level, pos);
		if (blockEntity != null) {
			return blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(ItemHandlerHelper::calcRedstoneFromInventory).orElse(0);
		}
		return 0;
	}

	@Override
	public EnumMatterType getMatterType() {
		return matterType;
	}
}