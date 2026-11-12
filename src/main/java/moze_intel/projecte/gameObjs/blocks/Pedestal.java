package moze_intel.projecte.gameObjs.blocks;

import java.util.List;
import java.util.function.Consumer;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.IMatterType;
import moze_intel.projecte.gameObjs.block_entities.DMPedestalBlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Pedestal extends Block implements SimpleWaterloggedBlock, PEEntityBlock<DMPedestalBlockEntity>, IMatterBlock {

	private static final VoxelShape SHAPE = Shapes.or(
			Block.box(3, 0, 3, 13, 2, 13),
			Shapes.or(
					Block.box(6, 2, 6, 10, 9, 10),
					Block.box(5, 9, 5, 11, 10, 11)
			)
	);

	public Pedestal(Properties props) {
		super(props);
		this.registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> props) {
		super.createBlockStateDefinition(props);
		props.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	@Deprecated
	public boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType pathComputationType) {
		return false;
	}

	@NotNull
	@Override
	@Deprecated
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
		return SHAPE;
	}

	/**
	 * @return True if there was an item and it got dropped, false otherwise.
	 */
	private boolean dropItem(Level level, BlockPos pos) {
		DMPedestalBlockEntity pedestal = WorldHelper.getBlockEntity(DMPedestalBlockEntity.class, level, pos);
		if (pedestal != null) {
			ItemStack stack = pedestal.getInventory().getStackInSlot(0);
			if (!stack.isEmpty()) {
				pedestal.getInventory().setStackInSlot(0, ItemStack.EMPTY);
				level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY() + 0.8, pos.getZ(), stack));
				return true;
			}
		}
		return false;
	}

	@Override
	public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
		if (oldState.getBlock() != newState.getBlock() && level instanceof Level world) {
			dropItem(world, pos);
		}
	}

	@Override
	@Deprecated
	public void attack(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player) {
		if (!level.isClientSide()) {
			dropItem(level, pos);
		}
	}

	@Override
	public boolean onDestroyedByPlayer(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, ItemStack tool, boolean willHarvest, @NotNull FluidState fluid) {
		if (player.isCreative() && dropItem(level, pos)) {
			level.sendBlockUpdated(pos, state, state, Block.UPDATE_IMMEDIATE);
			return false;
		}
		return super.onDestroyedByPlayer(state, level, pos, player, tool, willHarvest, fluid);
	}

	@NotNull
	@Override
	@Deprecated
	protected InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player,
			@NotNull InteractionHand hand, @NotNull BlockHitResult rtr) {
		if (!level.isClientSide()) {
			DMPedestalBlockEntity pedestal = WorldHelper.getBlockEntity(DMPedestalBlockEntity.class, level, pos, true);
			if (pedestal == null) {
				return InteractionResult.FAIL;
			}
			ItemStack item = pedestal.getInventory().getStackInSlot(0);
			if (stack.isEmpty() && !item.isEmpty()) {
				IPedestalItem pedestalItem = item.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY);
				if (pedestalItem != null) {
					pedestal.setActive(level, pos, !pedestal.getActive());
					level.sendBlockUpdated(pos, state, state, Block.UPDATE_IMMEDIATE);
				}
			} else if (!stack.isEmpty() && item.isEmpty()) {
				pedestal.getInventory().setStackInSlot(0, stack.split(1));
			}
		}
		return WorldHelper.sidedSuccess(level);
	}

	// [VanillaCopy] Adapted from NoteBlock
	@Deprecated
	public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block neighbor, @NotNull BlockPos neighborPos, boolean isMoving) {
		boolean hasSignal = level.hasNeighborSignal(pos);
		DMPedestalBlockEntity ped = WorldHelper.getBlockEntity(DMPedestalBlockEntity.class, level, pos);
		if (ped != null && ped.previousRedstoneState != hasSignal) {
			if (hasSignal) {
				ItemStack stack = ped.getInventory().getStackInSlot(0);
				//Note: Checking the capability is present will validate that the stack is not empty
				if (stack.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY) != null) {
					ped.setActive(level, pos, !ped.getActive());
					level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL_IMMEDIATE);
				}
			}
			ped.previousRedstoneState = hasSignal;
			ped.markDirty(level, pos, false);
		}
	}

	@Deprecated
	public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
		return true;
	}

	@Deprecated
	public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
		DMPedestalBlockEntity pedestal = WorldHelper.getBlockEntity(DMPedestalBlockEntity.class, level, pos);
		if (pedestal != null) {
			ItemStack stack = pedestal.getInventory().getStackInSlot(0);
			if (!stack.isEmpty()) {
				if (stack.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY) != null) {
					return pedestal.getActive() ? 15 : 10;
				}
				return 5;
			}
		}
		return 0;
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<DMPedestalBlockEntity> getType() {
		return PEBlockEntityTypes.DARK_MATTER_PEDESTAL;
	}

	@Override
	@Deprecated
	public boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);
		return triggerBlockEntityEvent(state, level, pos, id, param);
	}

	public void addTooltip(ItemStack stack, Consumer<Component> tooltip) {
		Component interact = Component.keybind("key.use");
		tooltip.accept(PELang.PEDESTAL_TOOLTIP1.translate(interact, Component.keybind("key.attack")));
		tooltip.accept(PELang.PEDESTAL_TOOLTIP2.translate(interact));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state == null ? null : state.setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@NotNull
	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Deprecated
	public BlockState updateShape(@NotNull BlockState state, @NotNull LevelReader level, @NotNull Direction facing, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED) && level instanceof LevelAccessor accessor) {
			accessor.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return state;
	}

	@Override
	public IMatterType getMatterType() {
		return EnumMatterType.DARK_MATTER;
	}
}
