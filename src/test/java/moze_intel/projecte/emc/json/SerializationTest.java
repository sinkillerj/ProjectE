package moze_intel.projecte.emc.json;

import com.google.gson.JsonParseException;
import java.util.HashMap;
import java.util.Map;
import moze_intel.projecte.api.nss.NSSCreator;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test Serialization of Normalized Simple Stacks")
class SerializationTest {

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
	@DisplayName("Test Serialization of a valid Item")
	void testValidItemSerialization() {
		Assertions.assertEquals(NSSItem.createItem(new ResourceLocation("minecraft", "dirt")), NSSSerializer.INSTANCE.deserialize("minecraft:dirt"));
	}

	@Test
	@DisplayName("Test Serialization of a valid Item with prefix included")
	void testValidItemSerializationAlt() {
		Assertions.assertEquals(NSSItem.createItem(new ResourceLocation("minecraft", "dirt")), NSSSerializer.INSTANCE.deserialize("ITEM|minecraft:dirt"));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Item")
	void testInvalidItemSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> NSSSerializer.INSTANCE.deserialize("minecraft:Dirt"));
	}

	@Test
	@DisplayName("Test Serialization of an Item with NBT")
	void testItemNBTSerialization() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("my", "tag");
		NSSItem expected = NSSItem.createItem(new ResourceLocation("minecraft", "dirt"), nbt);
		Assertions.assertEquals(expected, NSSSerializer.INSTANCE.deserialize("minecraft:dirt{my: \"tag\"}"));
	}

	@Test
	@DisplayName("Test Serialization of a valid Item Tag")
	void testValidItemTagSerialization() {
		Assertions.assertEquals(NSSItem.createTag(new ResourceLocation("forge", "cobblestone")), NSSSerializer.INSTANCE.deserialize("#forge:cobblestone"));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Item Tag")
	void testInvalidItemTagSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> NSSSerializer.INSTANCE.deserialize("#minecraft:TAG"));
		Assertions.assertThrows(JsonParseException.class, () -> NSSSerializer.INSTANCE.deserialize("#TAG"));
	}

	@Test
	@DisplayName("Test Serialization of an Item Tag with NBT")
	void testItemTagNBTSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> NSSSerializer.INSTANCE.deserialize("#forge:cobblestone{my: \"tag\"}"));
	}

	@Test
	@DisplayName("Test Serialization of a valid Fluid")
	void testValidFluidSerialization() {
		Assertions.assertEquals(NSSFluid.createFluid(new ResourceLocation("minecraft", "water")), NSSSerializer.INSTANCE.deserialize("FLUID|minecraft:water"));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Fluid")
	void testInvalidFluidSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> NSSSerializer.INSTANCE.deserialize("FLUID|minecraft:Milk"));
	}

	@Test
	@DisplayName("Test Serialization of an Fluid with NBT")
	void testFluidNBTSerialization() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("my", "tag");
		NSSFluid expected = NSSFluid.createFluid(new ResourceLocation("minecraft", "water"), nbt);
		Assertions.assertEquals(expected, NSSSerializer.INSTANCE.deserialize("FLUID|minecraft:water{my: \"tag\"}"));
	}

	@Test
	@DisplayName("Test Serialization of a valid Fluid Tag")
	void testValidFluidTagSerialization() {
		Assertions.assertEquals(NSSFluid.createTag(new ResourceLocation("forge", "milk")), NSSSerializer.INSTANCE.deserialize("FLUID|#forge:milk"));
	}

	@Test
	@DisplayName("Test Serialization of an invalid Fluid Tag")
	void testInvalidFluidTagSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> NSSSerializer.INSTANCE.deserialize("FLUID|#fore:Milk"));
		Assertions.assertThrows(JsonParseException.class, () -> NSSSerializer.INSTANCE.deserialize("FLUID|#TAG"));
	}

	@Test
	@DisplayName("Test Serialization of a Fluid Tag with NBT")
	void testFluidTagNBTSerialization() {
		Assertions.assertThrows(JsonParseException.class, () -> NSSSerializer.INSTANCE.deserialize("FLUID|#forge:milk{my: \"tag\"}"));
	}

	@Test
	@DisplayName("Test Serialization of a FAKE entry")
	void testFake() {
		Assertions.assertEquals(NSSFake.create("MyFakeEntry"), NSSSerializer.INSTANCE.deserialize("FAKE|MyFakeEntry"));
	}

	@Test
	@DisplayName("Test Serialization of an invalid type")
	void testInvalid() {
		Assertions.assertThrows(JsonParseException.class, () -> NSSSerializer.INSTANCE.deserialize("INVALID|minecraft:test"));
	}
}