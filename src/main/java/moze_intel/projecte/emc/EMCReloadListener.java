package moze_intel.projecte.emc;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.nss.AbstractNSSTag;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.network.PacketHandler;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

public class EMCReloadListener implements ISelectiveResourceReloadListener {

	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate) {
		long start = System.currentTimeMillis();

		//Clear the cached created tags
		AbstractNSSTag.clearCreatedTags();
		CustomEMCParser.init();

		try {
			EMCMappingHandler.map(resourceManager);
			PECore.LOGGER.info("Registered " + EMCMappingHandler.emc.size() + " EMC values. (took " + (System.currentTimeMillis() - start) + " ms)");
			PacketHandler.sendFragmentedEmcPacketToAll();
		} catch (Throwable t) {
			PECore.LOGGER.error("Error calculating EMC values", t);
		}
	}
}