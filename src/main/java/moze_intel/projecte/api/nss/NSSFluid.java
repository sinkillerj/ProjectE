package moze_intel.projecte.api.nss;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public final class NSSFluid implements NSSTag {

	@Nonnull
	private final ResourceLocation fluidName;
	private final boolean isTag;

	private NSSFluid(Fluid fluid) {
		//This should never be null or it would have crashed on being registered
		fluidName = fluid.getRegistryName();
		isTag = false;
	}

	private NSSFluid(@Nonnull ResourceLocation tagId) {
		fluidName = tagId;
		isTag = false;
	}

	@Nonnull
	public static NSSFluid createFluid(@Nonnull Fluid fluid) {
		return new NSSFluid(fluid);
	}

	@Nonnull
	public static NSSFluid createTag(@Nonnull ResourceLocation tagId) {
		return new NSSFluid(tagId);
	}

	@Nonnull
	public static NSSFluid createTag(@Nonnull Tag<Fluid> tag) {
		//TODO: Decide if we want to store it as a tag or the resource location of the tag
		return createTag(tag.getId());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof NSSFluid) {
			NSSFluid other = (NSSFluid) o;
			return isTag == other.isTag && fluidName.equals(other.fluidName);
		}
		return false;
	}

	@Override
	public String json() {
		if (isTag) {
			return "FLUID|#" + fluidName;
		}
		return "FLUID|" + fluidName;
	}

	@Override
	public int hashCode() {
		if (isTag) {
			return 31 + fluidName.hashCode();
		}
		return fluidName.hashCode();
	}

	@Override
	public String toString() {
		if (isTag) {
			return "Fluid Tag: " + fluidName;
		}
		return "Fluid: " + fluidName;
	}

	@Override
	public void forEachElement(Consumer<NormalizedSimpleStack> consumer) {
		if (isTag) {
			Tag<Fluid> tag = FluidTags.getCollection().get(fluidName);
			if (tag == null) {
				//TODO: Decide what to do about this warning given for example it theoretically will be thrown each time for milk if there is no milk loaded
				//TODO: FIXME this logger should not be accessed by the API package. Move over to multiple sourcesets to make it easier to not accidentally access non API?
				PECore.LOGGER.warn("Couldn't find fluid tag {}", fluidName);
			} else {
				tag.getAllElements().stream().map(NSSFluid::createFluid).forEach(consumer);
			}
		}
	}
}
