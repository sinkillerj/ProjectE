package moze_intel.projecte.emc;

import static org.junit.Assert.assertEquals;

import moze_intel.projecte.emc.arithmetics.HiddenFractionArithmetic;
import moze_intel.projecte.emc.valuetranslators.FractionToIntegerTranslator;

import org.apache.commons.lang3.math.Fraction;
import org.junit.Before;
import org.junit.Test;
import scala.Int;

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

	private static <T, V extends Number> int getValue(Map<T, V> map, T key) {
		V val = map.get(key);
		if (val == null) return 0;
		return val.intValue();
	}

}
