package moze_intel.projecte.api.conversion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

/**
 * @param setValueBefore Map of {@link NormalizedSimpleStack} to the value to set before applying conversions.
 * @param setValueAfter  Map of {@link NormalizedSimpleStack} to the value to set after applying conversions.
 * @param conversions    List of conversions
 */
public record FixedValues(Map<NormalizedSimpleStack, Long> setValueBefore, Map<NormalizedSimpleStack, Long> setValueAfter,
						  List<CustomConversion> conversions) implements IHasConversions {

	private static final Codec<Map<NormalizedSimpleStack, Long>> VALUE_CODEC = IPECodecHelper.INSTANCE.modifiableMap(
			//Note: We need to use the legacy codec as map keys for json are required to be able to be converted to strings
			IPECodecHelper.INSTANCE.lenientKeyUnboundedMap(IPECodecHelper.INSTANCE.legacyNSSCodec(), NeoForgeExtraCodecs.withAlternative(
					IPECodecHelper.INSTANCE.positiveLong(),
					ExtraCodecs.stringResolverCodec(
							val -> val == ProjectEAPI.FREE_ARITHMETIC_VALUE ? "free" : null,
							str -> str.equalsIgnoreCase("free") ? ProjectEAPI.FREE_ARITHMETIC_VALUE : null
					)
			))
	);

	public static final Codec<FixedValues> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			VALUE_CODEC.optionalFieldOf("before").forGetter(values -> IPECodecHelper.INSTANCE.ifNotEmpty(values.setValueBefore())),
			VALUE_CODEC.optionalFieldOf("after").forGetter(values -> IPECodecHelper.INSTANCE.ifNotEmpty(values.setValueAfter())),
			CustomConversion.MODIFIABLE_LIST_CODEC.optionalFieldOf("conversion").forGetter(values -> IPECodecHelper.INSTANCE.ifNotEmpty(values.conversions()))
	).apply(instance, (before, after, conversions) -> new FixedValues(before.orElseGet(HashMap::new), after.orElseGet(HashMap::new), conversions.orElseGet(ArrayList::new))));

	public FixedValues() {
		this(new HashMap<>(), new HashMap<>(), new ArrayList<>());
	}

	/**
	 * Merges another FixedValues into this one
	 *
	 * @param other Values to merge.
	 */
	public void merge(FixedValues other) {
		setValueBefore.putAll(other.setValueBefore());
		setValueAfter.putAll(other.setValueAfter());
		conversions.addAll(other.conversions());
	}

	/**
	 * Checks whether all the backing values and conversions of this object are empty.
	 *
	 * @return {@code true} if all are empty.
	 */
	public boolean isEmpty() {
		return setValueBefore.isEmpty() && setValueAfter.isEmpty() && conversions.isEmpty();
	}
}