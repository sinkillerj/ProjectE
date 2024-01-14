package moze_intel.projecte.api.nss;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import moze_intel.projecte.api.codec.IPECodecHelper;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract implementation to make implementing {@link NSSTag} simpler, and automatically be able to register conversions for:
 * <p>
 * - Tag -> Type
 * <p>
 * - Type -> Tag
 *
 * @param <TYPE> The type of the tag this {@link NormalizedSimpleStack} is for.
 */
public abstract class AbstractNSSTag<TYPE> implements NSSTag {

	private static final Set<NSSTag> createdTags = new HashSet<>();

	/**
	 * @return A set of all the {@link NSSTag}s that have been created that represent a tag
	 *
	 * @apiNote This method is meant for internal use of adding Tag -> Type and Type -> Tag conversions
	 */
	public static Set<NSSTag> getAllCreatedTags() {
		return ImmutableSet.copyOf(createdTags);
	}

	/**
	 * Clears the cache of what {@link AbstractNSSTag}s have been created that represent tags
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
	 * @return The {@link ResourceLocation} representing the tag if this {@link NSSTag} represents a tag, or the {@link ResourceLocation} of the object
	 */
	@NotNull
	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	/**
	 * @return The registry that the element this NSS object represents is a part of.
	 */
	@NotNull
	protected abstract Registry<TYPE> getRegistry();

	/**
	 * Helper to get the tag representation if this {@link NormalizedSimpleStack} is backed by a vanilla registry.
	 */
	protected final Optional<HolderSet.Named<TYPE>> getTag(Registry<TYPE> registry) {
		if (representsTag()) {
			return registry.getTag(TagKey.create(registry.key(), getResourceLocation()));
		}
		return Optional.empty();
	}

	/**
	 * Creates a new {@link NormalizedSimpleStack} for an element of the type it is for.
	 *
	 * @param type Object the stack represents.
	 */
	protected abstract NormalizedSimpleStack createNew(TYPE type);

	@Override
	public boolean representsTag() {
		return isTag;
	}

	@Override
	public void forEachElement(Consumer<NormalizedSimpleStack> consumer) {
		getTag(getRegistry()).ifPresent(tag -> tag.stream()
				.map(holder -> createNew(holder.value()))
				.forEach(consumer)
		);
	}

	@Override
	public String toString() {
		ResourceLocation type = getRegistry().key().location();
		if (representsTag()) {
			return type + " Tag: " + getResourceLocation();
		}
		return type + ": " + getResourceLocation();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o != null && getClass() == o.getClass()) {
			AbstractNSSTag<?> other = (AbstractNSSTag<?>) o;
			return representsTag() == other.representsTag() && getResourceLocation().equals(other.getResourceLocation());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(isTag, resourceLocation);
	}

	/**
	 * Helper to read a resource location and validate it is part of a registry.
	 *
	 * @param registry     Registry that backs this codec.
	 * @param allowDefault {@code true} to allow ids matching the default element of the registry.
	 * @param rl           Resource Location string representation.
	 */
	protected static DataResult<ResourceLocation> readRL(@Nullable Registry<?> registry, boolean allowDefault, String rl) {
		return ResourceLocation.read(rl).flatMap(id -> validate(registry, allowDefault, id));
	}

