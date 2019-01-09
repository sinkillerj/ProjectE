package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import net.minecraft.block.BlockTorchWall;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class InterdictionTorchWall extends BlockTorchWall implements ITileEntityProvider
{
    public InterdictionTorchWall(Builder builder)
    {
        super(builder);
    }

    @Nonnull
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader world)
    {
        return new InterdictionTile();
    }
}
