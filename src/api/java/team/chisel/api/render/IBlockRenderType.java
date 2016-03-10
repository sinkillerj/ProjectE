package team.chisel.api.render;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;

/**
 * Interface for chisel block render types MUST HAVE A NO ARGS CONSTRUCTOR
 */
public interface IBlockRenderType extends IRenderContextProvider{

    /**
     * Make a Chisel Texture from a list of sprites.
     * <p>
     * Tip: You can explicitly type the return of this method without any warnings or errors. For instance <blockquote>
     * <code>public IChiselTexture{@literal <}MyRenderType{@literal >} makeTexture(...) {...}</code> </blockquote> Is a valid override of this method.
     * 
     * @param layer
     *            The texture layer
     * @param sprites
     *            The Sprites
     * @return A Chisel Texture TODO This should probably take a bean, so that adding extra stuff later doesn't break API
     */
    <T extends IBlockRenderType> IChiselTexture<? extends T> makeTexture(EnumWorldBlockLayer layer, TextureSpriteCallback... sprites);


    /**
     * Gets the amount of quads per side
     * 
     * @return The Amount of quads per side
     */
    default int getQuadsPerSide() {
        return 1;
    }

    /**
     * The amount of textures required for this render type. For instance CTM requires two.
     * 
     * @return The amount of textures required.
     */
    default int requiredTextures() {
        return 1;
    }
}
