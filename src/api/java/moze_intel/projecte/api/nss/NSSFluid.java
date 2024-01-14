package moze_intel.projecte.api.nss;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import moze_intel.projecte.api.codec.NSSCodecHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Fluid}s.
 */
public final class NSSFluid extends AbstractNBTNSSTag<Fluid> {

	private static Registry<Fluid> registry() {
		try {
			return BuiltInRegistries.FLUID;
		} catch (Throwable throwable) {
			if (FMLEnvironment.production) {
				throw throwable;
			}
			//TODO: Come up with a better way to detect this, but when we are in dev if we can't initialize the registry
			// skip it and don't do the extra element is registered validation
			return null;
		}
	}

	private static final boolean ALLOW_DEFAULT = false;

	/**
	 * Codec for encoding NSSFluids to and from strings.
	 */
	public static final Codec<NSSFluid> LEGACY_CODEC = createLegacyCodec(registry(), ALLOW_DEFAULT, "FLUID|", NSSFluid::new);

	public static final MapCodec<NSSFluid> EXPLICIT_MAP_CODEC = createExplicitCodec(registry(), ALLOW_DEFAULT, NSSFluid::new);
	public static final Codec<NSSFluid> EXPLICIT_CODEC = EXPLICIT_MAP_CODEC.codec();

	public static final NSSCodecHolder<NSSFluid> CODECS = new NSSCodecHolder<>("FLUID", LEGACY_CODEC, EXPLICIT_CODEC);


	private NSSFluid(@NotNull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundTag nbt) {
		super(resourceLocation, isTag, nbt);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link FluidStack}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull FluidStack stack) {
		//Don't bother checking if it is empty as getFluid returns EMPTY which will then fail anyways for being empty
		return createFluid(stack.getFluid(), stack.getTag());
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Fluid}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull Fluid fluid) {
		return createFluid(fluid, null);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Fluid} and an optional {@link CompoundTag}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull Fluid fluid, @Nullable CompoundTag nbt) {
		if (fluid == Fluids.EMPTY) {
			throw new IllegalArgumentException("Can't make NSSFluid with an empty fluid");
		}
		Optional<ResourceKey<Fluid>> registryKey = BuiltInRegistries.FLUID.getResourceKey(fluid);
		if (registryKey.isEmpty()) {
			throw new IllegalArgumentException("Can't make an NSSFluid with an unregistered fluid");
		}
		//This should never be null, or it would have crashed on being registered
		return createFluid(registryKey.get().location(), nbt);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link ResourceLocation}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull ResourceLocation fluidID) {
		return createFluid(fluidID, null);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link ResourceLocation} and an optional {@link CompoundTag}
	 */
	@NotNull
	public static NSSFluid createFluid(@NotNull ResourceLocation fluidID, @Nullable CompoundTag nbt) {
		return new NSSFluid(fluidID, false, nbt);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a tag from a {@link ResourceLocation}
	 */
	@NotNull
	public static NSSFluid createTag(@NotNull ResourceLocation tagId) {
		return new NSSFluid(tagId, true, null);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a tag from a {@link TagKey<Fluid>}
	 */
	@NotNull
	public static NSSFluid createTag(@NotNull TagKey<Fluid> tag) {
		return createTag(tag.location());
	}

	@NotNull
	@Override
	protected Registry<Fluid> getRegistry() {
		return BuiltInRegistries.FLUID;
	}

	@Override
	protected NSSFluid createNew(Fluid fluid) {
		return NSSFluid.createFluid(fluid);
	}

	@Override
	public NSSCodecHolder<NSSFluid> codecs() {
		return CODECS;
	}
}