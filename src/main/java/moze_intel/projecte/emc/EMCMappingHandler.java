package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.collector.IExtendedMappingCollector;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.MappingConfig;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.arithmetic.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.collector.DumpToFileCollector;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.components.DataComponentManager;
import moze_intel.projecte.emc.generator.BigFractionToLongGenerator;
import moze_intel.projecte.emc.mappers.TagMapper;
import moze_intel.projecte.emc.pregenerated.PregeneratedEMC;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import moze_intel.projecte.network.packets.to_client.SyncEmcPKT;
import moze_intel.projecte.utils.AnnotationHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.math3.fraction.BigFraction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public final class EMCMappingHandler {

	private static final List<IEMCMapper<NormalizedSimpleStack, Long>> mappers = new ArrayList<>();
	@Nullable
	private static Object2LongMap<ItemInfo> emc;
	private static int loadIndex = -1;

	public static void loadMappers() {
		//If we don't have any mappers loaded try to load them
		if (mappers.isEmpty()) {
			//Add all the EMC mappers we have encountered
			mappers.addAll(AnnotationHelper.getEMCMappers());
			//Manually register the Tag Mapper to ensure that it is registered last so that it can "fix" all the tags used in any of the other mappers
			// This also has the side effect to make sure that we can use EMC_MAPPERS.isEmpty to check if we have attempted to initialize our cache yet
			mappers.add(new TagMapper());
			//Set up the config for the Mappers and processors
			MappingConfig.setup(mappers, DataComponentManager.loadProcessors());
		}
	}

	public static void map(ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
		//Keep the current values available until a complete replacement is ready. updateEmcValues swaps the entire map,
		// so values removed by the remap still disappear without exposing a temporary empty map or losing the last good map if remapping fails.
		SimpleGraphMapper<NormalizedSimpleStack, BigFraction, IValueArithmetic<BigFraction>> mapper = MappingConfig.recoverMissingItemEmc()
				? new SimpleGraphMapper<>(new HiddenBigFractionArithmetic(), BigFraction.ONE, stack -> stack instanceof NSSItem item && !item.representsTag())
				: new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
		BigFractionToLongGenerator<NormalizedSimpleStack> valueGenerator = new BigFractionToLongGenerator<>(mapper);
		IExtendedMappingCollector<NormalizedSimpleStack, Long, IValueArithmetic<BigFraction>> mappingCollector = new LongToBigFractionCollector<>(mapper);

		if (MappingConfig.dumpToFile()) {
			mappingCollector = new DumpToFileCollector<>(ProjectEConfig.CONFIG_DIR.resolve("mapping_dump.json"), mappingCollector);
		}

		boolean usePregenerated = MappingConfig.usePregenerated();
		Path pregeneratedEmcFile = ProjectEConfig.CONFIG_DIR.resolve("pregenerated_emc.json");
		Optional<Object2LongMap<ItemInfo>> readPregeneratedValues = PregeneratedEMC.read(registryAccess, pregeneratedEmcFile, usePregenerated);
		if (readPregeneratedValues.isPresent()) {
			int values = updateEmcValues(readPregeneratedValues.get());
			PECore.debugLog("Loaded {} values from pregenerated EMC File", values);
		} else {
			SimpleGraphMapper.setLogFoundExploits(MappingConfig.logExploits());

			PECore.debugLog("Starting to collect Mappings...");
			collectMappings(mappers, MappingConfig::isEnabled, mappingCollector, serverResources, registryAccess, resourceManager);

			PECore.debugLog("Mapping Collection finished");
			finishCollection(mappingCollector, registryAccess);

			PECore.debugLog("Starting to generate Values:");
			Object2LongMap<NormalizedSimpleStack> graphMapperValues = valueGenerator.generateValues();
			PECore.debugLog("Generated Values...");

			updateEmcValues(filterEMCMap(graphMapperValues));
			PECore.debugLog("Filtered Values...");

			if (usePregenerated && emc != null) {//Note: It should never be null here as we just set it
				//Should have used pregenerated, but the file was not read => regenerate.
				PregeneratedEMC.write(registryAccess, pregeneratedEmcFile, emc);
				PECore.debugLog("Wrote Pregen-file!");
			}
		}

		fireEmcRemapEvent();
	}

	static void collectMappings(Iterable<IEMCMapper<NormalizedSimpleStack, Long>> mapperList,
			Predicate<IEMCMapper<NormalizedSimpleStack, Long>> isEnabled, IMappingCollector<NormalizedSimpleStack, Long> mappingCollector,
			ReloadableServerResources serverResources, RegistryAccess registryAccess, ResourceManager resourceManager) {
		for (IEMCMapper<NormalizedSimpleStack, Long> emcMapper : mapperList) {
			if (!isEnabled.test(emcMapper)) {
				continue;
			}
			DumpToFileCollector.currentGroupName = emcMapper.getName();
			try {
				emcMapper.addMappings(mappingCollector, serverResources, registryAccess, resourceManager);
				PECore.debugLog("Collected Mappings from {}", emcMapper.getClass().getName());
			} catch (Exception e) {
				throw new IllegalStateException("Failed to collect EMC mappings from " + emcMapper.getClass().getName()
						+ "; remap aborted before publishing incomplete values", e);
			} finally {
				//Mappers should clean up their own temporary state, but enforce the boundary here so a broken third-party mapper cannot leak it.
				NSSFake.resetNamespace();
				DumpToFileCollector.currentGroupName = "default";
			}
		}
	}

	static void finishCollection(IMappingCollector<NormalizedSimpleStack, Long> mappingCollector, RegistryAccess registryAccess) {
		DumpToFileCollector.currentGroupName = "NSSHelper";
		try {
			mappingCollector.finishCollection(registryAccess);
		} finally {
			//finishCollection may create helper NSSFake entries. Do not let a failed collector leak temporary mapper or diagnostic state.
			NSSFake.resetNamespace();
			DumpToFileCollector.currentGroupName = "default";
		}
	}

	private static void fireEmcRemapEvent() {
		//Start by doing our implementations
		FuelMapper.loadMap();
		loadIndex++;
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
				if (knowledge != null) {
					if (knowledge instanceof KnowledgeImpl impl && impl.pruneStaleKnowledge()) {
						knowledge.sync(player);
					} else if (player.containerMenu instanceof TransmutationContainer) {
						//If knowledge didn't get trimmed due to pruning, tell clients that have the transmutation gui open
						// that they should update targets anyway, as it is possible EMC values changed and the order things
						// are drawn needs to be changed
						PECore.packetHandler().updateTransmutationTargets(player);
					}
				}
			}
		}
		NeoForge.EVENT_BUS.post(new EMCRemapEvent());
	}

	public static int getLoadIndex() {
		return loadIndex;
	}

	private static Object2LongMap<ItemInfo> filterEMCMap(Object2LongMap<NormalizedSimpleStack> map) {
		Object2LongMap<ItemInfo> resultMap = new Object2LongOpenHashMap<>(map.size());
		for (Iterator<Object2LongMap.Entry<NormalizedSimpleStack>> iterator = Object2LongMaps.fastIterator(map); iterator.hasNext(); ) {
			Object2LongMap.Entry<NormalizedSimpleStack> entry = iterator.next();
			if (entry.getKey() instanceof NSSItem nssItem) {
				//Note: We don't need to check if the value is greater than zero, as our generated values filter out any non positive values
				ItemInfo info = ItemInfo.fromNSS(nssItem);
				if (info != null) {//Ensure the item actually exists and is not a tag
					resultMap.put(info, entry.getLongValue());
				}
			}
		}
		return resultMap;
	}

	public static int getEmcMapSize() {
		return emc == null ? 0 : emc.size();
	}

	public static boolean hasEmcValue(@NotNull ItemInfo info) {
		return emc != null && emc.containsKey(info);
	}

	/**
	 * Gets the stored emc value or zero if there is no entry in the map for the given value.
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	public static long getStoredEmcValue(@NotNull ItemInfo info) {
		return emc == null ? 0 : emc.getLong(info);
	}

	public static void clearEmcMap() {
		replaceEmcValues(null, DataComponentManager::updateCachedValues);
	}

	/**
	 * Returns a modifiable set of all the mapped {@link ItemInfo}
	 */
	public static Set<ItemInfo> getMappedItems() {
		if (emc == null) {
			return new HashSet<>();
		}
		return new HashSet<>(emc.keySet());
	}

	@ApiStatus.Internal
	public static int updateEmcValues(Object2LongMap<ItemInfo> data) {
		replaceEmcValues(data, DataComponentManager::updateCachedValues);
		return data.size();
	}

	static void replaceEmcValues(@Nullable Object2LongMap<ItemInfo> data, CachedValueUpdater cacheUpdater) {
		Object2LongMap<ItemInfo> previous = emc;
		ToLongFunction<ItemInfo> lookup = data == null ? null : data::getLong;
		try {
			//Prepare dependent caches first. The server thread cannot observe the brief preparation window, and the live map is only swapped after success.
			cacheUpdater.update(lookup);
		} catch (RuntimeException | Error failure) {
			try {
				cacheUpdater.update(previous == null ? null : previous::getLong);
			} catch (RuntimeException | Error rollbackFailure) {
				failure.addSuppressed(rollbackFailure);
			}
			throw failure;
		}
		emc = data;
	}

	@FunctionalInterface
	interface CachedValueUpdater {

		void update(@Nullable ToLongFunction<ItemInfo> emcLookup);
	}

	public static SyncEmcPKT createPacketData() {
		return new SyncEmcPKT(emc == null ? Object2LongMaps.emptyMap() : Object2LongMaps.unmodifiable(emc));
	}
}
