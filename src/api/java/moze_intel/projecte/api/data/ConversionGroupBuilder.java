package moze_intel.projecte.api.data;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import moze_intel.projecte.api.data.ConversionGroupBuilder.GroupConversionBuilder;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;

/**
 * Builder class to help create conversion groups.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConversionGroupBuilder implements CustomConversionNSSHelper<GroupConversionBuilder> {

	private final CustomConversionBuilder customConversionBuilder;
	private final List<GroupConversionBuilder> conversions = new ArrayList<>();
	@Nullable
	private String comment;

	ConversionGroupBuilder(CustomConversionBuilder customConversionBuilder) {
		this.customConversionBuilder = customConversionBuilder;
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
	public GroupConversionBuilder conversion(NormalizedSimpleStack output, int amount) {
		if (amount < 1) {
			throw new IllegalArgumentException("Output amount for fixed value conversions must be at least one.");
		}
		GroupConversionBuilder builder = new GroupConversionBuilder(output, amount);
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

	/**
	 * Serializes this conversion group into a json object.
	 */
	JsonObject serialize() {
		JsonObject json = new JsonObject();
		if (comment != null) {
			json.addProperty("comment", comment);
		}
		if (!conversions.isEmpty()) {
			//Only add conversions if there are any, if there aren't then we will error with the correct message from our returned
			// object being empty
			json.add("conversions", CustomConversionBuilder.serializeConversions(conversions));
		}
		return json;
	}

	public class GroupConversionBuilder extends ConversionBuilder<GroupConversionBuilder> {

		private GroupConversionBuilder(NormalizedSimpleStack output, int count) {
			super(output, count);
		}

		/**
		 * Ends this group conversion builder and returns to the {@link ConversionGroupBuilder}.
		 *
		 * @apiNote While it is not required to call this method if it is the last line of your builder calls. It is recommended to do so to get better line number
		 * errors if you accidentally forgot to include any ingredients.
		 */
		public ConversionGroupBuilder end() {
			validateIngredients();
			return ConversionGroupBuilder.this;
		}
	}
}