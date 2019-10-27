package moze_intel.projecte.emc.mappers.customConversions;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moze_intel.projecte.api.nss.NSSCreator;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.emc.json.NSSSerializer;
import moze_intel.projecte.emc.mappers.customConversions.json.ConversionGroup;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Custom Conversion Mappers")
class CustomConversionMapperTest
{

	@BeforeAll
	@DisplayName("Manually load the default supported json serializers")
	static void setupAdditionalSerializers() {
		//IMC/init does not happen for tests so we need to manually add our serializers
		Map<String, NSSCreator> creators = new HashMap<>();
		creators.put("FAKE", NSSSerializer.fakeCreator);
		creators.put("ITEM", NSSSerializer.itemCreator);
		creators.put("FLUID", NSSSerializer.fluidCreator);
		NSSSerializer.INSTANCE.setCreators(creators);
	}

	@Test
	@DisplayName("Test conversion file that only contains a comment")
	void testCommentOnlyCustomConversionFileJson() {
		String simpleFile = "{'comment':'A very simple Example'}";
		CustomConversionFile f = CustomConversionMapper.parseJson(new StringReader(simpleFile));
		Assertions.assertNotNull(f);
		Assertions.assertEquals("A very simple Example", f.comment);
	}

	@Test
	@DisplayName("Test conversion file with empty group")
	void testSingleEmptyGroupConversionFileJson() {
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
		Assertions.assertNotNull(f);
		Assertions.assertEquals(1, f.groups.size());
		Assertions.assertTrue(f.groups.containsKey("groupa"), "Map contains key for group");
		ConversionGroup group = f.groups.get("groupa");
		Assertions.assertNotNull(group);
		Assertions.assertEquals(group.comment, "A conversion group for something", "Group contains specific comment");
		Assertions.assertEquals(0, group.conversions.size());
	}

	@Test
	@DisplayName("Test simple conversion file")
	void testSimpleConversionFileJson() {
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
		Assertions.assertNotNull(f);
		Assertions.assertEquals(1, f.groups.size());
		Assertions.assertTrue(f.groups.containsKey("groupa"), "Map contains key for group");
		ConversionGroup group = f.groups.get("groupa");
		Assertions.assertNotNull(group);
		Assertions.assertEquals(3, group.conversions.size());
		List<CustomConversion> conversions = group.conversions;
		{
			CustomConversion conversion = conversions.get(0);
			Assertions.assertEquals(NSSItem.createItem(new ResourceLocation("out_a")), conversion.output);
			Assertions.assertEquals(1, conversion.count);
			Assertions.assertEquals(3, conversion.ingredients.size());
			Assertions.assertEquals(1, (int)conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing1"))));
			Assertions.assertEquals(2, (int)conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing2"))));
			Assertions.assertEquals(3, (int)conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing3"))));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(NSSItem.createItem(new ResourceLocation("out_b")), conversion.output);
			Assertions.assertEquals(1, conversion.count);
			Assertions.assertEquals(3, conversion.ingredients.size());
			Assertions.assertEquals(1, (int)conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing1"))));
			Assertions.assertEquals(1, (int)conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing2"))));
			Assertions.assertEquals(1, (int)conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing3"))));
		}
		{
			CustomConversion conversion = conversions.get(2);
			Assertions.assertEquals(NSSItem.createItem(new ResourceLocation("out_c")), conversion.output);
			Assertions.assertEquals(3, conversion.count);
			Assertions.assertEquals(1, conversion.ingredients.size());
			Assertions.assertEquals(3, (int) conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing1"))));
		}
	}

	@Test
	@DisplayName("Test conversion file setting value")
	void testSetValueConversionFileJson()
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
		Assertions.assertNotNull(f.values);
		Assertions.assertEquals(1, f.values.setValueBefore.get(NSSItem.createItem(new ResourceLocation("a"))).longValue());
		Assertions.assertEquals(2, f.values.setValueBefore.get(NSSItem.createItem(new ResourceLocation("b"))).longValue());
		Assertions.assertEquals(Constants.FREE_ARITHMETIC_VALUE, f.values.setValueBefore.get(NSSItem.createItem(new ResourceLocation("c"))).longValue());
		Assertions.assertEquals(3, f.values.setValueAfter.get(NSSItem.createItem(new ResourceLocation("d"))).longValue());

	}

	@Test
	@DisplayName("Test set value from conversion")
	void testSetValueFromConversion()
	{
		String simpleFile =
				"{'values': {" +
						"	'conversion': [" +
						"		{'output':'out_a', 'ingredients':{'ing1': 1, 'ing2': 2, 'ing3': 3}}" +
						"	]" +
						"}" +
						"}";
		CustomConversionFile f = CustomConversionMapper.parseJson(new StringReader(simpleFile));
		Assertions.assertNotNull(f.values);
		Assertions.assertNotNull(f.values.conversion);
		Assertions.assertEquals(1, f.values.conversion.size());
		CustomConversion conversion = f.values.conversion.get(0);
		Assertions.assertEquals(NSSItem.createItem(new ResourceLocation("out_a")), conversion.output);
		Assertions.assertEquals(1, conversion.count);
		Assertions.assertEquals(3, conversion.ingredients.size());
		Assertions.assertEquals(1, (int)conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing1"))));
		Assertions.assertEquals(2, (int)conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing2"))));
		Assertions.assertEquals(3, (int)conversion.ingredients.get(NSSItem.createItem(new ResourceLocation("ing3"))));
	}

	@Test
	@DisplayName("Test to make sure FAKE values in conversions don't break things")
	void testNonInterferingFakes() {
		String file1 = "{ 'values': { 'conversion': [{ 'output':'FAKE|FOO', 'ingredients': ['FAKE|BAR'] }] }  }";

		NSSFake.setCurrentNamespace("file1");
		CustomConversionFile f1 = CustomConversionMapper.parseJson(new StringReader(file1));
		CustomConversionFile f2 = CustomConversionMapper.parseJson(new StringReader(file1));
		NSSFake.setCurrentNamespace("file2");
		CustomConversionFile f3 = CustomConversionMapper.parseJson(new StringReader(file1));

		Assertions.assertNotNull(f1);
		Assertions.assertNotNull(f2);
		Assertions.assertNotNull(f3);

		CustomConversion conversion1 = f1.values.conversion.get(0);
		CustomConversion conversion2 = f2.values.conversion.get(0);
		CustomConversion conversion3 = f3.values.conversion.get(0);

		Assertions.assertNotNull(conversion1);
		Assertions.assertNotNull(conversion2);
		Assertions.assertNotNull(conversion3);

		Assertions.assertEquals(conversion1.output, conversion2.output);
		Assertions.assertNotEquals(conversion1.output, conversion3.output);
		Assertions.assertNotEquals(conversion2.output, conversion3.output);
	}
}