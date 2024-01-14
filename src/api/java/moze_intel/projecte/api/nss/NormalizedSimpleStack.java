package moze_intel.projecte.api.nss;

import java.util.function.Consumer;
import moze_intel.projecte.api.codec.NSSCodecHolder;

/**
 * Represents a "stack" to be used by the EMC mapper.
 */
public interface NormalizedSimpleStack {

	/**
	 * @return A codec holder containing the legacy and explicit codec for this stack.
	 */
	NSSCodecHolder<?> codecs();

	/**
	 * Run the consumer for this {@link NormalizedSimpleStack} and if it is an {@link NSSTag} any elements in the tag.
	 *
	 * @param consumer The {@link Consumer<NormalizedSimpleStack>} to run on our {@link NormalizedSimpleStack}s.
	 */
	default void forSelfAndEachElement(Consumer<NormalizedSimpleStack> consumer) {
		consumer.accept(this);
	}

	@Override
	boolean equals(Object o);

	@Override
	int hashCode();

	@Override
	String toString();
}