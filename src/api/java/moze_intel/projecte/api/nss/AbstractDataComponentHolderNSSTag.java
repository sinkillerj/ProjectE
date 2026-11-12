package moze_intel.projecte.api.nss;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract implementation to make implementing {@link NSSTag} and {@link NSSDataComponentHolder} simpler, and automatically be able to register conversions for:
 * <p>
 * - Tag -> Type
 * <p>
 * - Type -> Tag
 *
 * @param <TYPE> The type of the tag this {@link NormalizedSimpleStack} is for.
 *
 * @implNote This does not handle data components on Tags.
 */
public abstract class AbstractDataComponentHolderNSSTag<TYPE> extends AbstractNSSTag<TYPE> implements NSSDataComponentHolder {

	@NotNull
	private final DataComponentPatch componentsPatch;
	private boolean hasCachedHash;
	private int cachedHashCode;

	protected AbstractDataComponentHolderNSSTag(@NotNull Identifier resourceLocation, boolean isTag, @NotNull DataComponentPatch componentsPatch) {
		super(resourceLocation, isTag);
		this.componentsPatch = Objects.requireNonNull(componentsPatch, "Components patch must not be null");
	}

	@NotNull
	@Override
	public DataComponentPatch getComponentsPatch() {
		return componentsPatch;
	}

	@Override
	public String toString() {
		//noinspection ConstantValue - Can be null during super construction
		if (componentsPatch == null || componentsPatch.isEmpty()) {
			return super.toString();
		}
		return super.toString() + " " + componentsPatch;
	}

	@Override
	public boolean equals(Object o) {
		//Note: This needs to use Objects.equals instead of equals so that it doesn't throw an exception in super when adding to the set of created tags
		return o == this || super.equals(o) && Objects.equals(componentsPatch, ((AbstractDataComponentHolderNSSTag<?>) o).componentsPatch);
	}

	@Override
	public int hashCode() {
		if (componentsPatch == null) {
			//Happens when adding to the set of created tags in super
			hasCachedHash = true;
			cachedHashCode = Objects.hash(super.hashCode(), DataComponentPatch.EMPTY);
		} else if (!hasCachedHash) {
			hasCachedHash = true;
			cachedHashCode = Objects.hash(super.hashCode(), componentsPatch);
		}
		return cachedHashCode;
	}

	/**
	 * Creates an explicit codec capable of reading and writing this {@link NormalizedSimpleStack}.
	 *
	 * @param registry       Registry that backs this codec.
	 * @param allowDefault   {@code true} to allow ids matching the default element of the registry.
	 * @param nssConstructor Normalized Simple Stack constructor.
	 */
	protected static <TYPE, NSS extends AbstractDataComponentHolderNSSTag<TYPE>> MapCodec<NSS> createCodec(Registry<TYPE> registry, boolean allowDefault,
			DataComponentHolderNSSConstructor<TYPE, NSS> nssConstructor) {
		//Note: We return a MapCodec so that dispatch codecs can inline this
		return NeoForgeExtraCodecs.withAlternative(
				createTagCodec(nssConstructor),
				RecordCodecBuilder.mapCodec(instance -> instance.group(
						idComponent(registry, allowDefault),
						DataComponentPatch.CODEC.optionalFieldOf("data", DataComponentPatch.EMPTY).forGetter(AbstractDataComponentHolderNSSTag::getComponentsPatch)
				).apply(instance, nssConstructor::create))
		);
	}

	/**
	 * Represents a constructor of an {@link AbstractDataComponentHolderNSSTag}.
	 */
	@FunctionalInterface
	protected interface DataComponentHolderNSSConstructor<TYPE, NSS extends AbstractDataComponentHolderNSSTag<TYPE>> extends NSSTagConstructor<TYPE, NSS> {

		NSS create(Identifier rl, boolean isTag, @NotNull DataComponentPatch componentsPatch);

		@Override
		default NSS create(Identifier rl, boolean isTag) {
			return create(rl, isTag, DataComponentPatch.EMPTY);
		}

		default NSS create(Identifier rl, @NotNull DataComponentPatch componentsPatch) {
			return create(rl, false, componentsPatch);
		}
	}
}
