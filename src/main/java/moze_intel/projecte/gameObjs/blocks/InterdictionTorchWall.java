package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import net.minecraft.block.BlockTorchWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class InterdictionTorchWall extends BlockTorchWall
{
    public InterdictionTorchWall(Builder builder)
    {
        super(builder);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull IBlockState state, @Nonnull IBlockReader world)
    {
        return new InterdictionTile();
    }
}
