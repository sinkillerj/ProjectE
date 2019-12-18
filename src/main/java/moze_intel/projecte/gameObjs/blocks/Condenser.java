package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class Condenser extends AlchemicalChest {

	public Condenser(Properties props) {
		super(props);
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
		return new CondenserTile();
	}

	@Nonnull
	@Override
	@Deprecated
	public ActionResultType func_225533_a_(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rtr) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof CondenserTile) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (CondenserTile) te, pos);
			}
		}
		return ActionResultType.SUCCESS;
	}
}