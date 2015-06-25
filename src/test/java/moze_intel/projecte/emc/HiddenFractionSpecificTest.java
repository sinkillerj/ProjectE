package moze_intel.projecte.emc;

import static org.junit.Assert.assertEquals;

import moze_intel.projecte.emc.arithmetics.HiddenFractionArithmetic;
import moze_intel.projecte.emc.valuetranslators.FractionToIntegerTranslator;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.math.Fraction;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

public class HiddenFractionSpecificTest
{
	public IValueGenerator<String, Integer> graphMapper;

	@Before
	public void setup()
	{
		graphMapper = new FractionToIntegerTranslator<String>(new SimpleGraphMapper<String, Fraction>(new HiddenFractionArithmetic()));
	}

	@Test
	public void slabRecipe()
	{
		graphMapper.setValue("s", 1, IMappingCollector.FixedValue.FixAndInherit);
		graphMapper.setValue("redstone", 64, IMappingCollector.FixedValue.FixAndInherit);
		graphMapper.setValue("glass", 1, IMappingCollector.FixedValue.FixAndInherit);
		graphMapper.addConversion(6, "slab", Arrays.asList("s", "s", "s"));
		graphMapper.addConversion(1, "doubleslab", Arrays.asList("slab", "slab"));
		graphMapper.addConversion(1, "transferpipe", Arrays.asList("slab", "slab", "slab", "glass", "redstone", "glass", "slab", "slab", "slab"));
		Map<String, Integer> values = graphMapper.generateValues();
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
		graphMapper.setValue("ingot", 2048, IMappingCollector.FixedValue.FixAndInherit);
		graphMapper.setValue("melon", 16, IMappingCollector.FixedValue.FixAndInherit);
		graphMapper.addConversion(9, "nugget", Arrays.asList("ingot"));
		graphMapper.addConversion(1, "goldmelon", Arrays.asList(
				"nugget", "nugget", "nugget",
				"nugget", "melon", "nugget",
				"nugget", "nugget", "nugget"
		));


		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(2048, getValue(values, "ingot"));
		assertEquals(16, getValue(values, "melon"));
		assertEquals(227, getValue(values, "nugget"));
		assertEquals(8*227+16, getValue(values, "goldmelon"));
	}

	@Test
	public void moltenEnderpearl()
	{
		graphMapper.setValue("enderpearl", 1024, IMappingCollector.FixedValue.FixAndInherit);
		graphMapper.setValue("bucket", 768, IMappingCollector.FixedValue.FixAndInherit);

		//Conversion using mili-milibuckets to make the 'emc per milibucket' smaller than 1
		graphMapper.addConversion(250*1000, "moltenEnder", Arrays.asList("enderpearl"));
		graphMapper.addConversionMultiple(1, "moltenEnderBucket", ImmutableMap.of("moltenEnder", 1000*1000, "bucket", 1));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1024, getValue(values, "enderpearl"));
		assertEquals(0, getValue(values, "moltenEnder"));
		assertEquals(768, getValue(values, "bucket"));
		assertEquals(4*1024+768, getValue(values, "moltenEnderBucket"));

	}


	@Test
	public void fractionPropagation()
	{
		graphMapper.setValue("glass", 1, IMappingCollector.FixedValue.FixAndInherit);

		graphMapper.addConversionMultiple(16, "pane", ImmutableMap.of("glass", 6));
		graphMapper.addConversionMultiple(5, "vial", ImmutableMap.of("pane", 5));
		//Internal EMC of pane and vial: 3/8 = 0.375
		//So 8 * vial should have an emc of 3 => testItem should have emc of 1
		graphMapper.addConversionMultiple(3, "testItem1", ImmutableMap.of("pane", 8));
		graphMapper.addConversionMultiple(3, "testItem2", ImmutableMap.of("vial", 8));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "glass"));
		assertEquals(0, getValue(values, "pane"));
		assertEquals(0, getValue(values, "vial"));
		assertEquals(1, getValue(values, "testItem1"));
		assertEquals(1, getValue(values, "testItem2"));
	}

	private static <T, V extends Number> int getValue(Map<T, V> map, T key) {
		V val = map.get(key);
		if (val == null) return 0;
		return val.intValue();
	}

}
