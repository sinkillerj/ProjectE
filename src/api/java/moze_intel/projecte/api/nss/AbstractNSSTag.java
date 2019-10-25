package moze_intel.projecte.api.nss;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;

/**
 * Abstract implementation to make implementing {@link NSSTag} simpler.
 *
 * @param <TYPE> The type of the {@link Tag} this {@link NormalizedSimpleStack} is for.
 */
public abstract class AbstractNSSTag<TYPE> implements NSSTag {

	@Nonnull
	private final ResourceLocation resourceLocation;
	private final boolean isTag;

	protected AbstractNSSTag(@Nonnull ResourceLocation resourceLocation, boolean isTag) {
		this.resourceLocation = resourceLocation;
		this.isTag = isTag;
	}

	/**
	 * @return The {@link ResourceLocation} representing the tag if this {@link NSSTag} represents a {@link Tag}, or the {@link ResourceLocation} of the
	 */
	@Nonnull
	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	/**
	 * @param o Another {@link AbstractNSSTag} to check if it is the same type as this {@link AbstractNSSTag}.
	 *
	 * @return True if the given {@link AbstractNSSTag} is of the same type as this {@link AbstractNSSTag}.
	 */
	protected abstract boolean isInstance(AbstractNSSTag o);

	/**
	 * @return A string representing a type description of this {@link NormalizedSimpleStack}
	 */
	@Nonnull
	protected abstract String getType();

	/**
	 * @return A string representing the prefix to use for json serialization.
	 *
	 * @implNote Must end with '|' to properly work. Anything without a '|' is assumed to be an item.
	 */
	@Nonnull
	protected abstract String getJsonPrefix();

	@Nonnull
	protected abstract TagCollection<TYPE> getTagCollection();

	protected abstract Function<TYPE, NormalizedSimpleStack> createNew();

	@Override
	public boolean representsTag() {
		return isTag;
	}

	@Override
	public void forEachElement(Consumer<NormalizedSimpleStack> consumer) {
		if (representsTag()) {
			Tag<TYPE> tag = getTagCollection().get(getResourceLocation());
			if (tag != null) {
				tag.getAllElements().stream().map(createNew()).forEach(consumer);
			}
		}
	}

	@Override
	public String json() {
		if (representsTag()) {
			return getJsonPrefix() + "#" + getResourceLocation();
		}
		return getJsonPrefix() + getResourceLocation();
	}

	@Override
	public String toString() {
		if (representsTag()) {
			return getType() + " Tag: " + getResourceLocation();
		}
		return getType() + ": " + getResourceLocation();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof AbstractNSSTag && isInstance((AbstractNSSTag) o)) {
			AbstractNSSTag other = (AbstractNSSTag) o;
			return representsTag() == other.representsTag() && getResourceLocation().equals(other.getResourceLocation());
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (representsTag()) {
			return 31 + resourceLocation.hashCode();
		}
		return resourceLocation.hashCode();
	}
}