package moze_intel.projecte.emc;

import moze_intel.projecte.PECore;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.network.PacketHandler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

import javax.annotation.Nonnull;

public class EMCReloadListener implements IResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager)
    {
        long start = System.currentTimeMillis();

        CustomEMCParser.init();

        try {
            EMCMapper.map(resourceManager);
            PECore.LOGGER.info("Registered " + EMCMapper.emc.size() + " EMC values. (took " + (System.currentTimeMillis() - start) + " ms)");
            PacketHandler.sendFragmentedEmcPacketToAll();
        } catch (Throwable t)
        {
            PECore.LOGGER.error("Error calculating EMC values", t);
        }
    }
}
