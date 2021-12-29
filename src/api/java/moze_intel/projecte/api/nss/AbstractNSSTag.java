package moze_intel.projecte.api.nss;

import com.google.common.collect.ImmutableSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;

/**
 * Abstract implementation to make implementing {@link NSSTag} simpler, and automatically be able to register conversions for:
 *
 * - Tag -> Type
 *
 * - Type -> Tag
 *
 * @param <TYPE> The type of the {@link Tag} this {@link NormalizedSimpleStack} is for.
 */
public abstract class AbstractNSSTag<TYPE> implements NSSTag {

	private static final Set<NSSTag> createdTags = new HashSet<>();

	/**
	 * @return A set of all the {@link NSSTag}s that have been created that represent a {@link Tag}
	 *
	 * @apiNote This method is meant for internal use of adding Tag -> Type and Type -> Tag conversions
	 */
	public static Set<NSSTag> getAllCreatedTags() {
		return ImmutableSet.copyOf(createdTags);
	}

	/**
	 * Clears the cache of what {@link AbstractNSSTag}s have been created that represent {@link Tag}s
	 *
	 * @apiNote This method is meant for internal use when the EMC mapper is reloading.
	 */
	public static void clearCreatedTags() {
		createdTags.clear();
	}

	@Nonnull
	private final ResourceLocation resourceLocation;
	private final boolean isTag;

	protected AbstractNSSTag(@Nonnull ResourceLocation resourceLocation, boolean isTag) {
		this.resourceLocation = resourceLocation;
		this.isTag = isTag;
		if (isTag) {
			createdTags.add(this);
		}
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
	protected abstract boolean isInstance(AbstractNSSTag<?> o);

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
			//TODO - 1.18: Make getTagCollection return a registry key instead and look it up using that
			Tag<TYPE> tag = getTagCollection().getTag(getResourceLocation());
			if (tag != null) {
				tag.getValues().stream().map(createNew()).forEach(consumer);
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
		if (o instanceof AbstractNSSTag<?> other && isInstance(other)) {
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