package moze_intel.projecte.api.conversion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.nss.NSSTag;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

/**
 * Represents a conversion
 *
 * @param count         Amount of output this conversion produces (may not be equal to zero)
 * @param output        Output stack
 * @param ingredients   Map of the ingredients and how many of each are necessary to perform the conversion (may not be equal to zero)
 * @param propagateTags if {@code true} and the output is an {@link NSSTag}, this conversion will be propagated to all elements in the tag
 */
public record CustomConversion(int count, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredients, boolean propagateTags) {

	private static final CustomConversion INVALID = new CustomConversion(0, null, Map.of(), false);

	private static final Codec<Integer> NON_ZERO_INT = ExtraCodecs.validate(
			Codec.INT,
			value -> value == 0 ? DataResult.error(() -> "Value must not be zero: " + value) : DataResult.success(value)
	);

	private static final Codec<Map<NormalizedSimpleStack, Integer>> INGREDIENT_CODEC = NeoForgeExtraCodecs.withAlternative(
			ExtraCodecs.nonEmptyList(IPECodecHelper.INSTANCE.nssCodec().listOf()).flatComapMap(
					//Note: During deserialization we allow duplicates and merge them together to form the count,
					// though during serialization we only allow serializing to an array if each ingredient has a count of one
					list -> list.stream().collect(Collectors.toMap(Function.identity(), stack -> 1, Integer::sum)),
					map -> {
						List<NormalizedSimpleStack> list = new ArrayList<>(map.size());
						for (Map.Entry<NormalizedSimpleStack, Integer> entry : map.entrySet()) {
							if (entry.getValue() != 1) {
								return DataResult.error(() -> "Ingredients can only be represented as an array if all elements have an amount of one");
							}
							list.add(entry.getKey());
						}
						return DataResult.success(list);
					}
			), IPECodecHelper.INSTANCE.modifiableMap(ExtraCodecs.validate(
					//Note: We need to use the legacy codec as map keys for json are required to be able to be converted to strings
					Codec.unboundedMap(IPECodecHelper.INSTANCE.legacyNSSCodec(), NON_ZERO_INT),
					map -> map.isEmpty() ? DataResult.error(() -> "Map must have contents") : DataResult.success(map)
			))
	);

	private static final MapCodec<CustomConversion> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(CustomConversion::count),
			IPECodecHelper.INSTANCE.nssCodec().fieldOf("output").forGetter(CustomConversion::output),
			INGREDIENT_CODEC.fieldOf("ingredients").forGetter(CustomConversion::ingredients),
			//Note: If propagateTags is set to true and the output isn't an NSSTag this gracefully gets set to false
			Codec.BOOL.optionalFieldOf("propagateTags", false).forGetter(CustomConversion::propagateTags)
	).apply(instance, CustomConversion::new));

	public static final Codec<CustomConversion> CODEC = MAP_CODEC.codec();
	private static final Codec<CustomConversion> OR_INVALID_CODEC = IPECodecHelper.INSTANCE.orElseWithLog(MAP_CODEC, INVALID, () -> "Failed to read conversions: {}").codec();

	public static final Codec<List<CustomConversion>> MODIFIABLE_LIST_CODEC = Util.make(() -> {
		Codec<List<CustomConversion>> listCodec = OR_INVALID_CODEC.listOf();
		//We only need to modify the decoder as we don't care about whether the list is modifiable during encoding
		return Codec.of(listCodec, listCodec.map(list -> list.stream()
				//Filter out any invalid entries we are skipping over from decoding
				.filter(conversion -> conversion != INVALID)
				//Collect to a mutable list
				.collect(Collectors.toList())
		));
	});

	public CustomConversion(int count, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredients) {
		this(count, output, ingredients, false);
	}

	public CustomConversion {
		//Only allow propagateTags to be true if the output is an NSSTag that represents a tag
		propagateTags = propagateTags && output instanceof NSSTag nssTag && nssTag.representsTag();
	}

	/**
	 * Creates a new conversion copying the passed in ingredients
	 *
	 * @param count       Amount of output this conversion produces (may not be equal to zero)
	 * @param output      Output stack
	 * @param ingredients Map of the ingredients and how many of each are necessary to perform the conversion (may not be equal to zero)
	 */
	public static CustomConversion getFor(int count, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredients) {
		//TODO: Figure out if this copying is even necessary
		CustomConversion conversion = new CustomConversion(count, output, new HashMap<>());
		conversion.ingredients.putAll(ingredients);
		return conversion;
	}

	@Override
	public String toString() {
		return "{" + count + " * " + output + " = " + ingredients + "}";
	}
}