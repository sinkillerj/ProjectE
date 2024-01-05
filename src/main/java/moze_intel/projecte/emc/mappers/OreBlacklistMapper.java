package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.ListBacked;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.Tags;

@EMCMapper
public class OreBlacklistMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		BuiltInRegistries.ITEM.getTag(Tags.Items.ORES)
				.stream()
				.flatMap(ListBacked::stream)
				.map(Holder::value)
				.map(NSSItem::createItem)
				.forEach(nssOre -> {
					mapper.setValueBefore(nssOre, 0L);
					mapper.setValueAfter(nssOre, 0L);
				});
	}

	@Override
	public String getName() {
		return "OresBlacklistMapper";
	}

	@Override
	public String getDescription() {
		return "Set EMC=0 for everything in the forge:ores tag";
	}
}