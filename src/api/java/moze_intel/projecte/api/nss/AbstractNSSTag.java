package moze_intel.projecte.api.nss;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import moze_intel.projecte.api.codec.IPECodecHelper;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

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
		return Set.copyOf(createdTags);
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
	private final Identifier resourceLocation;
	private final boolean isTag;

	protected AbstractNSSTag(@NotNull Identifier resourceLocation, boolean isTag) {
		this.resourceLocation = resourceLocation;
		this.isTag = isTag;
		if (isTag) {
			createdTags.add(this);
		}
	}

	/**
	 * @return The {@link Identifier} representing the tag if this {@link NSSTag} represents a tag, or the {@link Identifier} of the object
	 */
	@NotNull
	public Identifier getResourceLocation() {
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
	protected final Iterable<Holder<TYPE>> getTagElements(Registry<TYPE> registry) {
		if (representsTag()) {
			return registry.getTagOrEmpty(TagKey.create(registry.key(), getResourceLocation()));
		}
		return List.of();
	}

	/**
	 * Creates a new {@link NormalizedSimpleStack} for an element of the type it is for.
	 *
	 * @param type Object the stack represents.
	 */
	protected abstract NormalizedSimpleStack createNew(Holder<TYPE> type);

	@Override
	public boolean representsTag() {
		return isTag;
	}

	@Override
	public void forEachElement(Consumer<NormalizedSimpleStack> consumer) {
		for (Holder<TYPE> holder : getTagElements(getRegistry())) {
			consumer.accept(createNew(holder));
		}
	}

	@Override
	public <CONTEXT, DATA> void forEachElement(CONTEXT context, DATA data, TriConsumer<CONTEXT, NormalizedSimpleStack, DATA> consumer) {
		for (Holder<TYPE> holder : getTagElements(getRegistry())) {
			consumer.accept(context, createNew(holder), data);
		}
	}

	@Override
	public String toString() {
		Identifier type = getRegistry().key().identifier();
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
	 * {@return a {@link MapCodec} representing the tag variant of an explicit codec}
	 *
	 * @param nssConstructor Normalized Simple Stack constructor.
	 */
	protected static <TYPE, NSS extends AbstractNSSTag<TYPE>> MapCodec<NSS> createTagCodec(NSSTagConstructor<TYPE, NSS> nssConstructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				IPECodecHelper.INSTANCE.validatePresent(
						Identifier.CODEC, () -> "Must represent a tag"
				).fieldOf("tag").forGetter(nss -> nss.representsTag() ? nss.getResourceLocation() : null)
		).apply(instance, nssConstructor::createTag));
	}

	/**
	 * {@return the id component of an explicit codec}
	 *
	 * @param registry     Registry that backs this codec.
	 * @param allowDefault {@code true} to allow ids matching the default element of the registry.
	 */
	protected static <TYPE, NSS extends AbstractNSSTag<TYPE>> RecordCodecBuilder<NSS, Identifier> idComponent(Registry<?> registry, boolean allowDefault) {
		return Identifier.CODEC.validate(id -> {
			if (id == null) {
				return DataResult.error(() -> "Must represent a registry id");
			} else if (!registry.containsKey(id)) {
				return DataResult.error(() -> "Registry " + registry.key().identifier() + " does not contain element " + id);
			} else if (!allowDefault && registry instanceof DefaultedRegistry<?> defaultedRegistry && id.equals(defaultedRegistry.getDefaultKey())) {
				return DataResult.error(() -> "NormalizedSimpleStack cannot be created for registry " + registry.key().identifier() + " with the default element " + id);
			}
			return DataResult.success(id);
		}).fieldOf("id").forGetter(nss -> nss.representsTag() ? null : nss.getResourceLocation());
	}

	/**
	 * Creates an explicit codec capable of reading and writing this {@link NormalizedSimpleStack}.
	 *
	 * @param registry       Registry that backs this codec.
	 * @param allowDefault   {@code true} to allow ids matching the default element of the registry.
	 * @param nssConstructor Normalized Simple Stack constructor.
	 */
	protected static <TYPE, NSS extends AbstractNSSTag<TYPE>> MapCodec<NSS> createCodec(Registry<TYPE> registry, boolean allowDefault,
			NSSTagConstructor<TYPE, NSS> nssConstructor) {
		//Note: We return a MapCodec so that dispatch codecs can inline this
		return NeoForgeExtraCodecs.withAlternative(
				createTagCodec(nssConstructor),
				RecordCodecBuilder.mapCodec(instance -> instance.group(
						idComponent(registry, allowDefault)
				).apply(instance, nssConstructor::create))
		);
	}

	/**
	 * Represents a constructor of an {@link AbstractNSSTag}.
	 */
	@FunctionalInterface
	protected interface NSSTagConstructor<TYPE, NSS extends AbstractNSSTag<TYPE>> {

		NSS create(Identifier rl, boolean isTag);

		default NSS create(Identifier rl) {
			return create(rl, false);
		}

		default NSS createTag(Identifier rl) {
			return create(rl, true);
		}
	}
}
