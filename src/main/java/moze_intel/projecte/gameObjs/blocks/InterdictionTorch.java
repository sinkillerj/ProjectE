package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class InterdictionTorch extends BlockTorch implements ITileEntityProvider
{
	public InterdictionTorch(Properties props)
	{
		super(props);
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull IBlockReader world)
	{
		return new InterdictionTile();
	}
}
