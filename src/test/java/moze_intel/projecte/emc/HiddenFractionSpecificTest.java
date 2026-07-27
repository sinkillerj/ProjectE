package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import java.util.List;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.arithmetic.FullBigFractionArithmetic;
import moze_intel.projecte.emc.arithmetic.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.generator.BigFractionToLongGenerator;
import moze_intel.projecte.utils.EMCHelper;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test hidden fractions")
class HiddenFractionSpecificTest {

	private BigFractionToLongGenerator<String> valueGenerator;
	private IExtendedMappingCollector<String, Long, IValueArithmetic<BigFraction>> mappingCollector;

	@BeforeEach
	void setup() {
		SimpleGraphMapper<String, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
		valueGenerator = new BigFractionToLongGenerator<>(mapper);
		mappingCollector = new LongToBigFractionCollector<>(mapper);
	}

	@Test
	@DisplayName("Test slab recipe EMC calculations")
	void slabRecipe() {
		mappingCollector.setValueBefore("s", 1L);
		mappingCollector.setValueBefore("redstone", 64L);
		mappingCollector.setValueBefore("glass", 1L);
		mappingCollector.addConversion(6, "slab", List.of("s", "s", "s"));
		mappingCollector.addConversion(1, "doubleslab", List.of("slab", "slab"));
		mappingCollector.addConversion(1, "transferpipe", List.of("slab", "slab", "slab", "glass", "redstone", "glass", "slab", "slab", "slab"));
		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("s"));
		Assertions.assertEquals(64, values.getLong("redstone"));
		Assertions.assertEquals(1, values.getLong("glass"));
		Assertions.assertEquals(0, values.getLong("slab"));
		Assertions.assertEquals(3 + 64 + 2, values.getLong("transferpipe"));
		Assertions.assertEquals(1, values.getLong("doubleslab"));
	}

	@Test
	@DisplayName("Test EMC nugget recipe exploits")
	void nuggetExploits() {
		mappingCollector.setValueBefore("ingot", 2048L);
		mappingCollector.setValueBefore("melon", 16L);
		mappingCollector.addConversion(9, "nugget", EMCHelper.intMapOf("ingot", 1));
		mappingCollector.addConversion(1, "goldmelon", List.of(
				"nugget", "nugget", "nugget",
				"nugget", "melon", "nugget",
				"nugget", "nugget", "nugget"
		));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(2048, values.getLong("ingot"));
		Assertions.assertEquals(16, values.getLong("melon"));
		Assertions.assertEquals(227, values.getLong("nugget"));
		Assertions.assertEquals(8 * 227 + 16, values.getLong("goldmelon"));
	}

	@Test
	@DisplayName("Test EMC calculation for molten enderpearls")
	void moltenEnderpearl() {
		mappingCollector.setValueBefore("enderpearl", 1024L);
		mappingCollector.setValueBefore("bucket", 768L);

		//Conversion using mili-milibuckets to make the 'emc per milibucket' smaller than 1
		mappingCollector.addConversion(250 * 1000, "moltenEnder", EMCHelper.intMapOf("enderpearl", 1));
		mappingCollector.addConversion(1, "moltenEnderBucket", EMCHelper.intMapOf("moltenEnder", 1000 * 1000, "bucket", 1));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1024, values.getLong("enderpearl"));
		Assertions.assertEquals(0, values.getLong("moltenEnder"));
		Assertions.assertEquals(768, values.getLong("bucket"));
		Assertions.assertEquals(4 * 1024 + 768, values.getLong("moltenEnderBucket"));
	}

	@Test
	@DisplayName("Test EMC calculation for molten enderpearls with conversion arithmetic")
	void moltenEnderpearlWithConversionArithmetic() {
		FullBigFractionArithmetic fullFractionArithmetic = new FullBigFractionArithmetic();
		mappingCollector.setValueBefore("enderpearl", 1024L);
		mappingCollector.setValueBefore("bucket", 768L);

		//Conversion using milibuckets with a "don't round anything down"-arithmetic
		mappingCollector.addConversion(250, "moltenEnder", EMCHelper.intMapOf("enderpearl", 1), fullFractionArithmetic);
		mappingCollector.addConversion(1, "moltenEnderBucket", EMCHelper.intMapOf("moltenEnder", 1000, "bucket", 1));

		//Without using the full fraction arithmetic
		mappingCollector.addConversion(250, "moltenEnder2", EMCHelper.intMapOf("enderpearl", 1));
		mappingCollector.addConversion(1, "moltenEnderBucket2", EMCHelper.intMapOf("moltenEnder2", 1000, "bucket", 1));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1024, values.getLong("enderpearl"));
		Assertions.assertEquals(768, values.getLong("bucket"));
		Assertions.assertEquals(4 * 1024 + 768, values.getLong("moltenEnderBucket"));
		Assertions.assertNotEquals(4 * 1024 + 767, values.getLong("moltenEnderBucket2"));
	}


	@Test
	@DisplayName("Test exact fractions can recover after exceeding long denominator range")
	void recoverFractionWithLargeDenominator() {
		FullBigFractionArithmetic fullFractionArithmetic = new FullBigFractionArithmetic();
		int scale = Integer.MAX_VALUE;
		mappingCollector.setValueBefore("base", 1L);
		mappingCollector.addConversion(scale, "subunit1", EMCHelper.intMapOf("base", 1), fullFractionArithmetic);
		mappingCollector.addConversion(scale, "subunit2", EMCHelper.intMapOf("subunit1", 1), fullFractionArithmetic);
		mappingCollector.addConversion(scale, "subunit3", EMCHelper.intMapOf("subunit2", 1), fullFractionArithmetic);
		mappingCollector.addConversion(1, "recovered1", EMCHelper.intMapOf("subunit3", scale));
		mappingCollector.addConversion(1, "recovered2", EMCHelper.intMapOf("recovered1", scale));
		mappingCollector.addConversion(1, "recovered3", EMCHelper.intMapOf("recovered2", scale));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(0, values.getLong("subunit1"));
		Assertions.assertEquals(0, values.getLong("subunit2"));
		Assertions.assertEquals(0, values.getLong("subunit3"));
		Assertions.assertEquals(1, values.getLong("recovered3"));
	}


	@Test
	@DisplayName("Test exact fraction arithmetic retains a finite denominator bound")
	void fullFractionArithmeticHasFiniteDenominatorBound() {
		FullBigFractionArithmetic arithmetic = new FullBigFractionArithmetic();
		BigFraction value = BigFraction.ONE;
		int divisions = 0;
		while (!arithmetic.isZero(value) && divisions < 1_000) {
			value = arithmetic.div(value, 2);
			divisions++;
		}

		Assertions.assertTrue(arithmetic.isZero(value), "Descending exact fractions must eventually stop instead of growing forever");
		Assertions.assertTrue(divisions > Long.SIZE, "The exact range should extend beyond the former long-denominator limit");
		Assertions.assertTrue(divisions < 1_000, "The finite denominator guard must converge predictably");
	}

	@Test
	@DisplayName("Test reliquary vial recipe EMC calculations")
	void reliquaryVials() {
		mappingCollector.setValueBefore("glass", 1L);

		mappingCollector.addConversion(16, "pane", EMCHelper.intMapOf("glass", 6));
		mappingCollector.addConversion(5, "vial", EMCHelper.intMapOf("pane", 5));
		//Internal EMC of pane and vial: 3/8 = 0.375
		//So 8 * vial should have an emc of 3 => testItem should have emc of 1
		mappingCollector.addConversion(3, "testItem1", EMCHelper.intMapOf("pane", 8));
		mappingCollector.addConversion(3, "testItem2", EMCHelper.intMapOf("vial", 8));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("glass"));
		Assertions.assertEquals(0, values.getLong("pane"));
		Assertions.assertEquals(0, values.getLong("vial"));
		Assertions.assertEquals(1, values.getLong("testItem1"));
		Assertions.assertEquals(1, values.getLong("testItem2"));
	}

	@Test
	@DisplayName("Test Propagation of values")
	void propagation() {
		mappingCollector.setValueBefore("a", 1L);

		mappingCollector.addConversion(2, "ahalf", EMCHelper.intMapOf("a", 1));
		mappingCollector.addConversion(1, "ahalf2", EMCHelper.intMapOf("ahalf", 1));
		mappingCollector.addConversion(1, "2ahalf2", EMCHelper.intMapOf("ahalf2", 2));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a"));
		Assertions.assertEquals(0, values.getLong("ahalf"));
		Assertions.assertEquals(0, values.getLong("ahalf2"));
		Assertions.assertEquals(1, values.getLong("2ahalf2"));
	}
}
