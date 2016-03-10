package team.chisel.api.blockpack;

/**
 * A Block pack that is provided by a IBlockPackProvider. Needs more info then a regular IBlockPack
 */
public interface IProvidedBlockPack extends IBlockPack {

    /**
     * Get the name of this block pack
     * @return The Name of this block pack
     */
    String getName();
}
