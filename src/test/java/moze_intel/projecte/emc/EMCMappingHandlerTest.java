package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.collector.NoOpMappingCollector;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.DumpToFileCollector;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test EMC mapping failure isolation")
class EMCMappingHandlerTest {

	@AfterEach
	void cleanup() {
		EMCMappingHandler.clearEmcMap();
		NSSFake.resetNamespace();
		DumpToFileCollector.currentGroupName = "default";
	}

	@Test
	@DisplayName("A mapper failure aborts collection and clears temporary mapper state")
	void testMapperFailureAbortsCollectionAndClearsTemporaryState() {
		AtomicBoolean laterMapperRan = new AtomicBoolean();
		IEMCMapper<NormalizedSimpleStack, Long> failingMapper = new TestMapper("Failing Mapper", mapper -> {
			NSSFake.setCurrentNamespace("leaked");
			mapper.setValueBefore(NSSFake.create("partial"), 1L);
			throw new IllegalArgumentException("boom");
		});
		IEMCMapper<NormalizedSimpleStack, Long> laterMapper = new TestMapper("Later Mapper", mapper -> laterMapperRan.set(true));

		IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () ->
				EMCMappingHandler.collectMappings(List.of(failingMapper, laterMapper), mapper -> true, new NoOpMappingCollector<>(), null, null, null));

		Assertions.assertInstanceOf(IllegalArgumentException.class, exception.getCause());
		Assertions.assertFalse(laterMapperRan.get(), "Collection must stop before later mappers can contribute to an invalid remap");
		Assertions.assertEquals("default", DumpToFileCollector.currentGroupName);
		Assertions.assertEquals("NSSFake: /after_failure", NSSFake.create("after_failure").toString());
	}

	@Test
	@DisplayName("Finishing collection clears temporary mapper state after success")
	void testFinishCollectionSuccessClearsTemporaryState() {
		NoOpMappingCollector<NormalizedSimpleStack, Long> collector = new NoOpMappingCollector<>() {
			@Override
			public void finishCollection(HolderLookup.Provider registries) {
				NSSFake.setCurrentNamespace("leaked_finish");
			}
		};

		EMCMappingHandler.finishCollection(collector, null);
		Assertions.assertEquals("default", DumpToFileCollector.currentGroupName);
		Assertions.assertEquals("NSSFake: /after_finish", NSSFake.create("after_finish").toString());
	}

	@Test
	@DisplayName("Finishing collection clears temporary mapper state on failure")
	void testFinishCollectionFailureClearsTemporaryState() {
		NoOpMappingCollector<NormalizedSimpleStack, Long> collector = new NoOpMappingCollector<>() {
			@Override
			public void finishCollection(HolderLookup.Provider registries) {
				NSSFake.setCurrentNamespace("leaked_finish");
				throw new IllegalStateException("finish failed");
			}
		};

		Assertions.assertThrows(IllegalStateException.class, () -> EMCMappingHandler.finishCollection(collector, null));
		Assertions.assertEquals("default", DumpToFileCollector.currentGroupName);
		Assertions.assertEquals("NSSFake: /after_finish_failure", NSSFake.create("after_finish_failure").toString());
	}

	@Test
	@DisplayName("A cache preparation failure retains the previously published EMC map")
	void testCachePreparationFailureRetainsPreviousMap() {
		ItemInfo dirt = ItemInfo.fromItem(Items.DIRT);
		Object2LongMap<ItemInfo> previous = new Object2LongOpenHashMap<>();
		previous.put(dirt, 1L);
		EMCMappingHandler.replaceEmcValues(previous, lookup -> {
		});

		Object2LongMap<ItemInfo> replacement = new Object2LongOpenHashMap<>();
		replacement.put(dirt, 2L);
		List<Long> observedValues = new ArrayList<>();

		IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class, () ->
				EMCMappingHandler.replaceEmcValues(replacement, lookup -> {
					observedValues.add(lookup == null ? 0L : lookup.applyAsLong(dirt));
					if (observedValues.size() == 1) {
						throw new IllegalStateException("cache update failed");
					}
				}));

		Assertions.assertEquals("cache update failed", exception.getMessage());
		Assertions.assertEquals(List.of(2L, 1L), observedValues, "The failed candidate must be followed by a rollback to the previous lookup");
		Assertions.assertEquals(1L, EMCMappingHandler.getStoredEmcValue(dirt));
	}

	private record TestMapper(String name, Consumer<IMappingCollector<NormalizedSimpleStack, Long>> action)
			implements IEMCMapper<NormalizedSimpleStack, Long> {

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getTranslationKey() {
			return "test." + name;
		}

		@Override
		public String getDescription() {
			return name;
		}

		@Override
		public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, net.minecraft.server.ReloadableServerResources serverResources,
				net.minecraft.core.RegistryAccess registryAccess, net.minecraft.server.packs.resources.ResourceManager resourceManager) {
			action.accept(mapper);
		}
	}

}
