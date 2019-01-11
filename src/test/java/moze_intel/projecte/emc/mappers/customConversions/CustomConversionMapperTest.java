package moze_intel.projecte.emc.mappers.customConversions;

import static org.junit.Assert.*;

import moze_intel.projecte.emc.json.NSSFake;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.mappers.customConversions.json.ConversionGroup;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;

import net.minecraft.util.ResourceLocation;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

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
		assertEquals(0, group.conversions.size());
	}

	@Test
	public void testSimpleConversionFileJson() {
		String simpleFile =
				"{'groups': {" +
						"	'groupa': {" +
						"		'conversions':[" +
						"			{'output':'out_a', 'ingredients':{'ing1': 1, 'ing2': 2, 'ing3': 3}}," +
						"			{'output':'out_b', 'ingredients':['ing1', 'ing2', 'ing3']}," +
						"			{'output':'out_c', 'count':3, 'ingredients':['ing1', 'ing1', 'ing1']}" +
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
		assertEquals(3, group.conversions.size());
		List<CustomConversion> conversions = group.conversions;
		{
			CustomConversion conversion = conversions.get(0);
            assertEquals(new NSSItem(new ResourceLocation("out_a")), conversion.output);
			assertEquals(1, conversion.count);
			assertEquals(3, conversion.ingredients.size());
            assertEquals(1, (int)conversion.ingredients.get(new NSSItem(new ResourceLocation("ing1"))));
            assertEquals(2, (int)conversion.ingredients.get(new NSSItem(new ResourceLocation("ing2"))));
            assertEquals(3, (int)conversion.ingredients.get(new NSSItem(new ResourceLocation("ing3"))));
		}
		{
			CustomConversion conversion = conversions.get(1);
            assertEquals(new NSSItem(new ResourceLocation("out_b")), conversion.output);
			assertEquals(1, conversion.count);
			assertEquals(3, conversion.ingredients.size());
            assertEquals(1, (int)conversion.ingredients.get(new NSSItem(new ResourceLocation("ing1"))));
            assertEquals(1, (int)conversion.ingredients.get(new NSSItem(new ResourceLocation("ing2"))));
            assertEquals(1, (int)conversion.ingredients.get(new NSSItem(new ResourceLocation("ing3"))));
		}
		{
			CustomConversion conversion = conversions.get(2);
            assertEquals(new NSSItem(new ResourceLocation("out_c")), conversion.output);
			assertEquals(3, conversion.count);
			assertEquals(1, conversion.ingredients.size());
            assertEquals(3, (int) conversion.ingredients.get(new NSSItem(new ResourceLocation("ing1"))));

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
        assertEquals(1, f.values.setValueBefore.get(new NSSItem(new ResourceLocation("a"))).longValue());
        assertEquals(2, f.values.setValueBefore.get(new NSSItem(new ResourceLocation("b"))).longValue());
        assertEquals(Long.MIN_VALUE, f.values.setValueBefore.get(new NSSItem(new ResourceLocation("c"))).longValue());
        assertEquals(3, f.values.setValueAfter.get(new NSSItem(new ResourceLocation("d"))).longValue());

	}

	@Test
	public void testSetValueFromConversion()
	{
		String simpleFile =
				"{'values': {" +
						"	'conversion': [" +
						"		{'output':'out_a', 'ingredients':{'ing1': 1, 'ing2': 2, 'ing3': 3}}" +
						"	]" +
						"}" +
						"}";
		CustomConversionFile f = CustomConversionMapper.parseJson(new StringReader(simpleFile));
		assertNotNull(f.values);
		assertNotNull(f.values.conversion);
		assertEquals(1, f.values.conversion.size());
		CustomConversion conversion = f.values.conversion.get(0);
        assertEquals(new NSSItem(new ResourceLocation("out_a")), conversion.output);
		assertEquals(1, conversion.count);
		assertEquals(3, conversion.ingredients.size());
        assertEquals(1, (int)conversion.ingredients.get(new NSSItem(new ResourceLocation("ing1"))));
        assertEquals(2, (int)conversion.ingredients.get(new NSSItem(new ResourceLocation("ing2"))));
        assertEquals(3, (int)conversion.ingredients.get(new NSSItem(new ResourceLocation("ing3"))));
	}

	@Test
	public void testNonInteferingFakes() {
		String file1 = "{ 'values': { 'conversion': [{ 'output':'FAKE|FOO', 'ingredients': ['FAKE|BAR'] }] }  }";

		NSSFake.setCurrentNamespace("file1");
		CustomConversionFile f1 = CustomConversionMapper.parseJson(new StringReader(file1));
		CustomConversionFile f2 = CustomConversionMapper.parseJson(new StringReader(file1));
		NSSFake.setCurrentNamespace("file2");
		CustomConversionFile f3 = CustomConversionMapper.parseJson(new StringReader(file1));

		assertNotNull(f1);
		assertNotNull(f2);
		assertNotNull(f3);

		CustomConversion conversion1 = f1.values.conversion.get(0);
		CustomConversion conversion2 = f2.values.conversion.get(0);
		CustomConversion conversion3 = f3.values.conversion.get(0);

		assertNotNull(conversion1);
		assertNotNull(conversion2);
		assertNotNull(conversion3);

		assertEquals(conversion1.output, conversion2.output);
		assertNotEquals(conversion1.output, conversion3.output);
		assertNotEquals(conversion2.output, conversion3.output);
	}
}