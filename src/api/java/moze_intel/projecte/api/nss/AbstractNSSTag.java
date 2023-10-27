package moze_intel.projecte.api.nss;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract implementation to make implementing {@link NSSTag} simpler, and automatically be able to register conversions for:
 * <p>
 * - Tag -> Type
 * <p>
 * - Type -> Tag
 *
 * @param <TYPE> The type of the {@link ITag} this {@link NormalizedSimpleStack} is for.
 */
public abstract class AbstractNSSTag<TYPE> implements NSSTag {

	private static final Set<NSSTag> createdTags = new HashSet<>();

	/**
	 * @return A set of all the {@link NSSTag}s that have been created that represent a {@link ITag}
	 *
	 * @apiNote This method is meant for internal use of adding Tag -> Type and Type -> Tag conversions
	 */
	public static Set<NSSTag> getAllCreatedTags() {
		return ImmutableSet.copyOf(createdTags);
	}

	/**
	 * Clears the cache of what {@link AbstractNSSTag}s have been created that represent {@link ITag}s
	 *
	 * @apiNote This method is meant for internal use when the EMC mapper is reloading.
	 */
	public static void clearCreatedTags() {
		createdTags.clear();
	}

	@NotNull
	private final ResourceLocation resourceLocation;
	private final boolean isTag;

	protected AbstractNSSTag(@NotNull ResourceLocation resourceLocation, boolean isTag) {
		this.resourceLocation = resourceLocation;
		this.isTag = isTag;
		if (isTag) {
			createdTags.add(this);
		}
	}

	/**
	 * @return The {@link ResourceLocation} representing the tag if this {@link NSSTag} represents a {@link ITag}, or the {@link ResourceLocation} of the object
	 */
	@NotNull
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
	@NotNull
	protected abstract String getType();

	/**
	 * @return A string representing the prefix to use for json serialization.
	 *
	 * @implNote Must end with '|' to properly work. Anything without a '|' is assumed to be an item.
	 */
	@NotNull
	protected abstract String getJsonPrefix();

	/**
	 * @return An optional with an object that represents either a named tag or forge's tag representation.
	 */
	@NotNull
	protected abstract Optional<Either<Named<TYPE>, ITag<TYPE>>> getTag();

	/**
	 * Helper to get the tag representation if this {@link NormalizedSimpleStack} is backed by a vanilla registry.
	 */
	protected final Optional<Either<Named<TYPE>, ITag<TYPE>>> getTag(Registry<TYPE> registry) {
		if (representsTag()) {
			return registry.getTag(TagKey.create(registry.key(), getResourceLocation())).map(Either::left);
		}
		return Optional.empty();
	}

	/**
	 * Helper to get the tag representation if this {@link NormalizedSimpleStack} is backed by a forge registry.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected final Optional<Either<Named<TYPE>, ITag<TYPE>>> getTag(IForgeRegistry<? extends TYPE> registry) {
		if (representsTag()) {
			ITagManager<? extends TYPE> tags = registry.tags();
			if (tags != null) {
				return Optional.of(Either.right(tags.getTag((TagKey) tags.createTagKey(getResourceLocation()))));
			}
		}
		return Optional.empty();
	}

	protected abstract Function<TYPE, NormalizedSimpleStack> createNew();

	@Override
	public boolean representsTag() {
		return isTag;
	}

	@Override
	public void forEachElement(Consumer<NormalizedSimpleStack> consumer) {
		getTag().ifPresent(tag -> tag.map(t -> t.stream().map(Holder::value), ITag::stream)
				.map(createNew())
				.forEach(consumer)
		);
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