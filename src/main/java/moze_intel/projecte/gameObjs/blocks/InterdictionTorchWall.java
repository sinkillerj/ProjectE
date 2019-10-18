package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InterdictionTorchWall extends WallTorchBlock
{
    public InterdictionTorchWall(Properties props)
    {
        super(props);
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

    @Nonnull
    @Override
    public ResourceLocation getLootTable()
    {
        return ObjHandler.interdictionTorch.getLootTable();
    }
}
