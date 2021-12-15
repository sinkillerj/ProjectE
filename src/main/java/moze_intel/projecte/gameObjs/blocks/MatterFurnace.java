package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class MatterFurnace extends AbstractFurnaceBlock implements IMatterBlock {

	private final EnumMatterType matterType;

	public MatterFurnace(Properties props, EnumMatterType type) {
		super(props);
		this.matterType = type;
	}

	@Nonnull
	@Override
	public TileEntity newBlockEntity(@Nonnull IBlockReader world) {
		return matterType == EnumMatterType.RED_MATTER ? new RMFurnaceTile() : new DMFurnaceTile();
	}

	@Override
	protected void openContainer(World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player) {
		if (!world.isClientSide) {
			DMFurnaceTile te = WorldHelper.getTileEntity(DMFurnaceTile.class, world, pos, true);
			if (te != null) {
				NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
			}
		}
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tile = WorldHelper.getTileEntity(world, pos);
			if (tile != null) {
				tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> WorldHelper.dropInventory(inv, world, pos));
			}
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
		TileEntity te = WorldHelper.getTileEntity(world, pos);
		if (te != null) {
			return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(ItemHandlerHelper::calcRedstoneFromInventory).orElse(0);
		}
		return 0;
	}

	@Override
	public EnumMatterType getMatterType() {
		return matterType;
	}
}