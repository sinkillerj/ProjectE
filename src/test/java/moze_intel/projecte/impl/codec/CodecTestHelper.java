package moze_intel.projecte.impl.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.io.StringReader;
import java.util.function.Function;

public class CodecTestHelper {

	public static void initBuiltinNSS() {
		PECodecHelper.initBuiltinNSS();
	}

	public static <OBJ> OBJ parseJson(Codec<OBJ> codec, String description, String json) throws JsonParseException {
		return readJson(new StringReader(json), codec, description);
	}

	public static <OBJ> OBJ readJson(Reader reader, Codec<OBJ> codec, String description) throws JsonParseException {
		//Similar to PECodecHelper#read except without any extra logging
		JsonElement json = JsonParser.parseReader(reader);
		return codec.parse(JsonOps.INSTANCE, json)
				.get()
				.map(Function.identity(), error -> {
					throw new JsonParseException("Failed to deserialize json (" + description + "): " + error.message());
				});
	}
}