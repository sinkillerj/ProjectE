package moze_intel.projecte.api.nss;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import moze_intel.projecte.api.codec.IPECodecHelper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract implementation to make implementing {@link NSSTag} and {@link NSSNBT} simpler, and automatically be able to register conversions for:
 * <p>
 * - Tag -> Type
 * <p>
 * - Type -> Tag
 *
 * @param <TYPE> The type of the tag this {@link NormalizedSimpleStack} is for.
 *
 * @implNote This does not handle NBT on Tags.
 */
public abstract class AbstractNBTNSSTag<TYPE> extends AbstractNSSTag<TYPE> implements NSSNBT {

	@Nullable
	private final CompoundTag nbt;

	protected AbstractNBTNSSTag(@NotNull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundTag nbt) {
		super(resourceLocation, isTag);
		this.nbt = nbt != null && nbt.isEmpty() ? null : nbt;
	}

	@Nullable
	@Override
	public CompoundTag getNBT() {
		return nbt;
	}

	@Override
	public String toString() {
		String string = super.toString();
		if (hasNBT()) {
			return string + nbt;
		}
		return string;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || super.equals(o) && Objects.equals(nbt, ((AbstractNBTNSSTag<?>) o).nbt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), nbt);
	}

	/**
	 * Helper to read nbt.
	 *
	 * @param nbtAsString {@link CompoundTag} string representation.
	 */
	protected static DataResult<CompoundTag> readNbt(String nbtAsString) {
		try {
			return DataResult.success(TagParser.parseTag(nbtAsString));
		} catch (CommandSyntaxException e) {
			return DataResult.error(() -> "Not valid nbt: " + nbtAsString + " " + e.getMessage());
		}
	}

	/**
	 * Creates an explicit codec capable of reading and writing this {@link NormalizedSimpleStack}.
	 *
	 * @param registry       Registry that backs this codec.
	 * @param allowDefault   {@code true} to allow ids matching the default element of the registry.
	 * @param nssConstructor Normalized Simple Stack constructor.
	 */
	protected static <TYPE, NSS extends AbstractNBTNSSTag<TYPE>> MapCodec<NSS> createExplicitCodec(@Nullable Registry<TYPE> registry, boolean allowDefault,
			NbtNSSConstructor<TYPE, NSS> nssConstructor) {
		//Note: We return a MapCodec so that dispatch codecs can inline this
		return NeoForgeExtraCodecs.withAlternative(
				createExplicitTagCodec(nssConstructor),
				RecordCodecBuilder.mapCodec(instance -> instance.group(
						idComponent(registry, allowDefault),
						CraftingHelper.TAG_CODEC.optionalFieldOf("nbt").forGetter(nss -> Optional.ofNullable(nss.getNBT()))
				).apply(instance, (rl, nbt) -> nssConstructor.create(rl, nbt.orElse(null))))
		);
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
	protected static <TYPE, NSS extends AbstractNBTNSSTag<TYPE>> Codec<NSS> createLegacyCodec(@Nullable Registry<TYPE> registry, boolean allowDefault, String prefix,
			NbtNSSConstructor<TYPE, NSS> nssConstructor) {
		return createLegacyCodec(registry, allowDefault, IPECodecHelper.INSTANCE.withPrefix(prefix), nssConstructor);
	}

	/**
	 * Creates a legacy codec capable of reading and writing this {@link NormalizedSimpleStack} to/from strings.
	 *
	 * @param registry       Registry that backs this codec.
	 * @param allowDefault   {@code true} to allow ids matching the default element of the registry.
	 * @param baseCodec      String codec that processes any related prefix requirements.
	 * @param nssConstructor Normalized Simple Stack constructor.
	 */
	static <TYPE, NSS extends AbstractNBTNSSTag<TYPE>> Codec<NSS> createLegacyCodec(@Nullable Registry<TYPE> registry, boolean allowDefault, Codec<String> baseCodec,
			NbtNSSConstructor<TYPE, NSS> nssConstructor) {
		return baseCodec.comapFlatMap(name -> {
			if (name.startsWith("#")) {
				return ResourceLocation.read(name.substring(1))
						.map(nssConstructor::createTag);
			}
			int nbtStart = name.indexOf('{');
			if (nbtStart == -1) {
				return readRL(registry, allowDefault, name)
						.map(nssConstructor::create);
			}
			return readRL(registry, allowDefault, name.substring(0, nbtStart))
					.apply2(nssConstructor::create, readNbt(name.substring(nbtStart)));
		}, nss -> {
			if (nss.representsTag()) {
				return "#" + nss.getResourceLocation();
			} else if (nss.hasNBT()) {
				return nss.getResourceLocation() + "" + nss.getNBT();
			}
			return nss.getResourceLocation().toString();
		});
	}

	/**
	 * Represents a constructor of an {@link AbstractNBTNSSTag}.
	 */
	@FunctionalInterface
	protected interface NbtNSSConstructor<TYPE, NSS extends AbstractNBTNSSTag<TYPE>> extends NSSTagConstructor<TYPE, NSS> {

		NSS create(ResourceLocation rl, boolean isTag, @Nullable CompoundTag nbt);

		@Override
		default NSS create(ResourceLocation rl, boolean isTag) {
			return create(rl, isTag, null);
		}

		default NSS create(ResourceLocation rl, @Nullable CompoundTag nbt) {
			return create(rl, false, nbt);
		}
	}
}