	/**
	 * {@return a {@link MapCodec} representing the tag variant of an explicit codec}
	 *
	 * @param nssConstructor Normalized Simple Stack constructor.
	 */
	protected static <TYPE, NSS extends AbstractNSSTag<TYPE>> MapCodec<NSS> createExplicitTagCodec(NSSTagConstructor<TYPE, NSS> nssConstructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				IPECodecHelper.INSTANCE.validatePresent(
						ResourceLocation.CODEC, () -> "Must represent a tag"
				).fieldOf("tag").forGetter(nss -> nss.representsTag() ? nss.getResourceLocation() : null)
		).apply(instance, nssConstructor::createTag));
	}

	/**
	 * {@return the id component of an explicit codec}
	 *
	 * @param registry     Registry that backs this codec.
	 * @param allowDefault {@code true} to allow ids matching the default element of the registry.
	 */
	protected static <TYPE, NSS extends AbstractNSSTag<TYPE>> RecordCodecBuilder<NSS, ResourceLocation> idComponent(@Nullable Registry<?> registry, boolean allowDefault) {
		return ExtraCodecs.validate(ResourceLocation.CODEC, id -> {
			if (id == null) {
				return com.mojang.serialization.DataResult.error(() -> "Must represent a registry id");
			}
			return validate(registry, allowDefault, id);
		}).fieldOf("id").forGetter(nss -> nss.representsTag() ? null : nss.getResourceLocation());
	}

	/**
	 * Validates if an id is contained in the registry and if {@code allowDefault = false} doesn't match the default element of the registry.
	 *
	 * @param registry     Registry that backs this codec.
	 * @param allowDefault {@code true} to allow ids matching the default element of the registry.
	 */
	private static DataResult<ResourceLocation> validate(@Nullable Registry<?> registry, boolean allowDefault, ResourceLocation id) {
		if (registry == null) {//TODO - 1.20.4: Remove nullability of registry when neoforge allows initializing junit with registries
			return DataResult.success(id);
		}
		if (!registry.containsKey(id)) {
			return DataResult.error(() -> "Registry " + registry.key().location() + " does not contain element " + id);
		} else if (!allowDefault && registry instanceof DefaultedRegistry<?> defaultedRegistry && id.equals(defaultedRegistry.getDefaultKey())) {
			return DataResult.error(() -> "NormalizedSimpleStack cannot be created for registry " + registry.key().location() + " with the default element " + id);
		}
		return DataResult.success(id);
	}

	/**
	 * Creates an explicit codec capable of reading and writing this {@link NormalizedSimpleStack}.
	 *
	 * @param registry       Registry that backs this codec.
	 * @param allowDefault   {@code true} to allow ids matching the default element of the registry.
	 * @param nssConstructor Normalized Simple Stack constructor.
	 */
	protected static <TYPE, NSS extends AbstractNSSTag<TYPE>> Codec<NSS> createExplicitCodec(@Nullable Registry<TYPE> registry, boolean allowDefault,
			NSSTagConstructor<TYPE, NSS> nssConstructor) {
		//Note: We return a MapCodecCodec so that dispatch codecs can inline this
		return NeoForgeExtraCodecs.withAlternative(
				createExplicitTagCodec(nssConstructor),
				RecordCodecBuilder.mapCodec(instance -> instance.group(
						idComponent(registry, allowDefault)
				).apply(instance, nssConstructor::create))
		).codec();
	}

	/**
	 * Creates a legacy codec capable of reading and writing this {@link NormalizedSimpleStack} to/from strings.
	 *
	 * @param registry       Registry that backs this codec.
	 * @param allowDefault   {@code true} to allow ids matching the default element of the registry.
	 * @param prefix         A string representing the prefix to use for serialization. Must end with '|' to properly work. Anything without a '|' is assumed to be an
	 *                       item.
	 * @param nssConstructor Normalized Simple Stack constructor.
	 */
	protected static <TYPE, NSS extends AbstractNSSTag<TYPE>> Codec<NSS> createLegacyCodec(@Nullable Registry<TYPE> registry, boolean allowDefault, String prefix,
			NSSTagConstructor<TYPE, NSS> nssConstructor) {
		return IPECodecHelper.INSTANCE.withPrefix(prefix).comapFlatMap(name -> {
			if (name.startsWith("#")) {
				return ResourceLocation.read(name.substring(1)).map(nssConstructor::createTag);
			}
			return readRL(registry, allowDefault, name).map(nssConstructor::create);
		}, nss -> {
			if (nss.representsTag()) {
				return "#" + nss.getResourceLocation();
			}
			return nss.getResourceLocation().toString();
		});
	}

	/**
	 * Represents a constructor of an {@link AbstractNSSTag}.
	 */
	@FunctionalInterface
	protected interface NSSTagConstructor<TYPE, NSS extends AbstractNSSTag<TYPE>> {

		NSS create(ResourceLocation rl, boolean isTag);

		default NSS create(ResourceLocation rl) {
			return create(rl, false);
		}

		default NSS createTag(ResourceLocation rl) {
			return create(rl, true);
		}
	}
}