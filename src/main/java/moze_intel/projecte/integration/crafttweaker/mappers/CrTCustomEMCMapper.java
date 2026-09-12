package moze_intel.projecte.integration.crafttweaker.mappers;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import java.util.Iterator;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

@EMCMapper(requiredMods = "crafttweaker")
public class CrTCustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	//CraftTweaker actions are ordered. Preserve that order so overlapping tag and item assignments have deterministic, script-defined precedence.
	private static final Object2LongMap<NormalizedSimpleStack> customEmcValues = new Object2LongLinkedOpenHashMap<>();

	public static void registerCustomEMC(@NotNull NormalizedSimpleStack stack, long emcValue) {
		//Linked maps keep a key's original position when replacing its value. Reinsert repeats so a later tag or item action
		//still runs after any overlapping registrations that appeared between the two script actions.
		customEmcValues.removeLong(stack);
		customEmcValues.put(stack, emcValue);
	}

	public static void unregisterNSS(@NotNull NormalizedSimpleStack stack) {
		customEmcValues.removeLong(stack);
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		for (Iterator<Object2LongMap.Entry<NormalizedSimpleStack>> iterator = Object2LongMaps.fastIterator(customEmcValues); iterator.hasNext(); ) {
			Object2LongMap.Entry<NormalizedSimpleStack> entry = iterator.next();
			NormalizedSimpleStack normStack = entry.getKey();
			long value = entry.getLongValue();
			//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
			normStack.forSelfAndEachElement(mapper, value, IMappingCollector::setValueBefore);
			PECore.debugLog("CraftTweaker setting value for {} to {}", normStack, value);
		}
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_CRT_EMC_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_CRT_EMC_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_CRT_EMC_MAPPER.tooltip();
	}
}
