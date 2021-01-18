package moze_intel.projecte.api.mapper.recipe;

import java.util.Set;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.util.Tuple;

/**
 * Interface to make for a cleaner API than using a {@link java.util.function.Function} when creating groupings of {@link NormalizedSimpleStack}s.
 */
public interface INSSFakeGroupManager {

	/**
	 * Gets or creates a singular {@link NormalizedSimpleStack} representing the grouping or "ingredient" of the given stacks. Additionally a boolean is returned
	 * specifying if it was created or already existed. {@code true} for if it was created.
	 *
	 * @param stacks Individual stacks to represent as a single "combined" stack.
	 *
	 * @apiNote If the combined representation had to be created ({@code true} for the second element of the {@link Tuple}), then conversions from the individual elements
	 * to the returned stack <strong>MUST</strong> be added.
	 */
	Tuple<NormalizedSimpleStack, Boolean> getOrCreateFakeGroup(Set<NormalizedSimpleStack> stacks);
}