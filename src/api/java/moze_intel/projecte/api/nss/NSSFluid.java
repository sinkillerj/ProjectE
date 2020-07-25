package moze_intel.projecte.api.nss;

import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

/**
 * Implementation of {@link NormalizedSimpleStack} and {@link NSSTag} for representing {@link Fluid}s.
 */
public final class NSSFluid extends AbstractNBTNSSTag<Fluid> {

	private NSSFluid(@Nonnull ResourceLocation resourceLocation, boolean isTag, @Nullable CompoundNBT nbt) {
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
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link Fluid} and an optional {@link CompoundNBT}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull Fluid fluid, @Nullable CompoundNBT nbt) {
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
	 * Helper method to create an {@link NSSFluid} representing a fluid from a {@link ResourceLocation} and an optional {@link CompoundNBT}
	 */
	@Nonnull
	public static NSSFluid createFluid(@Nonnull ResourceLocation fluidID, @Nullable CompoundNBT nbt) {
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
	 * Helper method to create an {@link NSSFluid} representing a tag from a {@link ITag<Fluid>}
	 */
	@Nonnull
	public static NSSFluid createTag(@Nonnull ITag<Fluid> tag) {
		//TODO - 1.16: Evaluate if this should use FluidTags#getCollection. I believe the below is correct as this happens in a reload listener so before FluidTags is updated
		ResourceLocation tagLocation = TagCollectionManager.func_232928_e_().func_232926_c_().func_232973_a_(tag);
		if (tagLocation == null) {
			throw new IllegalArgumentException("Can't make NSSFluid with a tag that does not exist");
		}
		return createTag(tagLocation);
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
	protected TagCollection<Fluid> getTagCollection() {
		return FluidTags.getCollection();
	}

	@Override
	protected Function<Fluid, NormalizedSimpleStack> createNew() {
		return NSSFluid::createFluid;
	}
}