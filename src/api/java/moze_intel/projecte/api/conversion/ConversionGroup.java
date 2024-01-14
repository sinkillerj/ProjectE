package moze_intel.projecte.api.conversion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

/**
 * @param comment     Optional comment describing the group
 * @param conversions List of conversions
 */
public record ConversionGroup(@Nullable String comment, List<CustomConversion> conversions) implements IHasConversions {

	public static final Codec<ConversionGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.NON_EMPTY_STRING.optionalFieldOf("comment").forGetter(group -> Optional.ofNullable(group.comment())),
			CustomConversion.MODIFIABLE_LIST_CODEC.fieldOf("conversions").forGetter(ConversionGroup::conversions)
	).apply(instance, (comment, conversions) -> new ConversionGroup(comment.orElse(null), conversions)));

	public ConversionGroup() {
		this(null, new ArrayList<>());
	}

	/**
	 * {@return number of conversions in this group}
	 */
	public int size() {
		return conversions.size();
	}

	/**
	 * Merges another Conversion Group into this one
	 *
	 * @param other Group to merge.
	 *
	 * @implNote The comment of the other group is ignored.
	 */
	public ConversionGroup merge(ConversionGroup other) {
		conversions.addAll(other.conversions());
		return this;
	}
}