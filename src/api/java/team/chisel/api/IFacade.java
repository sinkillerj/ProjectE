package team.chisel.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * To be implemented on blocks that "hide" another block inside, so connected textures can still be
 * accomplished.
 */
public interface IFacade {
    /**
     * Gets the block state this facade is acting as.
     *
     * @param world {@link World}
     * @param pos   The Blocks position
     * @param side  The side being rendered, NOT the side being connected from.
     *              <p/>
     *              This value can be -1 if no side is specified. Please handle this appropriately.
     * @return The block inside of your facade block.
     */
    IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side);

}