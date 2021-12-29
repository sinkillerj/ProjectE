package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

@EMCMapper
public class OreBlacklistMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private static final ResourceLocation ORES = new ResourceLocation("forge", "ores");

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ServerResources dataPackRegistries,
			ResourceManager resourceManager) {
		//Note: We need to get the tag by resource location, as named tags are not populated yet here
		Tag<Item> ores = dataPackRegistries.getTags().getOrEmpty(Registry.ITEM_REGISTRY).getTag(ORES);
		if (ores != null) {
			for (Item ore : ores.getValues()) {
				NSSItem nssOre = NSSItem.createItem(ore);
				mapper.setValueBefore(nssOre, 0L);
				mapper.setValueAfter(nssOre, 0L);
			}
		}
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