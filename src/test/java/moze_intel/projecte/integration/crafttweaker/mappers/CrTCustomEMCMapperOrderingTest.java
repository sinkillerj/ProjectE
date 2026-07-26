package moze_intel.projecte.integration.crafttweaker.mappers;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.mapper.collector.NoOpMappingCollector;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test CraftTweaker custom EMC ordering")
class CrTCustomEMCMapperOrderingTest {

	private final List<NormalizedSimpleStack> registered = new ArrayList<>();

	@AfterEach
	void cleanup() {
		for (NormalizedSimpleStack stack : registered) {
			CrTCustomEMCMapper.unregisterNSS(stack);
		}
		registered.clear();
		NSSFake.resetNamespace();
	}

	@Test
	@DisplayName("Custom EMC values retain CraftTweaker registration order")
	void testRegistrationOrderIsStable() {
		List<NormalizedSimpleStack> expected = new ArrayList<>();
		for (int i = 0; i < 128; i++) {
			NormalizedSimpleStack stack = NSSFake.create("entry_" + i);
			register(stack, i + 1L);
			expected.add(stack);
		}

		RecordingCollector collector = new RecordingCollector();
		new CrTCustomEMCMapper().addMappings(collector, null, null, null);

		Assertions.assertEquals(expected, collector.order,
				"CraftTweaker fixed-value precedence must follow script registration order rather than hash-table iteration order");
	}

	@Test
	@DisplayName("A repeated registration receives the latest precedence")
	void testRepeatedRegistrationMovesToLatestPrecedence() {
		NormalizedSimpleStack first = NSSFake.create("first");
		NormalizedSimpleStack second = NSSFake.create("second");
		register(first, 10);
		register(second, 20);
		CrTCustomEMCMapper.registerCustomEMC(first, 30);

		RecordingCollector collector = new RecordingCollector();
		new CrTCustomEMCMapper().addMappings(collector, null, null, null);

		Assertions.assertEquals(List.of(second, first), collector.order,
				"The most recent script action must have the latest expansion precedence");
		Assertions.assertEquals(List.of(20L, 30L), collector.values);
	}

	private void register(NormalizedSimpleStack stack, long value) {
		registered.add(stack);
		CrTCustomEMCMapper.registerCustomEMC(stack, value);
	}

	private static class RecordingCollector extends NoOpMappingCollector<NormalizedSimpleStack, Long> {

		private final List<NormalizedSimpleStack> order = new ArrayList<>();
		private final List<Long> values = new ArrayList<>();

		@Override
		public void setValueBefore(NormalizedSimpleStack something, Long value) {
			order.add(something);
			values.add(value);
		}
	}
}
