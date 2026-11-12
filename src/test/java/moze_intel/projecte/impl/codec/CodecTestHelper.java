package moze_intel.projecte.impl.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import net.minecraft.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;

public class CodecTestHelper {

	public static final DataComponentPatch MY_TAG_PATCH = Util.make(() -> {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("my", "tag");
		return DataComponentPatch.builder()
				.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt))
				.build();
	});

	/**
	 * @apiNote Similar to {@link PECodecHelper#read(Provider, Reader, Codec, String)} except without any extra logging.
	 */
	public static <OBJ> OBJ parseJson(HolderLookup.Provider registryAccess, Codec<OBJ> codec, String description, String rawJson) throws JsonParseException {
		JsonElement json = JsonParser.parseString(rawJson);
		return codec.parse(registryAccess.createSerializationContext(JsonOps.INSTANCE), json)
				.getOrThrow(error -> new JsonParseException("Failed to deserialize json (" + description + "): " + error));
	}
}
