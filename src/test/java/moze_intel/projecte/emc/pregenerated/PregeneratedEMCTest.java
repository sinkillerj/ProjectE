package moze_intel.projecte.emc.pregenerated;

import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.impl.codec.CodecTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Pregenerated EMC Serialization")
class PregeneratedEMCTest {

	private static final Codec<Map<NSSItem, Long>> CODEC = IPECodecHelper.INSTANCE.lenientKeyUnboundedMap(ExtraCodecs.validate(
			NSSItem.LEGACY_CODEC,
			nss -> nss.representsTag() ? DataResult.error(() -> "ItemInfo does not support tags") : DataResult.success(nss)
	), IPECodecHelper.INSTANCE.positiveLong());

	//TODO - 1.20.4: Switch to ItemInfo once we can actually have registries be loaded for junit purposes
	private static Map<NSSItem, Long> parseJson(String json) {
		//return CodecTestHelper.parseJson(PregeneratedEMC.CODEC, "pregnerated emc test", json);
		return CodecTestHelper.parseJson(CODEC, "pregnerated emc test", json);
	}

	@BeforeAll
	@DisplayName("Manually load the default supported codecs")
	static void setupBuiltinCodecs() {
		//Registry init does not happen for tests, so we need to manually add our codecs
		CodecTestHelper.initBuiltinNSS();
	}

	@Test
	@DisplayName("Test empty pregen file")
	void testEmptyPregenFile() {
		Map<NSSItem, Long> pregenerated = parseJson("{}");
		Assertions.assertEquals(0, pregenerated.size());
	}

	@Test
	@DisplayName("Test a simple pregen file")
	void testSimplePregenFile() {
		Map<NSSItem, Long> pregenerated = parseJson("""
				{
					"minecraft:dirt": 1
				}""");
		Assertions.assertEquals(1, pregenerated.size());
		//Assertions.assertEquals(1, pregenerated.get(ItemInfo.fromItem(Items.DIRT)));
		Assertions.assertEquals(1, pregenerated.get(NSSItem.createItem(new ResourceLocation("dirt"))));
	}

	@Test
	@DisplayName("Test pregen file with long values")
	void testPregenFileLongValues() {
		Map<NSSItem, Long> pregenerated = parseJson("""
				{
					"minecraft:dirt": 2147483648
				}""");
		Assertions.assertEquals(1, pregenerated.size());
		//Max int + 1
		//Assertions.assertEquals(2_147_483_648L, pregenerated.get(ItemInfo.fromItem(Items.DIRT)));
		Assertions.assertEquals(2_147_483_648L, pregenerated.get(NSSItem.createItem(new ResourceLocation("dirt"))));
	}

	@Test
	@DisplayName("Test pregen file with keys that contain nbt")
	void testPregenFileWithNbt() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("my", "tag");
		Map<NSSItem, Long> pregenerated = parseJson("""
				{
					"minecraft:dirt{my: \\"tag\\"}": 1
				}""");
		Assertions.assertEquals(1, pregenerated.size());
		//Assertions.assertEquals(1, pregenerated.get(ItemInfo.fromItem(Items.DIRT, nbt)));
		Assertions.assertEquals(1, pregenerated.get(NSSItem.createItem(new ResourceLocation("dirt"), nbt)));
	}

	@Test
	@DisplayName("Test pregen file with keys that contain empty nbt")
	void testPregenFileWithEmptyNbt() {
		//Empty nbt is ignored and is treated as if it isn't there
		Map<NSSItem, Long> pregenerated = parseJson("""
				{
					"minecraft:dirt{}": 1
				}""");
		Assertions.assertEquals(1, pregenerated.size());
		//Assertions.assertEquals(1, pregenerated.get(ItemInfo.fromItem(Items.DIRT)));
		Assertions.assertEquals(1, pregenerated.get(NSSItem.createItem(new ResourceLocation("dirt"))));
	}

	@Test
	@DisplayName("Test pregen file with invalid value")
	void testPregenFileInvalidValues() {
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"minecraft:dirt": 0
				}"""));
		Assertions.assertThrows(JsonParseException.class, () -> parseJson("""
				{
					"minecraft:dirt": -1
				}"""));
	}

	@Test
	@DisplayName("Test pregen file with invalid keys")
	void testPregenFileInvalidKeys() {
		//Test to ensure we skip over any invalid keys rather than throwing an exception and failing to deserialize anything
		//TODO - 1.20.4: When switching to ItemInfo remove the comma from projecte:invalid
		Map<NSSItem, Long> pregenerated = parseJson("""
				{
					"minecraft:dirt": 1,
					"projecte:invalid,": 2,
					"minecraft:stone": 3,
					"INVALID": 4
				}""");
		Assertions.assertEquals(2, pregenerated.size());
		//Assertions.assertEquals(1, pregenerated.get(ItemInfo.fromItem(Items.DIRT)));
		//Assertions.assertEquals(3, pregenerated.get(ItemInfo.fromItem(Items.STONE)));
		Assertions.assertEquals(1, pregenerated.get(NSSItem.createItem(new ResourceLocation("dirt"))));
		Assertions.assertEquals(3, pregenerated.get(NSSItem.createItem(new ResourceLocation("stone"))));
	}
}