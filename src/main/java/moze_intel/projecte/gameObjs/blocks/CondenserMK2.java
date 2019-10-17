package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class CondenserMK2 extends Condenser
{
	public CondenserMK2(Properties props)
	{
		super(props);
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world)
	{
		return new CondenserMK2Tile();
	}
}
