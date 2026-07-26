package moze_intel.projecte.emc;

import com.google.gson.JsonParseException;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.impl.codec.CodecTestHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

//TODO: Add some tests that actually test serialization as all of these (and the ones for the other tests) only test deserialization
@ExtendWith(EphemeralTestServerProvider.class)
@DisplayName("Test Serialization of Normalized Simple Stacks")
class SerializationTest {

	@AfterEach
	void resetFakeNamespace() {
		NSSFake.resetNamespace();
	}

	private static NormalizedSimpleStack parseJson(HolderLookup.Provider registryAccess, String json) {
		return CodecTestHelper.parseJson(registryAccess, IPECodecHelper.INSTANCE.nssCodec(), "serialization test", json);
	}

	@Test
	@DisplayName("Test Serialization of a valid Item")
	void testValidItemSerialization(MinecraftServer server) {
		Assertions.assertEquals(NSSItem.createItem(Items.DIRT), parseJson(server.registryAccess(), """
				{
					"type": "projecte:item",
					"id": "minecraft:dirt"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Item")
	void testInvalidItemSerialization(MinecraftServer server) {
		Assertions.assertThrows(JsonParseException.class, () -> parseJson(server.registryAccess(), """
				{
					"type": "projecte:item",
					"id": "minecraft:Dirt"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an Item with Data Components")
	void testItemDCSerialization(MinecraftServer server) {
		NSSItem expected = NSSItem.createItem(Items.DIRT, CodecTestHelper.MY_TAG_PATCH);
		Assertions.assertEquals(expected, parseJson(server.registryAccess(), """
				{
					"type": "projecte:item",
					"id": "minecraft:dirt",
					"data": {
						"custom_data": "{my: \\"tag\\"}"
					}
				}"""));
		//Alternate data component format
		Assertions.assertEquals(expected, parseJson(server.registryAccess(), """
				{
					"type": "projecte:item",
					"id": "minecraft:dirt",
					"data": {
						"custom_data": {
							"my": "tag"
						}
					}
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a valid Item Tag")
	void testValidItemTagSerialization(MinecraftServer server) {
		Assertions.assertEquals(NSSItem.createTag(Tags.Items.COBBLESTONES), parseJson(server.registryAccess(), """
				{
					"type": "projecte:item",
					"tag": "c:cobblestones"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Item Tag")
	void testInvalidItemTagSerialization(MinecraftServer server) {
		Assertions.assertThrows(JsonParseException.class, () -> parseJson(server.registryAccess(), """
				{
					"type": "projecte:item",
					"tag": "minecraft:TAG"
				}"""));
		//Explicit with # (which makes it invalid)
		Assertions.assertThrows(JsonParseException.class, () -> parseJson(server.registryAccess(), """
				{
					"type": "projecte:item",
					"tag": "#c:cobblestones"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an Explicit Item Tag with Data Components")
	void testExplicitItemTagDCSerialization(MinecraftServer server) {
		//The tag is ignored
		NSSItem expected = NSSItem.createTag(Tags.Items.COBBLESTONES);
		Assertions.assertEquals(expected, parseJson(server.registryAccess(), """
				{
					"type": "projecte:item",
					"tag": "c:cobblestones",
					"data": {
						"custom_data": {
							"my": "tag"
						}
					}
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a valid Fluid")
	void testValidFluidSerialization(MinecraftServer server) {
		Assertions.assertEquals(NSSFluid.createFluid(Fluids.WATER), parseJson(server.registryAccess(), """
				{
					"type": "projecte:fluid",
					"id": "minecraft:water"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Fluid")
	void testInvalidFluidSerialization(MinecraftServer server) {
		Assertions.assertThrows(JsonParseException.class, () -> parseJson(server.registryAccess(), """
				{
					"type": "projecte:fluid",
					"id": "minecraft:Milk"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a Fluid with Data Components")
	void testFluidDCSerialization(MinecraftServer server) {
		NSSFluid expected = NSSFluid.createFluid(Fluids.WATER, CodecTestHelper.MY_TAG_PATCH);
		Assertions.assertEquals(expected, parseJson(server.registryAccess(), """
				{
					"type": "projecte:fluid",
					"id": "minecraft:water",
					"data": {
						"custom_data": "{my: \\"tag\\"}"
					}
				}"""));
		//Alternate data component format
		Assertions.assertEquals(expected, parseJson(server.registryAccess(), """
				{
					"type": "projecte:fluid",
					"id": "minecraft:water",
					"data": {
						"custom_data": {
							"my": "tag"
						}
					}
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a valid Fluid Tag")
	void testValidFluidTagSerialization(MinecraftServer server) {
		Assertions.assertEquals(NSSFluid.createTag(Tags.Fluids.MILK), parseJson(server.registryAccess(), """
				{
					"type": "projecte:fluid",
					"tag": "c:milk"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Fluid Tag")
	void testInvalidFluidTagSerialization(MinecraftServer server) {
		Assertions.assertThrows(JsonParseException.class, () -> parseJson(server.registryAccess(), """
				{
					"type": "projecte:fluid",
					"tag": "minecraft:Milk"
				}"""));
		//Explicit with # (which makes it invalid)
		Assertions.assertThrows(JsonParseException.class, () -> parseJson(server.registryAccess(), """
				{
					"type": "projecte:fluid",
					"tag": "#c:milk"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a Fluid Tag with Data Components")
	void testFluidTagDCSerialization(MinecraftServer server) {
		//The component data is ignored
		Assertions.assertEquals(NSSFluid.createTag(Tags.Fluids.MILK), parseJson(server.registryAccess(), """
				{
					"type": "projecte:fluid",
					"tag": "c:milk",
					"data": {
						"custom_data": {
							"my": "tag"
						}
					}
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a FAKE entry")
	void testFake(MinecraftServer server) {
		NSSFake expected = NSSFake.create("MyFakeEntry");
		Assertions.assertEquals(expected, parseJson(server.registryAccess(), """
				{
					"type": "projecte:fake",
					"description": "MyFakeEntry"
				}"""));
		//Optional namespace
		NormalizedSimpleStack withNameSpace = parseJson(server.registryAccess(), """
				{
					"type": "projecte:fake",
					"namespace": "test",
					"description": "MyFakeEntry"
				}""");
		Assertions.assertNotEquals(expected, withNameSpace);
		NSSFake.setCurrentNamespace("test");
		//Recreate the expected to make sure it is set with the correct namespace
		NSSFake expectedWithNamespace = NSSFake.create("MyFakeEntry");
		Assertions.assertEquals(expectedWithNamespace, withNameSpace);
		//Test it without the namespace being present but having set the namespace for NSSFake's instead
		Assertions.assertEquals(expectedWithNamespace, parseJson(server.registryAccess(), """
				{
					"type": "projecte:fake",
					"description": "MyFakeEntry"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a FAKE entry with an explicitly empty namespace")
	void testFakeEmptyNamespace(MinecraftServer server) {
		Assertions.assertThrows(JsonParseException.class, () -> parseJson(server.registryAccess(), """
				{
					"type": "projecte:fake",
					"namespace": "",
					"description": "MyFakeEntry"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an invalid type")
	void testInvalid(MinecraftServer server) {
		Assertions.assertThrows(JsonParseException.class, () -> parseJson(server.registryAccess(), """
				{
					"type": "projecte:invalid",
					"id": "minecraft:dirt"
				}"""));
		//Valid type but missing keys for said type
		Assertions.assertThrows(JsonParseException.class, () -> parseJson(server.registryAccess(), """
				{
					"type": "projecte:item",
					"wrong_id": "minecraft:water"
				}"""));
	}
}
