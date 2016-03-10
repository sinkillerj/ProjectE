package team.chisel.api.render;

import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IRenderContextProvider {

    /**
     * Gets the block render context for this block
     *
     * @param world
     *            The world block access
     * @param pos
     *            The block position
     * @return The block render context
     */
    IBlockRenderContext getBlockRenderContext(IBlockAccess world, BlockPos pos);
}
