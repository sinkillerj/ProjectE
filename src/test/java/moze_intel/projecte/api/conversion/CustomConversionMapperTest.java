package moze_intel.projecte.api.conversion;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.impl.codec.CodecTestHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
@DisplayName("Test Custom Conversion Mappers")
class CustomConversionMapperTest {

	@AfterEach
	void resetFakeNamespace() {
		NSSFake.resetNamespace();
	}

	private static CustomConversionFile parseJson(HolderLookup.Provider registryAccess, String json) {
		return CodecTestHelper.parseJson(registryAccess, CustomConversionFile.CODEC, "custom conversion test", json);
	}

	@Test
	@DisplayName("Test conversion file that only contains a comment")
	void testCommentOnlyCustomFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"comment": "A very simple Example"
				}""");
		Assertions.assertEquals("A very simple Example", conversionFile.comment());
	}

	@Test
	@DisplayName("Test conversion file with empty group")
	void testSingleEmptyGroupFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
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
		Assertions.assertEquals("A conversion group for something", group.comment(), "Group contains specific comment");
		Assertions.assertEquals(0, group.size());
	}

	@Test
	@DisplayName("Test simple conversion file")
	void testSimpleFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"groups": {
						"groupa": {
							"conversions": [
								{
									"output": {
										"type": "projecte:item",
										"id": "iron_ingot"
									},
									"ingredients": [
										{
											"type": "projecte:item",
											"id": "stone"
										},
										{
											"type": "projecte:item",
											"id": "granite",
											"amount": 2
										},
										{
											"type": "projecte:item",
											"id": "diorite",
											"amount": 3
										}
									]
								},
								{
									"output": {
										"type": "projecte:item",
										"id": "gold_ingot"
									},
									"ingredients": [
										{
											"type": "projecte:item",
											"id": "stone"
										},
										{
											"type": "projecte:item",
											"id": "granite"
										},
										{
											"type": "projecte:item",
											"id": "diorite"
										}
									]
								},
								{
									"output": {
										"type": "projecte:item",
										"id": "copper_ingot"
									},
									"count": 3,
									"ingredients": [
										{
											"type": "projecte:item",
											"id": "stone"
										},
										{
											"type": "projecte:item",
											"id": "stone"
										},
										{
											"type": "projecte:item",
											"id": "stone"
										}
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
			CustomConversion conversion = conversions.getFirst();
			Assertions.assertEquals(NSSItem.createItem(Items.IRON_INGOT), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(2, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(NSSItem.createItem(Items.GOLD_INGOT), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
		}
		{
			CustomConversion conversion = conversions.get(2);
			Assertions.assertEquals(NSSItem.createItem(Items.COPPER_INGOT), conversion.output());
			Assertions.assertEquals(3, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(1, ingredients.size());
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.STONE)));
		}
	}

	@Test
	@DisplayName("Test conversion file setting value")
	void testSetValueFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"values": {
						"before": [
							{
								"type": "projecte:item",
								"id": "minecraft:stone",
								"emc_value": 1
							},
							{
								"type": "projecte:item",
								"id": "granite",
								"emc_value": 2
							},
							{
								"type": "projecte:item",
								"id": "diorite",
								"emc_value": "free"
							}
						],
						"after": [
							{
								"type": "projecte:item",
								"id": "andesite",
								"emc_value": 3
							}
						]
					}
				}""");
		FixedValues values = conversionFile.values();
		Assertions.assertEquals(1, values.setValueBefore().getLong(NSSItem.createItem(Items.STONE)));
		Assertions.assertEquals(2, values.setValueBefore().getLong(NSSItem.createItem(Items.GRANITE)));
		Assertions.assertEquals(ProjectEAPI.FREE_ARITHMETIC_VALUE, values.setValueBefore().getLong(NSSItem.createItem(Items.DIORITE)));
		Assertions.assertEquals(3, values.setValueAfter().getLong(NSSItem.createItem(Items.ANDESITE)));
	}

	@Test
	@DisplayName("Test conversion file skipping invalid keys for setting value")
	void testInvalidKeySetValueFile(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"values": {
						"before": [
							{
								"type": "projecte:item",
								"id": "INVALID|stone",
								"emc_value": 1
							},
							{
								"type": "projecte:item",
								"id": "granite",
								"emc_value": 2
							}
						],
						"after": [
							{
								"type": "projecte:item",
								"id": "INVALID|andesite",
								"emc_value": 3
							}
						]
					}
				}""");
		FixedValues values = conversionFile.values();
		Assertions.assertEquals(2, values.setValueBefore().getLong(NSSItem.createItem(Items.GRANITE)));
		Assertions.assertTrue(values.setValueAfter().isEmpty());
	}

	@Test
	@DisplayName("Test set value from conversion")
	void testSetValueFromConversion(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"values": {
						"conversion": [
							{
								"output": {
									"type": "projecte:item",
									"id": "iron_ingot"
								},
								"ingredients": [
									{
										"type": "projecte:item",
										"id": "minecraft:stone",
										"amount": 1
									},
									{
										"type": "projecte:item",
										"id": "granite",
										"amount": 2
									},
									{
										"type": "projecte:item",
										"id": "minecraft:diorite",
										"amount": 3
									}
								]
							}
						]
					}
				}""");
		Assertions.assertEquals(1, conversionFile.values().conversions().size());
		CustomConversion conversion = conversionFile.values().conversions().getFirst();
		Assertions.assertEquals(NSSItem.createItem(Items.IRON_INGOT), conversion.output());
		Assertions.assertEquals(1, conversion.count());
		Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
		Assertions.assertEquals(3, ingredients.size());
		Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
		Assertions.assertEquals(2, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
		Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
	}

	@Test
	@DisplayName("Test explicit format in conversions")
	void testConversionExplicitFormat(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"values": {
						"conversion": [
							{
								"output": {
									"type": "projecte:item",
									"id": "iron_ingot"
								},
								"ingredients": [
									{
										"type": "projecte:item",
										"id": "minecraft:stone",
										"amount": 1
									},
									{
										"type": "projecte:item",
										"id": "granite",
										"amount": 2
									},
									{
										"type": "projecte:item",
										"id": "minecraft:diorite",
										"amount": 3
									}
								]
							},
							{
								"output": {
									"type": "projecte:item",
									"id": "gold_ingot",
									"data": {
										"custom_data": {
											"my": "tag"
										}
									}
								},
								"ingredients": [
									{
										"type": "projecte:item",
										"id": "stone"
									},
									{
										"type": "projecte:item",
										"id": "granite"
									},
									{
										"type": "projecte:item",
										"id": "diorite",
										"data": {
											"custom_data": "{my: \\"tag\\"}"
										}
									}
								]
							}
						]
					}
				}""");
		List<CustomConversion> conversions = conversionFile.values().conversions();
		Assertions.assertEquals(2, conversions.size());
		{
			CustomConversion conversion = conversions.getFirst();
			Assertions.assertEquals(NSSItem.createItem(Items.IRON_INGOT), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(2, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(NSSItem.createItem(Items.GOLD_INGOT, CodecTestHelper.MY_TAG_PATCH), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.DIORITE, CodecTestHelper.MY_TAG_PATCH)));
		}
	}

	@Test
	@DisplayName("Test to make sure FAKE values in conversions don't break things")
	void testNonInterferingFakes(MinecraftServer server) {
		String file1 = """
				{
					"values": {
						"conversion": [
							{
								"output": {
									"type": "projecte:fake",
									"description": "FOO"
								},
								"ingredients": [
									{
										"type": "projecte:fake",
										"description": "BAR"
									}
								]
							}
						]
					}
				}""";

		NSSFake.setCurrentNamespace("file1");
		CustomConversionFile conversionFile1 = parseJson(server.registryAccess(), file1);
		CustomConversionFile conversionFile2 = parseJson(server.registryAccess(), file1);
		NSSFake.setCurrentNamespace("file2");
		CustomConversionFile conversionFile3 = parseJson(server.registryAccess(), file1);

		CustomConversion conversion1 = conversionFile1.values().conversions().getFirst();
		CustomConversion conversion2 = conversionFile2.values().conversions().getFirst();
		CustomConversion conversion3 = conversionFile3.values().conversions().getFirst();

		Assertions.assertEquals(conversion1.output(), conversion2.output());
		Assertions.assertNotEquals(conversion1.output(), conversion3.output());
		Assertions.assertNotEquals(conversion2.output(), conversion3.output());
	}

	@Test
	@DisplayName("Test ignore invalid conversions")
	void testIgnoreInvalidConversions(MinecraftServer server) {
		CustomConversionFile conversionFile = parseJson(server.registryAccess(), """
				{
					"groups": {
						"groupa": {
							"conversions": [
								{
									"output": {
										"type": "projecte:item",
										"id": "iron_ingot"
									},
									"ingredients": [
										{
											"type": "projecte:item",
											"id": "minecraft:stone",
											"amount": 1
										},
										{
											"type": "projecte:item",
											"id": "granite",
											"amount": 2
										},
										{
											"type": "projecte:item",
											"id": "minecraft:diorite",
											"amount": 3
										}
									]
								},
								{
									"output": {
										"type": "projecte:item",
										"id": "gold_ingot"
									}
								},
								{
									"output": {
										"type": "projecte:item",
										"id": "copper_ingot"
									},
									"count": 3,
									"ingredients": [
										{
											"type": "projecte:item",
											"id": "minecraft:stone",
											"amount": 3
										}
									]
								},
								{
									"output": {
										"type": "projecte:item",
										"id": "diamond"
									},
									"ingredients": [
										{
											"type": "projecte:item",
											"id": "granite",
											"amount": 2
										},
										{
											"type": "projecte:item",
											"id": "minecraft:INVALID|stone",
											"amount": 1
										}
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
			CustomConversion conversion = conversions.getFirst();
			Assertions.assertEquals(NSSItem.createItem(Items.IRON_INGOT), conversion.output());
			Assertions.assertEquals(1, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(3, ingredients.size());
			Assertions.assertEquals(1, ingredients.getInt(NSSItem.createItem(Items.STONE)));
			Assertions.assertEquals(2, ingredients.getInt(NSSItem.createItem(Items.GRANITE)));
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.DIORITE)));
		}
		{
			CustomConversion conversion = conversions.get(1);
			Assertions.assertEquals(NSSItem.createItem(Items.COPPER_INGOT), conversion.output());
			Assertions.assertEquals(3, conversion.count());
			Object2IntMap<NormalizedSimpleStack> ingredients = conversion.ingredients();
			Assertions.assertEquals(1, ingredients.size());
			Assertions.assertEquals(3, ingredients.getInt(NSSItem.createItem(Items.STONE)));
		}
	}
}
