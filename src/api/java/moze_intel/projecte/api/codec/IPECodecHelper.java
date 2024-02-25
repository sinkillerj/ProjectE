package moze_intel.projecte.api.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface IPECodecHelper {

	/**
	 * Helper for dealing with {@link Codec Codecs} related to ProjectE.
	 */
	IPECodecHelper INSTANCE = ServiceLoader.load(IPECodecHelper.class).findFirst()
			.orElseThrow(() -> new IllegalStateException("No valid ServiceImpl for IPECodecHelper found, ProjectE may be absent, damaged, or outdated"));

	/**
	 * <STRONG>DO NOT CALL THIS METHOD</STRONG>
	 * <p>
	 * Called after the {@link moze_intel.projecte.api.ProjectERegistries#NSS_SERIALIZER} registry is baked to initialize the legacy codecs.
	 *
	 * @apiNote This method is for internal use only.
	 */
	@Internal
	void setSerializers(Registry<NSSCodecHolder<?>> registry);

	/**
	 * A legacy codec capable of reading and writing {@link NormalizedSimpleStack NormalizedSimpleStacks} to/from strings.
	 */
	Codec<NormalizedSimpleStack> legacyNSSCodec();

	/**
	 * An explicit codec capable of reading and writing {@link NormalizedSimpleStack NormalizedSimpleStacks} to/from strings.
	 */
	Codec<NormalizedSimpleStack> explicitNSSCodec();

	/**
	 * Alternative {@link Codec} that tries to encode and decode first using a {@link #legacyNSSCodec()} followed by an {@link #explicitNSSCodec()} if decoding as legacy
	 * failed.
	 */
	Codec<NormalizedSimpleStack> nssCodec();

	/**
	 * {@return Long Codec that validates the long is greater than or equal to zero}
	 */
	Codec<Long> nonNegativeLong();

	/**
	 * {@return Long Codec that validates the long is greater than zero}
	 */
	Codec<Long> positiveLong();

	/**
	 * {@return Long Codec that validates the long is within the range, and if not produces the given error}
	 *
	 * @param min          Min value inclusive.
	 * @param max          Max value inclusive.
	 * @param errorMessage Error message producer.
	 */
	Codec<Long> longRangeWithMessage(long min, long max, Function<Long, String> errorMessage);

	/**
	 * Helper to produce a string Codec that prepends the given prefix when serializing and validates it is present and then trims it when deserializing.
	 *
	 * @param prefix A string representing the prefix to use for serialization. Must end with '|' to properly work. Anything without a '|' is assumed to be an item.
	 */
	Codec<String> withPrefix(String prefix);

	/**
	 * Helper to create a Codec for a map that logs an error but does not fail on invalid keys. Invalid values still cause a failure though.
	 *
	 * @param keyCodec     Codec to serialize the keys with.
	 * @param elementCodec Codec to serialize the values with.
	 */
	<K, V> Codec<Map<K, V>> lenientKeyUnboundedMap(Codec<K> keyCodec, Codec<V> elementCodec);

	/**
	 * Helper to validate that the element being passed into the codec is not null and if it is produce the given error.
	 *
	 * @param codec        Codec
	 * @param errorMessage Error message to produce if the element is null.
	 */
	default <T> Codec<T> validatePresent(Codec<T> codec, Supplier<String> errorMessage) {
		return ExtraCodecs.validate(codec, t -> t == null ? DataResult.error(errorMessage) : DataResult.success(t));
	}

	/**
	 * Similar to {@link MapCodec#orElse(Consumer, Object)} but logs the error instead of just remapping it on the result
	 *
	 * @param codec    Codec
	 * @param fallback Fallback value for if an error is encountered.
	 * @param onError  Supplier providing the string to log. Should contain {@code {}} to include the data result's error.
	 */
	<T> MapCodec<T> orElseWithLog(MapCodec<T> codec, T fallback, Supplier<String> onError);

	/**
	 * Helper to convert a codec of maps to one that decodes into {@link HashMap mutable maps}.
	 *
	 * @param codec Base codec for serializing and deserializing a map.
	 *
	 * @implNote Only modifies the decoding and leaves encoding alone.
	 */
	default <K, V> Codec<Map<K, V>> modifiableMap(Codec<Map<K, V>> codec) {
		return modifiableMap(codec, HashMap::new);
	}

	/**
	 * Helper to convert a codec of maps to one that decodes into mutable maps.
	 *
	 * @param codec          Base codec for serializing and deserializing a map.
	 * @param mapConstructor Converts the immutable map to a mutable one.
	 *
	 * @implNote Only modifies the decoding and leaves encoding alone.
	 */
	default <K, V> Codec<Map<K, V>> modifiableMap(Codec<Map<K, V>> codec, UnaryOperator<Map<K, V>> mapConstructor) {
		return Codec.of(codec, codec.map(mapConstructor));
	}

	/**
	 * Helper method to wrap a collection in an optional if it is not empty.
	 *
	 * @param collection Collection to check and wrap.
	 */
	default <COLLECTION extends Collection<?>> Optional<COLLECTION> ifNotEmpty(COLLECTION collection) {
		return ifNotEmpty(collection, Collection::isEmpty);
	}

	/**
	 * Helper method to wrap a map in an optional if it is not empty.
	 *
	 * @param map Map to check and wrap.
	 */
	default <MAP extends Map<?, ?>> Optional<MAP> ifNotEmpty(MAP map) {
		return ifNotEmpty(map, Map::isEmpty);
	}

	/**
	 * Helper method to wrap an object in an optional if it is not empty.
	 *
	 * @param obj        Object to check and wrap.
	 * @param emptyCheck Check for if the object is empty.
	 */
	default <OBJ> Optional<OBJ> ifNotEmpty(OBJ obj, Predicate<OBJ> emptyCheck) {
		return emptyCheck.test(obj) ? Optional.empty() : Optional.of(obj);
	}
}