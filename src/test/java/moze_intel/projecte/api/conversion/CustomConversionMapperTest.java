package moze_intel.projecte.api.conversion;

import java.util.List;
import java.util.Map;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.api.nss.NormalizedSimpleStackTestHelper;
import moze_intel.projecte.impl.codec.CodecTestHelper;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Custom Conversion Mappers")
class CustomConversionMapperTest extends NormalizedSimpleStackTestHelper {

	private static CustomConversionFile parseJson(String json) {
		return CodecTestHelper.parseJson(CustomConversionFile.CODEC, "custom conversion test", json);
	}

	@BeforeAll
	@DisplayName("Manually load the default supported codecs")
	static void setupBuiltinCodecs() {
		//Registry init does not happen for tests, so we need to manually add our codecs
		CodecTestHelper.initBuiltinNSS();
	}

	@Test
	@DisplayName("Test conversion file that only contains a comment")
	void testCommentOnlyCustomFile() {
		CustomConversionFile conversionFile = parseJson("""
				{
					"comment": "A very simple Example"
				}""");
		Assertions.assertEquals("A very simple Example", conversionFile.comment());
	}

	@Test
	@DisplayName("Test conversion file with empty group")
	void testSingleEmptyGroupFile() {
		CustomConversionFile conversionFile = parseJson("""
				{
					"groups": {
						"groupa": {
							"comment": "A conversion group for something",
							"conversions": [
							]
						}
					}
				}""");
		Assertions.assertEquals(1, conversionFile.groups().size());
		Assertions.assertTrue(conversionFile.groups().containsKey("groupa"), "Map contains key for group");
		ConversionGroup group = conversionFile.groups().get("groupa");
		Assertions.assertEquals(group.comment(), "A conversion group for something", "Group contains specific comment");
		Assertions.assertEquals(0, group.size());
	}

	@Test
	@DisplayName("Test simple conversion file")
	void testSimpleFile() {
		CustomConversionFile conversionFile = parseJson("""
				{
					"groups": {
						"groupa": {
							"conversions": [
								{
									"output": "out_a",
									"ingredients": {
										"ing1": 1,
										"ing2": 2,
										"ing3": 3
									}
								},
								{
									"output": "out_b",
									"ingredients": [
										"ing1",
										"ing2",
										"ing3"
									]
								},
								{
									"output": "out_c",
									"count": 3,
									"ingredients": [
										"ing1",
										"ing1",
										"ing1"
									]
								}
							]
						}
					}
				}""");
		Assertions.assertEquals(1, conversionFile.groups().size());
		Assertions.assertTrue(conversionFile.groups().containsKey("groupa"), "Map contains key for group");
		ConversionGroup group = conversionFile.groups().get("groupa");
		Assertions.assertEquals(3, group.size());
		List<CustomConversion> conversions = group.conversions();
		{
			CustomConversion conversion = conversions.get(0);
			Assertions.assertEquals(createItem("out_a"), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Map<NormalizedSimpleStack, Integer> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.get(createItem("ing1")));
			Assertions.assertEquals(2, ingredients.get(createItem("ing2")));
			Assertions.assertEquals(3, ingredients.get(createItem("ing3")));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(createItem("out_b"), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Map<NormalizedSimpleStack, Integer> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.get(createItem("ing1")));
			Assertions.assertEquals(1, ingredients.get(createItem("ing2")));
			Assertions.assertEquals(1, ingredients.get(createItem("ing3")));
		}
		{
			CustomConversion conversion = conversions.get(2);
			Assertions.assertEquals(createItem("out_c"), conversion.output());
			Assertions.assertEquals(3, conversion.count());
			Map<NormalizedSimpleStack, Integer> ingredients = conversion.ingredients();
			Assertions.assertEquals(1, ingredients.size());
			Assertions.assertEquals(3, ingredients.get(createItem("ing1")));
		}
	}

	@Test
	@DisplayName("Test conversion file setting value")
	void testSetValueFile() {
		CustomConversionFile conversionFile = parseJson("""
				{
					"values": {
						"before": {
							"a": 1,
							"b": 2,
							"c": "free"
						},
						"after": {
							"d": 3
						}
					}
				}""");
		FixedValues values = conversionFile.values();
		Assertions.assertEquals(1, values.setValueBefore().get(createItem("a")));
		Assertions.assertEquals(2, values.setValueBefore().get(createItem("b")));
		Assertions.assertEquals(ProjectEAPI.FREE_ARITHMETIC_VALUE, values.setValueBefore().get(createItem("c")));
		Assertions.assertEquals(3, values.setValueAfter().get(createItem("d")));
	}

	@Test
	@DisplayName("Test conversion file skipping invalid keys for setting value")
	void testInvalidKeySetValueFile() {
		CustomConversionFile conversionFile = parseJson("""
				{
					"values": {
						"before": {
							"INVALID|a": 1,
							"b": 2
						},
						"after": {
							"INVALID|d": 3
						}
					}
				}""");
		FixedValues values = conversionFile.values();
		Assertions.assertEquals(2, values.setValueBefore().get(createItem("b")));
		Assertions.assertTrue(values.setValueAfter().isEmpty());
	}

