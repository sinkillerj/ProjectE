package moze_intel.projecte.emc;

import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.api.nss.NormalizedSimpleStackTestHelper;
import moze_intel.projecte.impl.codec.CodecTestHelper;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

//TODO - 1.20.4: Add some tests that actually test serialization as all of these (and the ones for the other tests) only test deserialization
@DisplayName("Test Serialization of Normalized Simple Stacks")
class SerializationTest extends NormalizedSimpleStackTestHelper {

	private static NormalizedSimpleStack deserializeLegacyNSS(String jsonString) {
		return Util.getOrThrow(IPECodecHelper.INSTANCE.legacyNSSCodec().parse(JsonOps.INSTANCE, new JsonPrimitive(jsonString)), JsonParseException::new);
	}

	private static NormalizedSimpleStack parseJson(String json) {
		return CodecTestHelper.parseJson(IPECodecHelper.INSTANCE.nssCodec(), "serialization test", json);
	}

	@BeforeAll
	@DisplayName("Manually load the default supported codecs")
	static void setupBuiltinCodecs() {
		//Registry init does not happen for tests, so we need to manually add our codecs
		CodecTestHelper.initBuiltinNSS();
	}

	@Test
	@DisplayName("Test Serialization of a valid Item")
	void testValidItemSerialization() {
		NSSItem expected = createItem("minecraft", "dirt");
		Assertions.assertEquals(expected, deserializeLegacyNSS("minecraft:dirt"));
		//Test explicit syntax
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:item",
					"id": "minecraft:dirt"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a valid Item with prefix included")
	void testValidItemSerializationAlt() {
		Assertions.assertEquals(createItem("minecraft", "dirt"), deserializeLegacyNSS("ITEM|minecraft:dirt"));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Item")
	void testInvalidItemSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> deserializeLegacyNSS("minecraft:Dirt"));
		//Test explicit syntax
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"type": "projecte:item",
					"id": "minecraft:Dirt"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an Item with NBT")
	void testItemNBTSerialization() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("my", "tag");
		NSSItem expected = createItem("minecraft", "dirt", nbt);
		Assertions.assertEquals(expected, deserializeLegacyNSS("minecraft:dirt{my: \"tag\"}"));
		//Test explicit syntax
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:item",
					"id": "minecraft:dirt",
					"nbt": "{my: \\"tag\\"}"
				}"""));
		//Alternate nbt format
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:item",
					"id": "minecraft:dirt",
					"nbt": {
						"my": "tag"
					}
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a valid Item Tag")
	void testValidItemTagSerialization() {
		NSSItem expected = createTag("forge", "cobblestone");
		Assertions.assertEquals(expected, deserializeLegacyNSS("#forge:cobblestone"));
		//Test explicit syntax
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:item",
					"tag": "forge:cobblestone"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Item Tag")
	void testInvalidItemTagSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> deserializeLegacyNSS("#minecraft:TAG"));
		Assertions.assertThrows(JsonParseException.class, () -> deserializeLegacyNSS("#TAG"));
		//Test explicit syntax
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"type": "projecte:item",
					"tag": "minecraft:TAG"
				}"""));
		//Explicit with # (which makes it invalid)
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"type": "projecte:item",
					"tag": "#forge:cobblestone"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an Item Tag with NBT")
	void testItemTagNBTSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> deserializeLegacyNSS("#forge:cobblestone{my: \"tag\"}"));
	}

	@Test
	@DisplayName("Test Serialization of an Explicit Item Tag with NBT")
	void testExplicitItemTagNBTSerialization() {
		//The tag is ignored
		NSSItem expected = createTag("forge", "cobblestone");
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:item",
					"tag": "forge:cobblestone",
					"nbt": {
						"my": "tag"
					}
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a valid Fluid")
	void testValidFluidSerialization() {
		NSSFluid expected = createFluid("minecraft", "water");
		Assertions.assertEquals(expected, deserializeLegacyNSS("FLUID|minecraft:water"));
		//Test explicit syntax
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:fluid",
					"id": "minecraft:water"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Fluid")
	void testInvalidFluidSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> deserializeLegacyNSS("FLUID|minecraft:Milk"));
		//Test explicit syntax
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"type": "projecte:fluid",
					"id": "minecraft:Milk"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a Fluid with NBT")
	void testFluidNBTSerialization() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("my", "tag");
		NSSFluid expected = createFluid("minecraft", "water", nbt);
		Assertions.assertEquals(expected, deserializeLegacyNSS("FLUID|minecraft:water{my: \"tag\"}"));
		//Test explicit syntax
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:fluid",
					"id": "minecraft:water",
					"nbt": "{my: \\"tag\\"}"
				}"""));
		//Alternate nbt format
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:fluid",
					"id": "minecraft:water",
					"nbt": {
						"my": "tag"
					}
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a valid Fluid Tag")
	void testValidFluidTagSerialization() {
		NSSFluid expected = createFluidTag("forge", "milk");
		Assertions.assertEquals(expected, deserializeLegacyNSS("FLUID|#forge:milk"));
		//Test explicit syntax
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:fluid",
					"tag": "forge:milk"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Fluid Tag")
	void testInvalidFluidTagSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> deserializeLegacyNSS("FLUID|#forge:Milk"));
		Assertions.assertThrows(JsonParseException.class, () -> deserializeLegacyNSS("FLUID|#TAG"));
		//Test explicit syntax
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"type": "projecte:fluid",
					"tag": "minecraft:Milk"
				}"""));
		//Explicit with # (which makes it invalid)
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"type": "projecte:fluid",
					"tag": "#forge:milk"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a Fluid Tag with NBT")
	void testFluidTagNBTSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> deserializeLegacyNSS("FLUID|#forge:milk{my: \"tag\"}"));
		//The tag is ignored
		NSSFluid expected = createFluidTag("forge", "milk");
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:fluid",
					"tag": "forge:milk",
					"nbt": {
						"my": "tag"
					}
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a FAKE entry")
	void testFake() {
		NSSFake expected = NSSFake.create("MyFakeEntry");
		Assertions.assertEquals(expected, deserializeLegacyNSS("FAKE|MyFakeEntry"));
		//Test explicit syntax
		Assertions.assertEquals(expected, parseJson("""
				{
					"type": "projecte:fake",
					"description": "MyFakeEntry"
				}"""));
		//Optional namespace
		NormalizedSimpleStack withNameSpace = parseJson("""
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
		Assertions.assertEquals(expectedWithNamespace, parseJson("""
				{
					"type": "projecte:fake",
					"description": "MyFakeEntry"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of a FAKE entry with an explicitly empty namespace")
	void testFakeEmptyNamespace() {
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"type": "projecte:fake",
					"namespace": "",
					"description": "MyFakeEntry"
				}"""));
	}

	@Test
	@DisplayName("Test Serialization of an invalid type")
	void testInvalid() {
		Assertions.assertThrows(JsonParseException.class, () -> deserializeLegacyNSS("INVALID|minecraft:test"));
		//Test explicit syntax
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"type": "projecte:invalid",
					"id": "minecraft:dirt"
				}"""));
		//Valid type but missing keys for said type
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"type": "projecte:item",
					"wrong_id": "minecraft:water"
				}"""));
	}
}