package moze_intel.projecte.api.nss;

import java.util.function.Consumer;
import net.minecraftforge.registries.tags.ITag;

/**
 * An extension of {@link NormalizedSimpleStack} that allows for representing stacks that are both "simple" and can have a {@link ITag} representation.
 */
public interface NSSTag extends NormalizedSimpleStack {

	/**
	 * Checks if our {@link NormalizedSimpleStack} is representing a {@link ITag}
	 *
	 * @return True if this {@link NSSTag} object is representing a {@link ITag}, or false if we are really just a {@link NormalizedSimpleStack}.
	 */
	boolean representsTag();

	/**
	 * For every element in our {@link ITag} run the given {@link Consumer<NormalizedSimpleStack>} on the {@link NormalizedSimpleStack} that represents them.
	 *
	 * @param consumer The {@link Consumer<NormalizedSimpleStack>} to run on our {@link NormalizedSimpleStack}s.
	 *
	 * @apiNote This does not do anything if this {@link NSSTag} is not currently representing a tag.
	 */
	void forEachElement(Consumer<NormalizedSimpleStack> consumer);
}