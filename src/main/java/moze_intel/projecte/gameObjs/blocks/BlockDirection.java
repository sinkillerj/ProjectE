package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

public abstract class BlockDirection extends Block {

	public static final Property<Direction> FACING = HorizontalDirectionalBlock.FACING;

	public BlockDirection(Properties props) {
		super(props);
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> props) {
		super.createBlockStateDefinition(props);
		props.add(FACING);
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
		if (oldState.getBlock() != newState.getBlock() && level instanceof Level world && !world.isClientSide()) {
			ResourceHandler<ItemResource> resourceHandler = world.getCapability(Capabilities.Item.BLOCK, pos, oldState, null, null);
			ResourceHandler<ItemResource> handler = resourceHandler == null ? null : /*ResourceHandler<ItemResource>.of*/(resourceHandler);
			WorldHelper.dropInventory(handler, world, pos);
		}
	}

	@Override
	@Deprecated
	public void attack(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player) {
		if (!level.isClientSide()) {
			ItemStack stack = player.getMainHandItem();
			if (!stack.isEmpty() && stack.is(PEItems.PHILOSOPHERS_STONE)) {
				level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(FACING, player.getDirection().getOpposite()));
			}
		}
	}

	@NotNull
	@Override
	@Deprecated
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@NotNull
	@Override
	@Deprecated
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}
}
