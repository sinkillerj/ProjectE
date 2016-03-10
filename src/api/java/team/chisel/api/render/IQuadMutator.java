package team.chisel.api.render;

import net.minecraft.client.renderer.block.model.BakedQuad;

/**
 * Allow mutation of quads
 */
public interface IQuadMutator extends IRenderContextProvider {

    /**
     * Mutate the quad
     * @param quadIn The Original Quad
     * @param variation The Variation of the block
     * @param context The Context needed for mutation
     * @return The New Quad
     */
    BakedQuad mutate(BakedQuad quadIn, int variation, IBlockRenderContext context);

}