	@Test
	@DisplayName("Test set value from conversion")
	void testSetValueFromConversion() {
		CustomConversionFile conversionFile = parseJson("""
				{
					"values": {
						"conversion": [
							{
								"output": "out_a",
								"ingredients": {
									"ing1": 1,
									"ing2": 2,
									"ing3": 3
								}
							}
						]
					}
				}""");
		Assertions.assertEquals(1, conversionFile.values().conversions().size());
		CustomConversion conversion = conversionFile.values().conversions().get(0);
		Assertions.assertEquals(createItem("out_a"), conversion.output());
		Assertions.assertEquals(1, conversion.count());
		Map<NormalizedSimpleStack, Integer> ingredients = conversion.ingredients();
		Assertions.assertEquals(3, ingredients.size());
		Assertions.assertEquals(1, ingredients.get(createItem("ing1")));
		Assertions.assertEquals(2, ingredients.get(createItem("ing2")));
		Assertions.assertEquals(3, ingredients.get(createItem("ing3")));
	}

	@Test
	@DisplayName("Test explicit format in conversions")
	void testConversionExplicitFormat() {
		CustomConversionFile conversionFile = parseJson("""
				{
					"values": {
						"conversion": [
							{
								"output": {
									"type": "projecte:item",
									"id": "out_a"
								},
								"ingredients": {
									"ing1": 1,
									"ing2": 2,
									"ing3": 3
								}
							},
							{
								"output": {
									"type": "projecte:item",
									"id": "out_b",
									"nbt": {
										"my": "tag"
									}
								},
								"ingredients": [
									{
										"type": "projecte:item",
										"id": "ing1"
									},
									"ing2",
									{
										"type": "projecte:item",
										"id": "ing3",
										"nbt": "{my: \\"tag\\"}"
									}
								]
							}
						]
					}
				}""");
		CompoundTag nbt = new CompoundTag();
		nbt.putString("my", "tag");
		List<CustomConversion> conversions = conversionFile.values().conversions();
		Assertions.assertEquals(2, conversions.size());
		{
			CustomConversion conversion = conversions.get(0);
			Assertions.assertEquals(createItem("out_a"), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Map<NormalizedSimpleStack, Integer> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.get(createItem("ing1")));
			Assertions.assertEquals(2, ingredients.get(createItem("ing2")));
			Assertions.assertEquals(3, ingredients.get(createItem("ing3")));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(createItem("minecraft", "out_b", nbt), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Map<NormalizedSimpleStack, Integer> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.get(createItem("ing1")));
			Assertions.assertEquals(1, ingredients.get(createItem("ing2")));
			Assertions.assertEquals(1, ingredients.get(createItem("minecraft", "ing3", nbt)));
		}
	}

	@Test
	@DisplayName("Test to make sure FAKE values in conversions don't break things")
	void testNonInterferingFakes() {
		String file1 = """
				{
					"values": {
						"conversion": [
							{
								"output": "FAKE|FOO",
								"ingredients": [
									"FAKE|BAR"
								]
							}
						]
					}
				}""";

		NSSFake.setCurrentNamespace("file1");
		CustomConversionFile conversionFile1 = parseJson(file1);
		CustomConversionFile conversionFile2 = parseJson(file1);
		NSSFake.setCurrentNamespace("file2");
		CustomConversionFile conversionFile3 = parseJson(file1);

		CustomConversion conversion1 = conversionFile1.values().conversions().get(0);
		CustomConversion conversion2 = conversionFile2.values().conversions().get(0);
		CustomConversion conversion3 = conversionFile3.values().conversions().get(0);

		Assertions.assertEquals(conversion1.output(), conversion2.output());
		Assertions.assertNotEquals(conversion1.output(), conversion3.output());
		Assertions.assertNotEquals(conversion2.output(), conversion3.output());
	}

	@Test
	@DisplayName("Test ignore invalid conversions")
	void testIgnoreInvalidConversions() {
		CustomConversionFile conversionFile = parseJson("""
				{
					"groups": {
						"groupa": {
							"conversions": [
								{
									"output": "out_a",
									"ingredients": {
										"ing1": 1,
										"ing2": 2,
										"ing3": 3
									}
								},
								{
									"output": "out_b"
								},
								{
									"output": "out_c",
									"count": 3,
									"ingredients": [
										"ing1",
										"ing1",
										"ing1"
									]
								}
							]
						}
					}
				}""");
		Assertions.assertEquals(1, conversionFile.groups().size());
		Assertions.assertTrue(conversionFile.groups().containsKey("groupa"), "Map contains key for group");
		ConversionGroup group = conversionFile.groups().get("groupa");
		Assertions.assertEquals(2, group.size());
		List<CustomConversion> conversions = group.conversions();
		{
			CustomConversion conversion = conversions.get(0);
			Assertions.assertEquals(createItem("out_a"), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Map<NormalizedSimpleStack, Integer> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.get(createItem("ing1")));
			Assertions.assertEquals(2, ingredients.get(createItem("ing2")));
			Assertions.assertEquals(3, ingredients.get(createItem("ing3")));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(createItem("out_c"), conversion.output());
			Assertions.assertEquals(3, conversion.count());
			Map<NormalizedSimpleStack, Integer> ingredients = conversion.ingredients();
			Assertions.assertEquals(1, ingredients.size());
			Assertions.assertEquals(3, ingredients.get(createItem("ing1")));
		}
	}
}