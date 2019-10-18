package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InterdictionTorch extends TorchBlock
{
	public InterdictionTorch(Properties props)
	{
		super(props);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world)
	{
		return new InterdictionTile();
	}
}
