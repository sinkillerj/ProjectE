package moze_intel.projecte.api.conversion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import moze_intel.projecte.api.codec.IPECodecHelper;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

/**
 * Holds deserialized custom conversions. <a href="https://gist.github.com/pupnewfster/7b5c411635e16227c1dea9af5e20e4c3">Full grammar specification</a>
 *
 * @param replace if {@code true} overrides any previously loaded conversions when {@link #merge(CustomConversionFile, CustomConversionFile) merging}.
 * @param comment Optional comment describing the file.
 * @param groups  Map of conversion groups.
 * @param values  Values that are fixed either by setting or by conversion.
 */
public record CustomConversionFile(boolean replace, @Nullable String comment, Map<String, ConversionGroup> groups, FixedValues values) {

	private static final Codec<Map<String, ConversionGroup>> GROUP_CODEC = IPECodecHelper.INSTANCE.modifiableMap(
			Codec.unboundedMap(ExtraCodecs.NON_EMPTY_STRING, ConversionGroup.CODEC)
	);

	public static final Codec<CustomConversionFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(CustomConversionFile::replace),
			ExtraCodecs.NON_EMPTY_STRING.optionalFieldOf("comment").forGetter(file -> Optional.ofNullable(file.comment())),
			GROUP_CODEC.optionalFieldOf("groups").forGetter(file -> IPECodecHelper.INSTANCE.ifNotEmpty(file.groups())),
			FixedValues.CODEC.optionalFieldOf("values").forGetter(file -> IPECodecHelper.INSTANCE.ifNotEmpty(file.values(), FixedValues::isEmpty))
	).apply(instance, (replace, comment, groups, values) -> new CustomConversionFile(replace, comment.orElse(null), groups.orElseGet(HashMap::new), values.orElseGet(FixedValues::new))));

	public CustomConversionFile() {
		this(false, null, new HashMap<>(), new FixedValues());
	}

	/**
	 * Merges the right Conversion File into the left one.
	 *
	 * @param left  Group to merge into.
	 * @param right Group to merge
	 *
	 * @return right if it is set to replace earlier ones, otherwise left with right's contents merged in.
	 *
	 * @implNote The comment of the other file is ignored.
	 */
	public static CustomConversionFile merge(CustomConversionFile left, CustomConversionFile right) {
		if (right.replace) {
			return right;
		}
		right.groups.forEach((name, group) -> left.groups.merge(name, group, ConversionGroup::merge));
		left.values.merge(right.values);
		return left;
	}

	/**
	 * Gets the existing Conversion Group with the given name or adds one if there isn't one already.
	 *
	 * @param groupName Group name.
	 */
	public ConversionGroup getOrAddGroup(String groupName) {
		return groups.computeIfAbsent(groupName, name -> new ConversionGroup());
	}
}