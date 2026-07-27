package moze_intel.projecte.emc.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToLongFunction;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.IDataComponentProcessor;
import moze_intel.projecte.config.MappingConfig;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.utils.AnnotationHelper;
import net.minecraft.core.component.DataComponentPatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class DataComponentManager {

	private static final List<IDataComponentProcessor> processors = new ArrayList<>();
	private static final Set<Class<?>> failedProcessorClasses = ConcurrentHashMap.newKeySet();

	public static List<IDataComponentProcessor> loadProcessors() {
		if (processors.isEmpty()) {
			processors.addAll(AnnotationHelper.getDataComponentProcessors());
		}
		return Collections.unmodifiableList(processors);
	}

	//TODO: Do we want to eventually try and make this support ProjectEAPI.FREE_ARITHMETIC_VALUE
	public static void updateCachedValues(@Nullable ToLongFunction<ItemInfo> emcLookup) {
		//Allow one fresh diagnostic per processor after each remap/configuration refresh without adding work to successful lookups.
		failedProcessorClasses.clear();
		ComponentProcessorHelper.instance().updateCachedValues(emcLookup);
		for (IDataComponentProcessor processor : processors) {
			//Note: We only have to update enabled processors, as when a processor gets enabled it will update the cached values
			if (MappingConfig.isEnabled(processor)) {
				processor.updateCachedValues(emcLookup);
			}
		}
	}

	@NotNull
	static ItemInfo getPersistentInfo(@NotNull ItemInfo info) {
		if (!info.hasModifiedComponents() || info.getItem().is(PETags.Items.DATA_COMPONENT_WHITELIST) || EMCMappingHandler.hasEmcValue(info)) {
			//If we have no custom Data Components, we want to allow data components to be kept, or we have an exact match to a stored value just go with it
			return info;
		}
		//Cleans up the tag in item to reduce it as much as possible
		DataComponentPatch.Builder builder = DataComponentPatch.builder();
		for (IDataComponentProcessor processor : processors) {
			if (MappingConfig.isEnabled(processor) && processor.hasPersistentComponents() && MappingConfig.hasPersistent(processor)) {
				processor.collectPersistentComponents(info, builder);
			}
		}
		return ItemInfo.fromItem(info.getItem(), builder.build());
	}

	@Range(from = 0, to = Long.MAX_VALUE)
	public static long getEmcValue(@NotNull ItemInfo info) {
		//TODO: Account for extra components layered onto an exact component-bearing mapping (for example an enchanted potion),
		//without reprocessing the components that define the mapped variant itself.
		long emcValue = EMCMappingHandler.getStoredEmcValue(info);
		if (!info.hasModifiedComponents() || emcValue > 0) {
			//An exact component-bearing mapping is authoritative. Recipe mappers, custom conversions, and integrations may have already
			//included the component-specific cost in this value, so running the processors again would double count that state.
			return emcValue;
		}

		//Try the component-less item when there is no exact mapping for the complete component state.
		emcValue = EMCMappingHandler.getStoredEmcValue(info.itemOnly());
		if (emcValue == 0) {
			//The base item doesn't have an EMC value either so just exit
			return 0;
		}

		//Note: We continue to use our initial ItemInfo so that we are calculating based on the Data Components
		for (IDataComponentProcessor processor : processors) {
			try {
				if (MappingConfig.isEnabled(processor)) {
					emcValue = processor.recalculateEMC(info, emcValue);
					if (emcValue <= 0) {
						//Exit if it gets to zero (also safety check for less than zero in case a mod didn't bother sanctifying their data)
						return 0;
					}
				}
			} catch (ArithmeticException e) {
				//Exit with it not having an EMC value, as it most likely overflowed, and we don't want to allow wasting EMC
				return 0;
			} catch (RuntimeException e) {
				//A broken integration must not crash EMC consumers or leave a partially processed value. Log only once per processor class
				//between cache refreshes so a repeatedly queried malformed stack cannot flood the log or become a TPS problem.
				if (failedProcessorClasses.add(processor.getClass())) {
					PECore.LOGGER.error("Data Component Processor {} failed while calculating EMC. Treating the item as having no EMC.",
							processor.getClass().getName(), e);
				}
				return 0;
			}
		}
		return emcValue;
	}
}
