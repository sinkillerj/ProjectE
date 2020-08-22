package moze_intel.projecte.emc;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.nss.AbstractNSSTag;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.network.PacketHandler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

//Note: Has to be IResourceManagerReloadListener, so that it works properly on servers
public class EMCReloadListener implements IResourceManagerReloadListener {

	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
		long start = System.currentTimeMillis();

		//Clear the cached created tags
		AbstractNSSTag.clearCreatedTags();
		CustomEMCParser.init();

		try {
			EMCMappingHandler.map(resourceManager);
			PECore.LOGGER.info("Registered " + EMCMappingHandler.getEmcMapSize() + " EMC values. (took " + (System.currentTimeMillis() - start) + " ms)");
			//TODO - 1.16: FIXME, first call there is no server yet so we should no-op?
			PacketHandler.sendFragmentedEmcPacketToAll();
		} catch (Throwable t) {
			PECore.LOGGER.error("Error calculating EMC values", t);
		}
	}
}