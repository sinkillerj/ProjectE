package moze_intel.projecte.emc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import moze_intel.projecte.emc.arithmetics.DoubleArithmetic;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import moze_intel.projecte.emc.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.collector.LongToDoubleCollector;
import moze_intel.projecte.emc.generators.DoubleToLongGenerator;
import moze_intel.projecte.emc.generators.IValueGenerator;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.math.Fraction;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class HiddenFractionSpecificTest
{
	private IValueGenerator<String, Long> valueGenerator;
	private IExtendedMappingCollector<String, Long, IValueArithmetic<Double>> mappingCollector;

	@Before
	public void setup()
	{
		SimpleGraphMapper<String, Double, IValueArithmetic<Double>> mapper = new SimpleGraphMapper<>(new DoubleArithmetic());
		valueGenerator = new DoubleToLongGenerator<>(mapper);
		mappingCollector = new LongToDoubleCollector<>(mapper);
	}

	@Test
	public void slabRecipe()
	{
		mappingCollector.setValueBefore("s", 1l);
		mappingCollector.setValueBefore("redstone", 64l);
		mappingCollector.setValueBefore("glass", 1l);
		mappingCollector.addConversion(6, "slab", Arrays.asList("s", "s", "s"));
		mappingCollector.addConversion(1, "doubleslab", Arrays.asList("slab", "slab"));
		mappingCollector.addConversion(1, "transferpipe", Arrays.asList("slab", "slab", "slab", "glass", "redstone", "glass", "slab", "slab", "slab"));
		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "s"));
		assertEquals(64, getValue(values, "redstone"));
		assertEquals(1, getValue(values, "glass"));
		assertEquals(0, getValue(values, "slab"));
		assertEquals(3 + 64 + 2, getValue(values, "transferpipe"));
		assertEquals(1, getValue(values, "doubleslab"));

	}

	@Test
	public void nuggetExploits()
	{
		mappingCollector.setValueBefore("ingot", 2048l);
		mappingCollector.setValueBefore("melon", 16l);
		mappingCollector.addConversion(9, "nugget", Collections.singletonList("ingot"));
		mappingCollector.addConversion(1, "goldmelon", Arrays.asList(
				"nugget", "nugget", "nugget",
				"nugget", "melon", "nugget",
				"nugget", "nugget", "nugget"
		));


		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(2048, getValue(values, "ingot"));
		assertEquals(16, getValue(values, "melon"));
		assertEquals(227, getValue(values, "nugget"));
		assertEquals(8*227+16, getValue(values, "goldmelon"));
	}

	@Test
	public void moltenEnderpearl()
	{
		mappingCollector.setValueBefore("enderpearl", 1024l);
		mappingCollector.setValueBefore("bucket", 768l);

		//Conversion using mili-milibuckets to make the 'emc per milibucket' smaller than 1
		mappingCollector.addConversion(250*1000, "moltenEnder", Collections.singletonList("enderpearl"));
		mappingCollector.addConversion(1, "moltenEnderBucket", ImmutableMap.of("moltenEnder", 1000 * 1000, "bucket", 1));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1024, getValue(values, "enderpearl"));
		assertEquals(0, getValue(values, "moltenEnder"));
		assertEquals(768, getValue(values, "bucket"));
		assertEquals(4*1024+768, getValue(values, "moltenEnderBucket"));

	}

	@Test
	public void moltenEnderpearlWithConversionArithmetic()
	{
		DoubleArithmetic fullFractionArithmetic = new DoubleArithmetic();
		mappingCollector.setValueBefore("enderpearl", 1024l);
		mappingCollector.setValueBefore("bucket", 768l);

		//Conversion using milibuckets with a "don't round anything down"-arithmetic
		mappingCollector.addConversion(250, "moltenEnder", Collections.singletonList("enderpearl"), fullFractionArithmetic);
		mappingCollector.addConversion(1, "moltenEnderBucket", ImmutableMap.of("moltenEnder", 1000, "bucket", 1));

		//Without using the full fraction arithmetic
		mappingCollector.addConversion(250, "moltenEnder2", Collections.singletonList("enderpearl"));
		mappingCollector.addConversion(1, "moltenEnderBucket2", ImmutableMap.of("moltenEnder2", 1000, "bucket", 1));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1024, getValue(values, "enderpearl"));
		assertEquals(768, getValue(values, "bucket"));
		assertEquals(4*1024+768, getValue(values, "moltenEnderBucket"));

		assertNotEquals(4*1024+767, getValue(values, "moltenEnderBucket2"));

	}


	@Test
	public void reliquaryVials()
	{
		mappingCollector.setValueBefore("glass", 1l);

		mappingCollector.addConversion(16, "pane", ImmutableMap.of("glass", 6));
		mappingCollector.addConversion(5, "vial", ImmutableMap.of("pane", 5));
		//Internal EMC of pane and vial: 3/8 = 0.375
		//So 8 * vial should have an emc of 3 => testItem should have emc of 1
		mappingCollector.addConversion(3, "testItem1", ImmutableMap.of("pane", 8));
		mappingCollector.addConversion(3, "testItem2", ImmutableMap.of("vial", 8));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "glass"));
		assertEquals(0, getValue(values, "pane"));
		assertEquals(0, getValue(values, "vial"));
		assertEquals(1, getValue(values, "testItem1"));
		assertEquals(1, getValue(values, "testItem2"));
	}

	@Test
	public void propagation()
	{
		mappingCollector.setValueBefore("a", 1l);

		mappingCollector.addConversion(2, "ahalf", ImmutableMap.of("a", 1));
		mappingCollector.addConversion(1, "ahalf2", ImmutableMap.of("ahalf", 1));
		mappingCollector.addConversion(1, "2ahalf2", ImmutableMap.of("ahalf2", 2));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a"));
		assertEquals(0, getValue(values, "ahalf"));
		assertEquals(0, getValue(values, "ahalf2"));
		assertEquals(1, getValue(values, "2ahalf2"));

	}

	private static <T, V extends Number> int getValue(Map<T, V> map, T key) {
		V val = map.get(key);
		if (val == null) return 0;
		return val.intValue();
	}

}
