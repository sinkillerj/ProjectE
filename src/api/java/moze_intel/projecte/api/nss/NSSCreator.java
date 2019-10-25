package moze_intel.projecte.api.nss;

import com.google.gson.JsonParseException;
import javax.annotation.Nonnull;

/**
 * Used for creating a {@link NormalizedSimpleStack} from a {@link String} retrieved from JSON.
 */
@FunctionalInterface
public interface NSSCreator {

	/**
	 * Creates a {@link NormalizedSimpleStack} from the given {@link String}
	 *
	 * @param string The string received from JSON to parse.
	 *
	 * @return A {@link NormalizedSimpleStack} created from the given {@link String}.
	 *
	 * @throws JsonParseException If something went wrong parsing the {@link String}.
	 */
	@Nonnull
	NormalizedSimpleStack create(String string) throws JsonParseException;
}