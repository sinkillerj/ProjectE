package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;

@EMCMapper
public class RawOreBlacklistMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		for (Item rawOre : PETags.Items.RAW_ORES_LOOKUP.tag()) {
			NSSItem nssRawORe = NSSItem.createItem(rawOre);
			mapper.setValueBefore(nssRawORe, 0L);
			mapper.setValueAfter(nssRawORe, 0L);
		}
	}

	@Override
	public String getName() {
		return "RawOresBlacklistMapper";
	}

	@Override
	public String getDescription() {
		return "Set EMC=0 for everything in the forge:raw_materials tag";
	}
}