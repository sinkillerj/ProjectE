package moze_intel.projecte.impl.codec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapCodec.ResultFunction;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.codec.NSSCodecHolder;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.VisibleForTesting;

public class PECodecHelper implements IPECodecHelper {

	private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Codec<Long> NON_NEGATIVE_LONG = longRangeWithMessage(0, Long.MAX_VALUE, value -> "Value must be non-negative: " + value);
	private final Codec<Long> POSITIVE_LONG = longRangeWithMessage(1, Long.MAX_VALUE, value -> "Value must be positive: " + value);

	private final Map<String, Codec<? extends NormalizedSimpleStack>> nssLegacyCodecs = new HashMap<>();
	private Codec<NSSCodecHolder<?>> nssSerializerCodec;

	@SuppressWarnings("unchecked")
	private final Codec<NormalizedSimpleStack> LEGACY_NSS_CODEC = new Codec<>() {
		@Override
		public <T> DataResult<Pair<NormalizedSimpleStack, T>> decode(DynamicOps<T> ops, T input) {
			return ExtraCodecs.NON_EMPTY_STRING.decode(ops, input).flatMap(p -> {
				String[] parts = p.getFirst().split("\\|", 2);
				String prefix = parts.length == 2 ? parts[0] : NSSItem.CODECS.legacyPrefix();
				Codec<NormalizedSimpleStack> codec = (Codec<NormalizedSimpleStack>) nssLegacyCodecs.get(prefix);
				if (codec == null) {
					return DataResult.error(() -> "Unknown legacy prefix: " + prefix);
				}
				return codec.decode(ops, input);
			});
		}

		@Override
		public <T> DataResult<T> encode(NormalizedSimpleStack input, DynamicOps<T> ops, T prefix) {
			Codec<NormalizedSimpleStack> inputCodec = (Codec<NormalizedSimpleStack>) input.codecs().legacy();
			return inputCodec.encode(input, ops, prefix);
		}

		@Override
		public String toString() {
			return "projecte:legacy_normalized_simple_stack";
		}
	};

	private final Codec<NormalizedSimpleStack> EXPLICIT_NSS_CODEC = ExtraCodecs.lazyInitializedCodec(() -> nssSerializerCodec.dispatch(NormalizedSimpleStack::codecs, NSSCodecHolder::explicit));

	private final Codec<NormalizedSimpleStack> NSS_CODEC = NeoForgeExtraCodecs.withAlternative(LEGACY_NSS_CODEC, EXPLICIT_NSS_CODEC);

	@VisibleForTesting
	static void initBuiltinNSS() {
		PECodecHelper instance = (PECodecHelper) INSTANCE;
		instance.nssLegacyCodecs.put(NSSFake.CODECS.legacyPrefix(), NSSFake.CODECS.legacy());
		instance.nssLegacyCodecs.put(NSSItem.CODECS.legacyPrefix(), NSSItem.CODECS.legacy());
		instance.nssLegacyCodecs.put(NSSFluid.CODECS.legacyPrefix(), NSSFluid.CODECS.legacy());
		ResourceLocation item = PECore.rl("item");
		ResourceLocation fluid = PECore.rl("fluid");
		ResourceLocation fake = PECore.rl("fake");
		instance.nssSerializerCodec = ResourceLocation.CODEC.flatXmap(id -> {
			if (id.equals(item)) {
				return DataResult.success(NSSItem.CODECS);
			} else if (id.equals(fluid)) {
				return DataResult.success(NSSFluid.CODECS);
			} else if (id.equals(fake)) {
				return DataResult.success(NSSFake.CODECS);
			}
			return DataResult.error(() -> "Unknown builtin NSS serializer");
		}, codecHolder -> {
			if (codecHolder == NSSItem.CODECS) {
				return DataResult.success(item);
			} else if (codecHolder == NSSFluid.CODECS) {
				return DataResult.success(fluid);
			} else if (codecHolder == NSSFake.CODECS) {
				return DataResult.success(fake);
			}
			return DataResult.error(() -> "Unknown builtin NSS serializer");
		});
	}

	@Override
	public void setSerializers(Registry<NSSCodecHolder<?>> registry) {
		if (nssSerializerCodec != null || !nssLegacyCodecs.isEmpty()) {
			//TODO - 1.20.4: Figure out if this causes issues due to the fact I think baking might also happen to some degree on registry id sync
			throw new IllegalStateException("This method may only be called once");
		}
		for (NSSCodecHolder<?> codecHolder : registry) {
			nssLegacyCodecs.put(codecHolder.legacyPrefix(), codecHolder.legacy());
		}
		nssSerializerCodec = registry.byNameCodec();
	}

