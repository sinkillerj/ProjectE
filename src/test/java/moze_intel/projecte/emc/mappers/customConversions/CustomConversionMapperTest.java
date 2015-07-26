package moze_intel.projecte.emc.mappers.customConversions;

import static org.junit.Assert.*;

import moze_intel.projecte.emc.mappers.customConversions.json.ConversionGroup;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;

import org.junit.Test;

import java.io.StringReader;

public class CustomConversionMapperTest
{
	@Test
	public void testCommentOnlyCustomConversionFileJson() {
		String simpleFile = "{'comment':'A very simple Example'}";
		CustomConversionFile f = CustomConversionMapper.parseJson(new StringReader(simpleFile));
		assertNotNull(f);
		assertEquals("A very simple Example", f.comment);
	}

	@Test
	public void testSingleEmptyGroupConversionFileJson() {
		String simpleFile =
				"{'groups': {" +
						"	'groupa': {" +
						"		'comment':'A conversion group for something'," +
						"		'conversions':[" +
						"		]" +
						"	}" +
						"}" +
					"}";

		CustomConversionFile f = CustomConversionMapper.parseJson(new StringReader(simpleFile));
		assertNotNull(f);
		assertEquals(1, f.groups.size());
		assertTrue("Map contains key for group", f.groups.containsKey("groupa"));
		ConversionGroup group = f.groups.get("groupa");
		assertNotNull(group);
		assertEquals("Group contains specific comment", group.comment, "A conversion group for something");
		assertEquals(0, group.conversions.length);
	}

	@Test
	public void testSimpleConversionFileJson() {
		String simpleFile =
				"{'groups': {" +
						"	'groupa': {" +
						"		'conversions':[" +
						"			{'output':'outA', 'ingr':{'ing1': 1, 'ing2': 2, 'ing3': 3}}," +
						"			{'output':'outB', 'ingr':['ing1', 'ing2', 'ing3']}," +
						"			{'output':'outC', 'count':3, 'ingr':['ing1', 'ing1', 'ing1']}" +
						"		]" +
						"	}" +
						"}" +
					"}";

		CustomConversionFile f = CustomConversionMapper.parseJson(new StringReader(simpleFile));
		assertNotNull(f);
		assertEquals(1, f.groups.size());
		assertTrue("Map contains key for group", f.groups.containsKey("groupa"));
		ConversionGroup group = f.groups.get("groupa");
		assertNotNull(group);
		assertEquals(3, group.conversions.length);
		CustomConversion[] conversions = group.conversions;
		{
			CustomConversion conversion = conversions[0];
			assertEquals("outA", conversion.output);
			assertEquals(1, conversion.count);
			assertEquals(3, conversion.ingredients.size());
			assertEquals(1, (int)conversion.ingredients.get("ing1"));
			assertEquals(2, (int)conversion.ingredients.get("ing2"));
			assertEquals(3, (int)conversion.ingredients.get("ing3"));
		}
		{
			CustomConversion conversion = conversions[1];
			assertEquals("outB", conversion.output);
			assertEquals(1, conversion.count);
			assertEquals(3, conversion.ingredients.size());
			assertEquals(1, (int)conversion.ingredients.get("ing1"));
			assertEquals(1, (int)conversion.ingredients.get("ing2"));
			assertEquals(1, (int)conversion.ingredients.get("ing3"));
		}
		{
			CustomConversion conversion = conversions[2];
			assertEquals("outC", conversion.output);
			assertEquals(3, conversion.count);
			assertEquals(1, conversion.ingredients.size());
			assertEquals(3, (int) conversion.ingredients.get("ing1"));

		}
	}

	@Test
	public void testSetValueConversionFileJson()
	{
		String simpleFile =
				"{'values': {" +
						"	'before': {" +
						"		'a': 1, 'b': 2, 'c': 'free'" +
						"	}," +
						"	'after': {" +
						"		'd': 3" +
						"	}" +
						"}" +
						"}";
		CustomConversionFile f = CustomConversionMapper.parseJson(new StringReader(simpleFile));
		assertNotNull(f.values);
		assertEquals(1, (int) f.values.setValueBefore.get("a"));
		assertEquals(2, (int) f.values.setValueBefore.get("b"));
		assertEquals(Integer.MIN_VALUE, (int) f.values.setValueBefore.get("c"));
		assertEquals(3, (int) f.values.setValueAfter.get("d"));

	}
}