package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.Collections;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

@EMCMapper
public class WeatheredCopperMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ServerResources dataPackRegistries,
			ResourceManager resourceManager) {
		int recipeCount = 0;
		for (Map.Entry<Block, Block> entry : WeatheringCopper.NEXT_BY_BLOCK.get().entrySet()) {
			//Add conversions both directions due to scraping
			NSSItem unweathered = NSSItem.createItem(entry.getKey());
			NSSItem weathered = NSSItem.createItem(entry.getValue());
			mapper.addConversion(1, weathered, Collections.singleton(unweathered));
			mapper.addConversion(1, unweathered, Collections.singleton(weathered));
			recipeCount += 2;
		}
		PECore.debugLog("WeatheredCopperMapper Statistics:");
		PECore.debugLog("Found {} Weathered Copper Conversions", recipeCount);
	}

	@Override
	public String getName() {
		return "WeatheredCopperMapper";
	}

	@Override
	public String getDescription() {
		return "Add Conversions for all weathered copper variants";
	}
}