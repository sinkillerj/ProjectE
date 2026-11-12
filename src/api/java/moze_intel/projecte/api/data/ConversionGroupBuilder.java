package moze_intel.projecte.api.data;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.api.conversion.ConversionGroup;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;

/**
 * Builder class to help create conversion groups.
 */
@ParametersAreNonnullByDefault
public class ConversionGroupBuilder extends BaseFileBuilder<ConversionGroupBuilder> implements CustomConversionNSSHelper<ConversionBuilder<ConversionGroupBuilder>> {

	private final CustomConversionBuilder customConversionBuilder;
	private final List<ConversionBuilder<?>> conversions = new ArrayList<>();

	ConversionGroupBuilder(CustomConversionBuilder customConversionBuilder) {
		super("Group");
		this.customConversionBuilder = customConversionBuilder;
	}

	ConversionGroup build() {
		return new ConversionGroup(comment, conversions.stream().map(ConversionBuilder::build).toList());
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
}
