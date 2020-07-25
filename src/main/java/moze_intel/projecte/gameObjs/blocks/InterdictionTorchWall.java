package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class InterdictionTorchWall extends WallTorchBlock {

	public InterdictionTorchWall(Properties props) {
		super(props, ParticleTypes.SOUL_FIRE_FLAME);//TODO - 1.16: Is this good or should we make our own particle
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
		return new InterdictionTile();
	}
}