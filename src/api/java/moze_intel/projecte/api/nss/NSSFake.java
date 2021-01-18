package moze_intel.projecte.api.nss;

import com.google.common.base.Objects;
import javax.annotation.Nonnull;

/**
 * Implementation of {@link NormalizedSimpleStack} for representing abstract objects, that do not actually exist.
 */
public final class NSSFake implements NormalizedSimpleStack {

	/**
	 * We need this bit of global mutable state, so that we can distinguish fake NSS's originating from separate files, but have the same name. For example, "FAKE|foo"
	 * from a.json and "FAKE|foo" from b.json should not have anything to do with each other.
	 */
	private static String currentNamespace = "";

	private final String namespace;
	private final String description;

	private NSSFake(String namespace, String description) {
		this.namespace = namespace;
		this.description = description;
	}

	/**
	 * Resets the current namespace that will be used for any newly created {@link NSSFake} objects.
	 *
	 * @apiNote For internal use by ProjectE
	 */
	public static void resetNamespace() {
		setCurrentNamespace("");
	}

	/**
	 * Sets the current namespace that will be used for any newly created {@link NSSFake} objects.
	 *
	 * @param ns Namespace
	 *
	 * @apiNote For internal use by ProjectE
	 */
	public static void setCurrentNamespace(@Nonnull String ns) {
		currentNamespace = ns;
	}

	/**
	 * Helper method to create an {@link NSSFake} representing an abstract object that does not actually exist from a description.
	 */
	@Nonnull
	public static NormalizedSimpleStack create(String description) {
		return new NSSFake(currentNamespace, description);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof NSSFake && description.equals(((NSSFake) o).description) && namespace.equals(((NSSFake) o).namespace);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(namespace, description);
	}

	@Override
	public String json() {
		return "FAKE|" + this.description;
	}

	@Override
	public String toString() {
		return "NSSFAKE:" + namespace + "/" + description;
	}
}