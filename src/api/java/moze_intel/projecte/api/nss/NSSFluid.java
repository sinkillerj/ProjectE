package moze_intel.projecte.api.nss;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Fluid}s.
 */
public final class NSSFluid extends AbstractNBTNSSTag<Fluid> {

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

	@Override
	protected boolean isInstance(AbstractNSSTag<?> o) {
		return o instanceof NSSFluid;
	}

	@NotNull
	@Override
	public String getJsonPrefix() {
		return "FLUID|";
	}

	@NotNull
	@Override
	public String getType() {
		return "Fluid";
	}

	@NotNull
	@Override
	protected Registry<Fluid> getRegistry() {
		return BuiltInRegistries.FLUID;
	}

	@Override
	protected Function<Fluid, NormalizedSimpleStack> createNew() {
		return NSSFluid::createFluid;
	}
}