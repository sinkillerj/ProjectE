package moze_intel.projecte.impl.codec;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.BaseMapCodec;
import java.util.Map;
import moze_intel.projecte.PECore;

public record LenientKeyUnboundedMapCodec<K, V>(Codec<K> keyCodec, Codec<V> elementCodec) implements BaseMapCodec<K, V>, Codec<Map<K, V>> {

	//Based on BaseMapCodec#decode and LenientUnboundedMapCodec
	@Override
	public <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input) {
		final ImmutableMap.Builder<K, V> read = ImmutableMap.builder();
		final ImmutableList.Builder<Pair<T, T>> failed = ImmutableList.builder();

		final DataResult<Unit> result = input.entries().reduce(
				DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
				(r, pair) -> {
					final DataResult<K> keyResult = keyCodec().parse(ops, pair.getFirst());
					if (keyResult.error().isPresent()) {
						PECore.LOGGER.error("Unable to deserialize key: {}", keyResult.error().get().message());
						return r;//Skip this key as it is invalid (potentially representing something unloaded)
					}
					final DataResult<V> valueResult = elementCodec().parse(ops, pair.getSecond());
					valueResult.error().ifPresent(e -> failed.add(pair));
					return r.apply3((u, key, value) -> {
						read.put(key, value);
						return u;
					}, keyResult, valueResult);
				},
				(r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
		);

		final Map<K, V> elements = read.build();
		final T errors = ops.createMap(failed.build().stream());

		return result.map(unit -> elements).setPartial(elements).mapError(e -> e + "; missed input: " + errors);
	}

	@Override
	public <T> DataResult<Pair<Map<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
		//[VanillaCopy] UnboundedMapCodec#decode(DynamicOps, T)
		return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap(map -> decode(ops, map)).map(r -> Pair.of(r, input));
	}

	@Override
	public <T> DataResult<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final T prefix) {
		//[VanillaCopy] UnboundedMapCodec#encode(Map, DynamicOps, T)
		return encode(input, ops, ops.mapBuilder()).build(prefix);
	}

	@Override
	public String toString() {
		return "LenientKeyUnboundedMapCodec[" + keyCodec + " -> " + elementCodec + ']';
	}
}
