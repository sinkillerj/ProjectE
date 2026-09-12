package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import java.util.List;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.arithmetic.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.generator.BigFractionToLongGenerator;
import moze_intel.projecte.utils.EMCHelper;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Timeout(10)
class ModpackRecoveryTest {

	private BigFractionToLongGenerator<String> generator;
	private IExtendedMappingCollector<String, Long, IValueArithmetic<BigFraction>> collector;

	@BeforeEach
	void setup() {
		SimpleGraphMapper<String, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(
				new HiddenBigFractionArithmetic(), BigFraction.ONE, key -> key.startsWith("item:"));
		generator = new BigFractionToLongGenerator<>(mapper);
		collector = new LongToBigFractionCollector<>(mapper);
	}

	@Test
	void subUnitMachineAlternativeDoesNotEraseNuggetOrBucket() {
		collector.setValueBefore("item:iron_ingot", 256L);
		collector.addConversion(100_000, "fake:machine", List.of("item:iron_ingot"));
		collector.addConversion(1, "item:iron_nugget", List.of("fake:machine"));
		collector.addConversion(9, "item:iron_nugget", List.of("item:iron_ingot"));
		collector.addConversion(1, "item:bucket", EMCHelper.intMapOf("item:iron_ingot", 3));
		Object2LongMap<String> values = generator.generateValues();
		assertEquals(28, values.getLong("item:iron_nugget"));
		assertEquals(768, values.getLong("item:bucket"));
	}

	@Test
	void deferredMinimumPropagatesToDownstreamRecipes() {
		collector.setValueBefore("item:stone", 1L);
		collector.addConversion(2, "item:slab", List.of("item:stone"));
		collector.addConversion(1, "item:downstream", EMCHelper.intMapOf("item:slab", 3));
		Object2LongMap<String> values = generator.generateValues();
		assertEquals(1, values.getLong("item:slab"));
		assertEquals(3, values.getLong("item:downstream"));
	}

	@Test
	void lowCovalenceDustUsesDeferredMinimum() {
		collector.setValueBefore("item:charcoal", 8L);
		collector.setValueBefore("item:cobblestone", 1L);
		collector.addConversion(40, "item:low_covalence_dust", EMCHelper.intMapOf("item:charcoal", 1, "item:cobblestone", 8));
		assertEquals(1, generator.generateValues().getLong("item:low_covalence_dust"));
	}

	@Test
	void normalRecipeWinsOverEarlySubUnitAlternative() {
		collector.setValueBefore("item:source", 128L);
		collector.addConversion(1_000, "item:result", List.of("item:source"));
		collector.addConversion(1, "fake:step1", List.of("item:source"));
		collector.addConversion(1, "fake:step2", List.of("fake:step1"));
		collector.addConversion(1, "item:result", List.of("fake:step2"));
		assertEquals(128, generator.generateValues().getLong("item:result"));
	}

	@Test
	void fractionalFluidStillContributesItsExactCost() {
		collector.setValueBefore("item:source", 1L);
		collector.addConversion(2, "fluid:test", List.of("item:source"));
		collector.addConversion(1, "item:result", EMCHelper.intMapOf("fluid:test", 1_000));
		assertEquals(500, generator.generateValues().getLong("item:result"));
	}

	@Test
	void selfCycleRetainsAcyclicCostOnlyInRecoveryMode() {
		collector.setValueBefore("item:source", 256L);
		collector.addConversion(1, "item:bucket", EMCHelper.intMapOf("item:source", 3));
		collector.addConversion(2, "item:bucket", List.of("item:bucket"));
		assertEquals(768, generator.generateValues().getLong("item:bucket"));
	}

	@Test
	void cycleWithoutValuedSourceRemainsMissing() {
		collector.addConversion(2, "item:a", List.of("item:b"));
		collector.addConversion(2, "item:b", List.of("item:a"));
		assertFalse(generator.generateValues().containsKey("item:a"));
	}

	@Test
	void freeItemIsNotResurrectedByPaidAlternative() {
		collector.setValueBefore("item:paid", 64L);
		collector.setValueBefore("fluid:free", ProjectEAPI.FREE_ARITHMETIC_VALUE);
		collector.addConversion(1, "item:result", List.of("fluid:free"));
		collector.addConversion(1, "item:result", List.of("item:paid"));
		assertFalse(generator.generateValues().containsKey("item:result"));
	}

	@Test
	void explicitZeroAndCustomValuesStayAuthoritative() {
		collector.setValueBefore("item:source", 64L);
		collector.setValueBefore("item:disabled", 0L);
		collector.setValueBefore("item:custom", 100L);
		collector.setValueAfter("item:after", 0L);
		for (String item : List.of("item:disabled", "item:custom", "item:after")) {
			collector.addConversion(1, item, List.of("item:source"));
		}
		Object2LongMap<String> values = generator.generateValues();
		assertFalse(values.containsKey("item:disabled"));
		assertFalse(values.containsKey("item:after"));
		assertEquals(100, values.getLong("item:custom"));
	}

	@Test
	void forcedRecipeCannotFallBackToOrdinaryRecipe() {
		collector.setValueBefore("item:source", 64L);
		collector.addConversion(1, "item:result", List.of("item:source"));
		collector.setValueFromConversion(1, "item:result", List.of("item:missing"));
		assertFalse(generator.generateValues().containsKey("item:result"));
	}

	@Test
	void missingIngredientIsNotTreatedAsFree() {
		collector.setValueBefore("item:source", 64L);
		collector.addConversion(1, "item:result", List.of("item:source", "item:missing"));
		assertFalse(generator.generateValues().containsKey("item:result"));
	}

	@Test
	void syntheticGroupDoesNotGetMinimumItemValue() {
		collector.setValueBefore("item:source", 1L);
		collector.addConversion(1_000, "fake:group", List.of("item:source"));
		collector.addConversion(1, "item:result", EMCHelper.intMapOf("fake:group", 2_000));
		assertEquals(2, generator.generateValues().getLong("item:result"));
	}

	@Test
	void multiNodeCycleTerminatesAndRetainsValuedEntry() {
		collector.setValueBefore("item:source", 256L);
		collector.addConversion(1, "item:a", List.of("item:source"));
		collector.addConversion(2, "item:b", List.of("item:a"));
		collector.addConversion(2, "item:a", List.of("item:b"));
		Object2LongMap<String> values = generator.generateValues();
		assertEquals(256, values.getLong("item:a"));
		assertEquals(128, values.getLong("item:b"));
	}

	@Test
	void forcedOnlyConversionIsMapped() {
		collector.setValueBefore("item:source", 64L);
		collector.setValueFromConversion(1, "item:forced", List.of("item:source"));
		assertEquals(64, generator.generateValues().getLong("item:forced"));
	}
}
