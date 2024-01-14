package moze_intel.projecte.api.data;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.api.conversion.ConversionGroup;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * Builder class to help create conversion groups.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConversionGroupBuilder implements CustomConversionNSSHelper<ConversionBuilder<ConversionGroupBuilder>> {

	private final CustomConversionBuilder customConversionBuilder;
	private final List<ConversionBuilder<?>> conversions = new ArrayList<>();
	@Nullable
	private String comment;

	ConversionGroupBuilder(CustomConversionBuilder customConversionBuilder) {
		this.customConversionBuilder = customConversionBuilder;
	}

	ConversionGroup build() {
		return new ConversionGroup(comment, conversions.stream().map(ConversionBuilder::build).toList());
	}

	/**
	 * Optionally adds a given comment to the conversion group. Useful for describing what the group is used for to people looking at the json file.
	 *
	 * @param comment Comment to add.
	 */
	public ConversionGroupBuilder comment(String comment) {
		CustomConversionBuilder.validateComment(this.comment, comment, "Group");
		this.comment = comment;
		return this;
	}

	@Override
	public ConversionBuilder<ConversionGroupBuilder> conversion(NormalizedSimpleStack output, int amount) {
		if (amount < 1) {
			throw new IllegalArgumentException("Output amount for fixed value conversions must be at least one.");
		}
		ConversionBuilder<ConversionGroupBuilder> builder = new ConversionBuilder<>(this, output, amount);
		conversions.add(builder);
		return builder;
	}

	/**
	 * Ends this group builder and returns to the {@link CustomConversionBuilder}.
	 */
	public CustomConversionBuilder end() {
		return customConversionBuilder;
	}

	/**
	 * @return {@code true} if this group has a comment, {@code false} otherwise.
	 */
	boolean hasComment() {
		return comment != null;
	}
}