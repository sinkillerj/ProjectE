package moze_intel.projecte.impl.codec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectERegistries;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.codec.MapProcessor;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import org.jetbrains.annotations.Nullable;

public class PECodecHelper implements IPECodecHelper {

	private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Codec<ItemStack> LENIENT_STACK_CODEC = ItemStack.CODEC.promotePartial(error -> PECore.LOGGER.error("Tried to load invalid item: '{}'", error));
	//Based off of ItemStack#OPTIONAL_CODEC
	private static final Codec<ItemStack> LENIENT_OPTIONAL_STACK_CODEC = ExtraCodecs.optionalEmptyMap(LENIENT_STACK_CODEC.orElse(ItemStack.EMPTY)).xmap(
			stack -> stack.orElse(ItemStack.EMPTY),
			stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack)
	);

	public static final Codec<PEItemStacksHandler> MUTABLE_HANDLER_CODEC = LENIENT_OPTIONAL_STACK_CODEC.listOf().flatComapMap(
			list -> {
				NonNullList<ItemStack> itemList = NonNullList.createWithCapacity(list.size());
				itemList.addAll(list);
				return new PEItemStacksHandler(itemList);
			}, handler -> DataResult.success(handler.copyToList().stream().toList()));

	private final Codec<Long> NON_NEGATIVE_LONG = longRangeWithMessage(0, Long.MAX_VALUE, value -> "Value must be non-negative: " + value);
	private final Codec<Long> POSITIVE_LONG = longRangeWithMessage(1, Long.MAX_VALUE, value -> "Value must be positive: " + value);
	private final Codec<BigInteger> BIG_INT = Codec.STRING.xmap(val -> val.isEmpty() ? BigInteger.ZERO : new BigInteger(val), BigInteger::toString);
	private final Codec<BigInteger> NON_NEGATIVE_BIG_INT = bigIntRangeWithMessage(BigInteger.ZERO, null, value -> "Value must be non-negative: " + value);
	private final Codec<BigInteger> POSITIVE_BIG_INT = bigIntRangeWithMessage(BigInteger.ONE, null, value -> "Value must be non-negative: " + value);

	private final MapCodec<NormalizedSimpleStack> NSS_MAP_CODEC = ProjectERegistries.NSS_SERIALIZER.byNameCodec().dispatchMap(
			NormalizedSimpleStack::codec, Function.identity()
	);
	private final Codec<NormalizedSimpleStack> NSS_CODEC = NSS_MAP_CODEC.codec();

	@Override
	public Codec<NormalizedSimpleStack> nssCodec() {
		return NSS_CODEC;
	}

	@Override
	public MapCodec<NormalizedSimpleStack> nssMapCodec() {
		return NSS_MAP_CODEC;
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
		return Codec.LONG.validate(
				value -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0
						 ? DataResult.success(value)
						 : DataResult.error(() -> errorMessage.apply(value))
		);
	}

	@Override
	public Codec<BigInteger> nonNegativeBigInt() {
		return NON_NEGATIVE_BIG_INT;
	}

	@Override
	public Codec<BigInteger> positiveBigInt() {
		return POSITIVE_BIG_INT;
	}

	@Override
	public Codec<BigInteger> bigIntRangeWithMessage(@Nullable BigInteger min, @Nullable BigInteger max, Function<BigInteger, String> errorMessage) {
		if (min == null && max == null) {
			return BIG_INT;
		}
		return BIG_INT.validate(value -> {
			if ((min == null || value.compareTo(min) >= 0) &&
				(max == null || value.compareTo(max) <= 0)) {
				return DataResult.success(value);
			}
			return DataResult.error(() -> errorMessage.apply(value));
		});
	}

	@Override
	public <K, V> Codec<Map<K, V>> lenientKeyUnboundedMap(MapCodec<K> keyCodec, MapCodec<V> elementCodec, MapProcessor<K, V> processor) {
		return PEUnboundedMapCodec.create(keyCodec, elementCodec, processor, true);
	}

	@Override
	public <K, V> Codec<Map<K, V>> unboundedMap(MapCodec<K> keyCodec, MapCodec<V> elementCodec, MapProcessor<K, V> processor) {
		return PEUnboundedMapCodec.create(keyCodec, elementCodec, processor, false);
	}

	@Override
	public <TYPE> MapCodec<TYPE> orElseWithLog(MapCodec<TYPE> codec, TYPE fallback, Supplier<String> onError) {
		return codec.mapResult(new ResultFunction<>() {//Like orElse except logs the error
			@Override
			public <T> DataResult<TYPE> apply(DynamicOps<T> ops, MapLike<T> input, DataResult<TYPE> result) {
				if (result.isError()) {
					PECore.LOGGER.error(onError.get(), result.error().orElseThrow().message());
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
				return "projecte:OrElseWithLog[" + onError + " " + fallback + "]";
			}
		});
	}

	public static <TYPE> void writeToFile(HolderLookup.Provider registries, Path path, Codec<TYPE> codec, TYPE value, String fileDescription) {
		DataResult<JsonElement> result = codec.encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), value);
		if (result.isError()) {
			PECore.LOGGER.error("Failed to convert {} to json: {}", fileDescription, result.error().orElseThrow().message());
			return;
		}
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			PRETTY_GSON.toJson(result.getOrThrow(), writer);
		} catch (IOException e) {
			PECore.LOGGER.error("Failed to write {} file: {}", fileDescription, path, e);
		}
	}

	public static <TYPE> Optional<TYPE> readFromFile(HolderLookup.Provider registries, Path path, Codec<TYPE> codec, String fileDescription) {
		if (Files.exists(path)) {
			try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				return read(registries, reader, codec, fileDescription);
			} catch (IOException e) {
				PECore.LOGGER.error("Couldn't access {} file: {}", fileDescription, path, e);
			}
		}
		return Optional.empty();
	}

	public static <TYPE> Optional<TYPE> read(HolderLookup.Provider registryAccess, Reader reader, Codec<TYPE> codec, String description) {
		return read(registryAccess.createSerializationContext(JsonOps.INSTANCE), reader, codec, description);
	}

	public static <TYPE> Optional<TYPE> read(DynamicOps<JsonElement> ops, Reader reader, Codec<TYPE> codec, String description) {
		JsonElement json;
		try {
			json = JsonParser.parseReader(reader);
		} catch (JsonParseException e) {
			PECore.LOGGER.error("Couldn't parse {}", description, e);
			return Optional.empty();
		}
		DataResult<TYPE> result = codec.parse(ops, json);
		if (result.isError()) {
			PECore.LOGGER.error("Couldn't parse {}: {}", description, result.error().orElseThrow().message());
			return Optional.empty();
		}
		return result.result();
	}
}
