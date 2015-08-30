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
	public IValueGenerator<String, Integer, IValueArithmetic<Fraction>> graphMapper;

	@Before
	public void setup()
	{
		graphMapper = new FractionToIntegerTranslator<String, IValueArithmetic<Fraction>>(new SimpleGraphMapper<String, Fraction, IValueArithmetic<Fraction>>(new HiddenFractionArithmetic()));
	}

	@Test
	public void slabRecipe()
	{
		graphMapper.setValueBefore("s", 1);
		graphMapper.setValueBefore("redstone", 64);
		graphMapper.setValueBefore("glass", 1);
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
		graphMapper.setValueBefore("ingot", 2048);
		graphMapper.setValueBefore("melon", 16);
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
		graphMapper.setValueBefore("enderpearl", 1024);
		graphMapper.setValueBefore("bucket", 768);

		//Conversion using mili-milibuckets to make the 'emc per milibucket' smaller than 1
		graphMapper.addConversion(250*1000, "moltenEnder", Arrays.asList("enderpearl"));
		graphMapper.addConversion(1, "moltenEnderBucket", ImmutableMap.of("moltenEnder", 1000 * 1000, "bucket", 1));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1024, getValue(values, "enderpearl"));
		assertEquals(0, getValue(values, "moltenEnder"));
		assertEquals(768, getValue(values, "bucket"));
		assertEquals(4*1024+768, getValue(values, "moltenEnderBucket"));

	}


	@Test
	public void reliquaryVials()
	{
		graphMapper.setValueBefore("glass", 1);

		graphMapper.addConversion(16, "pane", ImmutableMap.of("glass", 6));
		graphMapper.addConversion(5, "vial", ImmutableMap.of("pane", 5));
		//Internal EMC of pane and vial: 3/8 = 0.375
		//So 8 * vial should have an emc of 3 => testItem should have emc of 1
		graphMapper.addConversion(3, "testItem1", ImmutableMap.of("pane", 8));
		graphMapper.addConversion(3, "testItem2", ImmutableMap.of("vial", 8));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "glass"));
		assertEquals(0, getValue(values, "pane"));
		assertEquals(0, getValue(values, "vial"));
		assertEquals(1, getValue(values, "testItem1"));
		assertEquals(1, getValue(values, "testItem2"));
	}

	@Test
	public void propagation()
	{
		graphMapper.setValueBefore("a", 1);

		graphMapper.addConversion(2, "ahalf", ImmutableMap.of("a", 1));
		graphMapper.addConversion(1, "ahalf2", ImmutableMap.of("ahalf", 1));
		graphMapper.addConversion(1, "2ahalf2", ImmutableMap.of("ahalf2", 2));

		Map<String, Integer> values = graphMapper.generateValues();
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
