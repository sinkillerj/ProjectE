package moze_intel.projecte.api.nss;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Fluid}s.
 */
public final class NSSFluid extends AbstractNBTNSSTag<Fluid> {

	private NSSFluid(@Nonnull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundTag nbt) {
		super(resourceLocation, isTag, nbt);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link FluidStack}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull FluidStack stack) {
		//Don't bother checking if it is empty as getFluid returns EMPTY which will then fail anyways for being empty
		return createFluid(stack.getFluid(), stack.getTag());
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Fluid}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull Fluid fluid) {
		return createFluid(fluid, null);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Fluid} and an optional {@link CompoundTag}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull Fluid fluid, @Nullable CompoundTag nbt) {
		if (fluid == Fluids.EMPTY) {
			throw new IllegalArgumentException("Can't make NSSFluid with an empty fluid");
		}
		//This should never be null or it would have crashed on being registered
		return createFluid(fluid.getRegistryName(), nbt);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link ResourceLocation}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull ResourceLocation fluidID) {
		return createFluid(fluidID, null);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link ResourceLocation} and an optional {@link CompoundTag}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull ResourceLocation fluidID, @Nullable CompoundTag nbt) {
		return new NSSFluid(fluidID, false, nbt);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a tag from a {@link ResourceLocation}
	 */
	@Nonnull
	public static NSSFluid createTag(@Nonnull ResourceLocation tagId) {
		return new NSSFluid(tagId, true, null);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a tag from a {@link TagKey<Fluid>}
	 */
	@Nonnull
	public static NSSFluid createTag(@Nonnull TagKey<Fluid> tag) {
		return createTag(tag.location());
	}

	@Override
	protected boolean isInstance(AbstractNSSTag<?> o) {
		return o instanceof NSSFluid;
	}

	@Nonnull
	@Override
	public String getJsonPrefix() {
		return "FLUID|";
	}

	@Nonnull
	@Override
	public String getType() {
		return "Fluid";
	}

	@Nonnull
	@Override
	protected Optional<Named<Fluid>> getTag() {
		return getTag(Registry.FLUID);
	}

	@Override
	protected Function<Fluid, NormalizedSimpleStack> createNew() {
		return NSSFluid::createFluid;
	}
}