	@Override
	public Codec<NormalizedSimpleStack> legacyNSSCodec() {
		return LEGACY_NSS_CODEC;
	}

	@Override
	public Codec<NormalizedSimpleStack> explicitNSSCodec() {
		return EXPLICIT_NSS_CODEC;
	}

	@Override
	public Codec<NormalizedSimpleStack> nssCodec() {
		return NSS_CODEC;
	}

	@Override
	public Codec<Long> nonNegativeLong() {
		return NON_NEGATIVE_LONG;
	}

	@Override
	public Codec<Long> positiveLong() {
		return POSITIVE_LONG;
	}

	@Override
	public Codec<Long> longRangeWithMessage(long min, long max, Function<Long, String> errorMessage) {
		return ExtraCodecs.validate(
				Codec.LONG,
				value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0
						 ? DataResult.success(value)
						 : DataResult.error(() -> errorMessage.apply(value))
		);
	}

	@Override
	public Codec<String> withPrefix(String prefix) {
		return ExtraCodecs.NON_EMPTY_STRING.comapFlatMap(str -> {
			if (str.startsWith(prefix)) {
				return DataResult.success(str.substring(prefix.length()));
			}
			return DataResult.error(() -> "Does not start with " + prefix);
		}, str -> prefix + str);
	}

	@Override
	public <K, V> Codec<Map<K, V>> lenientKeyUnboundedMap(Codec<K> keyCodec, Codec<V> elementCodec) {
		return new LenientKeyUnboundedMapCodec<>(keyCodec, elementCodec);
	}

	@Override
	public <TYPE> MapCodec<TYPE> orElseWithLog(MapCodec<TYPE> codec, TYPE fallback, Supplier<String> onError) {
		return codec.mapResult(new ResultFunction<>() {//Like orElse except logs the error
			@Override
			public <T> DataResult<TYPE> apply(DynamicOps<T> ops, MapLike<T> input, DataResult<TYPE> result) {
				if (result.error().isPresent()) {
					PECore.LOGGER.error(onError.get(), result.error().get().message());
					//If there is a key that is not serializable promote it to an invalid object. This will be filtered out before converting to a map
					// but allows for us to collect and see what errors might exist in the values
					return DataResult.success(fallback);
				}
				return result;
			}

			@Override
			public <T> RecordBuilder<T> coApply(DynamicOps<T> ops, TYPE input, RecordBuilder<T> builder) {
				return builder;
			}

			@Override
			public String toString() {
				//TODO: Once junit is supported by Neo we should make this also include the fallback in the toString similar to the base MapCodec#orElse
				return "projecte:OrElseWithLog[" + onError + "]";
			}
		});
	}

	public static <TYPE> void writeToFile(Path path, Codec<TYPE> codec, TYPE value, String fileDescription) {
		DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, value);
		Optional<PartialResult<JsonElement>> error = result.error();
		if (error.isPresent()) {
			PECore.LOGGER.error("Failed to convert {} to json: {}", fileDescription, error.get().message());
			return;
		}
		JsonElement json = result.result().orElseThrow();
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			PRETTY_GSON.toJson(json, writer);
		} catch (IOException e) {
			PECore.LOGGER.error("Failed to write {} file: {}", fileDescription, path, e);
		}
	}

	public static <TYPE> Optional<TYPE> readFromFile(Path path, Codec<TYPE> codec, String fileDescription) {
		if (Files.exists(path)) {
			try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				return read(reader, codec, fileDescription);
			} catch (IOException e) {
				PECore.LOGGER.error("Couldn't access {} file: {}", fileDescription, path, e);
			}
		}
		return Optional.empty();
	}

	public static <TYPE> Optional<TYPE> read(Reader reader, Codec<TYPE> codec, String description) {
		JsonElement json;
		try {
			json = JsonParser.parseReader(reader);
		} catch (JsonParseException e) {
			PECore.LOGGER.error("Couldn't parse {}", description, e);
			return Optional.empty();
		}
		DataResult<TYPE> result = codec.parse(JsonOps.INSTANCE, json);
		if (result.error().isPresent()) {
			PECore.LOGGER.error("Couldn't parse {}: {}", description, result.error().get().message());
			return Optional.empty();
		}
		return result.result();
	}
}