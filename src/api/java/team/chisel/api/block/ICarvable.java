package team.chisel.api.block;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import team.chisel.api.render.IQuadMutator;
import team.chisel.client.BlockFaceData;

public interface ICarvable {

    /**
     * @return The index of this block, each index holds 16 variations.
     */
    int getIndex();

    /**
     * @return The total amount of variations for all indeces of this block.
     */
    int getTotalVariations();

    /**
     * @param variation
     *            The index of the variation (0-15).
     * @return The {@link VariationData} for the variation index.
     */
    VariationData getVariationData(int variation);

    /**
     * @return All variations for this block instance.
     */
    VariationData[] getVariations();

    /**
     * Gets the variation index from the world state.
     * 
     * @param state
     *            The current {@link IExtendedBlockState}.
     * @return The variation index.
     */
    int getVariationIndex(IExtendedBlockState state);

    /**
     * Called whenever resources are reloaded. Used to update render information.
     */
    @SideOnly(Side.CLIENT)
    void setBlockFaceData(BlockFaceData blockFaceData);

    @SideOnly(Side.CLIENT)
    BlockFaceData getBlockFaceData();

//    /**
//     * Called to get the quad mutator, called multiple times so please cache this value
//     */
//    @SideOnly(Side.CLIENT)
//    IQuadMutator getQuadMutator();
}
