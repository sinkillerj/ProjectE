package moze_intel.projecte.emc;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.nss.AbstractNSSTag;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.network.PacketHandler;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

//Note: Has to be IResourceManagerReloadListener, so that it works properly on servers
public class EMCReloadListener implements ResourceManagerReloadListener {

	private final ServerResources dataPackRegistries;

	public EMCReloadListener(ServerResources dataPackRegistries) {
		this.dataPackRegistries = dataPackRegistries;
	}

	@Override
	public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
		long start = System.currentTimeMillis();

		//Clear the cached created tags
		AbstractNSSTag.clearCreatedTags();
		CustomEMCParser.init();

		try {
			EMCMappingHandler.map(dataPackRegistries, resourceManager);
			PECore.LOGGER.info("Registered " + EMCMappingHandler.getEmcMapSize() + " EMC values. (took " + (System.currentTimeMillis() - start) + " ms)");
			PacketHandler.sendFragmentedEmcPacketToAll();
		} catch (Throwable t) {
			PECore.LOGGER.error("Error calculating EMC values", t);
		}
	}
}