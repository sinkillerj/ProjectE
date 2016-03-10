package team.chisel.api.blockpack;

import java.util.List;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Allows Block packs to be provided dynamically. For example if you want to load a block pack from a file
 */
public interface IBlockPackProvider {

    List<IProvidedBlockPack> getProvidedPacks(FMLPreInitializationEvent event);
}
