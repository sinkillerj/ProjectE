package moze_intel.projecte.emc;

import com.google.common.collect.ImmutableMap;
import moze_intel.projecte.emc.arithmetics.FullBigFractionArithmetic;
import moze_intel.projecte.emc.arithmetics.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import moze_intel.projecte.emc.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.generators.BigFractionToLongGenerator;
import moze_intel.projecte.emc.generators.IValueGenerator;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@DisplayName("Test hidden fractions")
class HiddenFractionSpecificTest
{
	private IValueGenerator<String, Long> valueGenerator;
	private IExtendedMappingCollector<String, Long, IValueArithmetic<BigFraction>> mappingCollector;

	@BeforeEach
	void setup()
	{
		SimpleGraphMapper<String, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
		valueGenerator = new BigFractionToLongGenerator<>(mapper);
		mappingCollector = new LongToBigFractionCollector<>(mapper);
	}

	@Test
	@DisplayName("Test slab recipe EMC calculations")
	void slabRecipe()
	{
		mappingCollector.setValueBefore("s", 1L);
		mappingCollector.setValueBefore("redstone", 64L);
		mappingCollector.setValueBefore("glass", 1L);
		mappingCollector.addConversion(6, "slab", Arrays.asList("s", "s", "s"));
		mappingCollector.addConversion(1, "doubleslab", Arrays.asList("slab", "slab"));
		mappingCollector.addConversion(1, "transferpipe", Arrays.asList("slab", "slab", "slab", "glass", "redstone", "glass", "slab", "slab", "slab"));
		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "s"));
		Assertions.assertEquals(64, getValue(values, "redstone"));
		Assertions.assertEquals(1, getValue(values, "glass"));
		Assertions.assertEquals(0, getValue(values, "slab"));
		Assertions.assertEquals(3 + 64 + 2, getValue(values, "transferpipe"));
		Assertions.assertEquals(1, getValue(values, "doubleslab"));
	}

	@Test
	@DisplayName("Test EMC nugget recipe exploits")
	void nuggetExploits()
	{
		mappingCollector.setValueBefore("ingot", 2048L);
		mappingCollector.setValueBefore("melon", 16L);
		mappingCollector.addConversion(9, "nugget", Collections.singletonList("ingot"));
		mappingCollector.addConversion(1, "goldmelon", Arrays.asList(
				"nugget", "nugget", "nugget",
				"nugget", "melon", "nugget",
				"nugget", "nugget", "nugget"
		));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(2048, getValue(values, "ingot"));
		Assertions.assertEquals(16, getValue(values, "melon"));
		Assertions.assertEquals(227, getValue(values, "nugget"));
		Assertions.assertEquals(8*227+16, getValue(values, "goldmelon"));
	}

	@Test
	@DisplayName("Test EMC calculation for molten enderpearls")
	void moltenEnderpearl()
	{
		mappingCollector.setValueBefore("enderpearl", 1024L);
		mappingCollector.setValueBefore("bucket", 768L);

		//Conversion using mili-milibuckets to make the 'emc per milibucket' smaller than 1
		mappingCollector.addConversion(250*1000, "moltenEnder", Collections.singletonList("enderpearl"));
		mappingCollector.addConversion(1, "moltenEnderBucket", ImmutableMap.of("moltenEnder", 1000 * 1000, "bucket", 1));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1024, getValue(values, "enderpearl"));
		Assertions.assertEquals(0, getValue(values, "moltenEnder"));
		Assertions.assertEquals(768, getValue(values, "bucket"));
		Assertions.assertEquals(4*1024+768, getValue(values, "moltenEnderBucket"));
	}

	@Test
	@DisplayName("Test EMC calculation for molten enderpearls with conversion arithmetic")
	void moltenEnderpearlWithConversionArithmetic()
	{
		FullBigFractionArithmetic fullFractionArithmetic = new FullBigFractionArithmetic();
		mappingCollector.setValueBefore("enderpearl", 1024L);
		mappingCollector.setValueBefore("bucket", 768L);

		//Conversion using milibuckets with a "don't round anything down"-arithmetic
		mappingCollector.addConversion(250, "moltenEnder", Collections.singletonList("enderpearl"), fullFractionArithmetic);
		mappingCollector.addConversion(1, "moltenEnderBucket", ImmutableMap.of("moltenEnder", 1000, "bucket", 1));

		//Without using the full fraction arithmetic
		mappingCollector.addConversion(250, "moltenEnder2", Collections.singletonList("enderpearl"));
		mappingCollector.addConversion(1, "moltenEnderBucket2", ImmutableMap.of("moltenEnder2", 1000, "bucket", 1));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1024, getValue(values, "enderpearl"));
		Assertions.assertEquals(768, getValue(values, "bucket"));
		Assertions.assertEquals(4*1024+768, getValue(values, "moltenEnderBucket"));
		Assertions.assertNotEquals(4*1024+767, getValue(values, "moltenEnderBucket2"));
	}


	@Test
	@DisplayName("Test reliquary vial recipe EMC calculations")
	void reliquaryVials()
	{
		mappingCollector.setValueBefore("glass", 1L);

		mappingCollector.addConversion(16, "pane", ImmutableMap.of("glass", 6));
		mappingCollector.addConversion(5, "vial", ImmutableMap.of("pane", 5));
		//Internal EMC of pane and vial: 3/8 = 0.375
		//So 8 * vial should have an emc of 3 => testItem should have emc of 1
		mappingCollector.addConversion(3, "testItem1", ImmutableMap.of("pane", 8));
		mappingCollector.addConversion(3, "testItem2", ImmutableMap.of("vial", 8));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "glass"));
		Assertions.assertEquals(0, getValue(values, "pane"));
		Assertions.assertEquals(0, getValue(values, "vial"));
		Assertions.assertEquals(1, getValue(values, "testItem1"));
		Assertions.assertEquals(1, getValue(values, "testItem2"));
	}

	@Test
	@DisplayName("Test Propagation of values")
	void propagation()
	{
		mappingCollector.setValueBefore("a", 1L);

		mappingCollector.addConversion(2, "ahalf", ImmutableMap.of("a", 1));
		mappingCollector.addConversion(1, "ahalf2", ImmutableMap.of("ahalf", 1));
		mappingCollector.addConversion(1, "2ahalf2", ImmutableMap.of("ahalf2", 2));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a"));
		Assertions.assertEquals(0, getValue(values, "ahalf"));
		Assertions.assertEquals(0, getValue(values, "ahalf2"));
		Assertions.assertEquals(1, getValue(values, "2ahalf2"));
	}

	private static <T, V extends Number> long getValue(Map<T, V> map, T key) {
		V val = map.get(key);
		return val == null ? 0 : val.longValue();
	}
}