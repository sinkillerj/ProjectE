package moze_intel.projecte.emc.mappers;

import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

@EMCMapper
public class WaxableMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		Registry<Block> blocks = registryAccess.lookupOrThrow(Registries.BLOCK);
		NSSItem wax = NSSItem.createItem(Items.HONEYCOMB);
		int recipeCount = 0;
		for (Map.Entry<ResourceKey<Block>, Waxable> entry : blocks.getDataMap(NeoForgeDataMaps.WAXABLES).entrySet()) {
			//Add conversions both directions due to scraping
			Block block = blocks.getValue(entry.getKey());
			if (block != null) {
				NSSItem base = NSSItem.createItem(block);
				NSSItem waxed = NSSItem.createItem(entry.getValue().waxed());
				mapper.addConversion(1, waxed, EMCHelper.intMapOf(
						base, 1,
						wax, 1
				));
				//Scraping the block does not return the wax
				mapper.addConversion(1, base, EMCHelper.intMapOf(waxed, 1));
				recipeCount += 2;
			}
		}
		PECore.debugLog("{} Statistics:", getName());
		PECore.debugLog("Found {} Waxable Conversions", recipeCount);
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_WAXABLE_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_WAXABLE_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_WAXABLE_MAPPER.tooltip();
	}
}
