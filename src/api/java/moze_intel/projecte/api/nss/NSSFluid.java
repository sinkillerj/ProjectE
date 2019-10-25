package moze_intel.projecte.api.nss;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Fluid}s.
 */
public final class NSSFluid extends AbstractNSSTag<Fluid> {

	private NSSFluid(@Nonnull ResourceLocation resourceLocation, boolean isTag) {
		super(resourceLocation, isTag);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link FluidStack}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull FluidStack stack) {
		//Don't bother checking if it is empty as getFluid returns EMPTY which will then fail anyways for being empty
		return createFluid(stack.getFluid());
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Fluid}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull Fluid fluid) {
		if (fluid == Fluids.EMPTY) {
			throw new IllegalArgumentException("Can't make NSSFluid with an empty fluid");
		}
		//This should never be null or it would have crashed on being registered
		return createFluid(fluid.getRegistryName());
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link ResourceLocation}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull ResourceLocation fluidID) {
		return new NSSFluid(fluidID, false);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a tag from a {@link ResourceLocation}
	 */
	@Nonnull
	public static NSSFluid createTag(@Nonnull ResourceLocation tagId) {
		return new NSSFluid(tagId, true);
	}

	/**
	 * Helper method to create an {@link NSSFluid} representing a tag from a {@link Tag<Fluid>}
	 */
	@Nonnull
	public static NSSFluid createTag(@Nonnull Tag<Fluid> tag) {
		return createTag(tag.getId());
	}

	@Override
	protected boolean isInstance(AbstractNSSTag o) {
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
	protected TagCollection<Fluid> getTagCollection() {
		return FluidTags.getCollection();
	}

	@Override
	protected Function<Fluid, NormalizedSimpleStack> createNew() {
		return NSSFluid::createFluid;
	}
}