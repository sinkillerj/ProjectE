package moze_intel.projecte.emc.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import moze_intel.projecte.PECore;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class NSSFluidTag implements NormalizedSimpleStack {
	static final Map<String, NSSFluidTag> tagStacks = new HashMap<>();

	private final ResourceLocation tagId;

	private NSSFluidTag(ResourceLocation tagId) {
		this.tagId = tagId;
	}

	public static NormalizedSimpleStack create(String oreDictionaryName) {
		return tagStacks.computeIfAbsent(oreDictionaryName, s -> new NSSFluidTag(new ResourceLocation(s)));
	}

	public static NormalizedSimpleStack create(ResourceLocation tagId) {
		return tagStacks.computeIfAbsent(tagId.toString(), s -> new NSSFluidTag(tagId));
	}

	public Iterable<Fluid> getAllElements()
	{
		Tag<Fluid> tag = FluidTags.getCollection().get(tagId);
		if (tag == null)
		{
			PECore.LOGGER.warn("Couldn't find fluid tag {}", tagId);
			return Collections.emptyList();
		}
		return tag.getAllElements();
	}

	@Override
	public int hashCode() {
		return tagId.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof NSSFluidTag && this.tagId.equals(((NSSFluidTag) o).tagId);
	}

	@Override
	public String json() {
		return "FLUID|#" + this.tagId;
	}

	@Override
	public String toString() {
		return "FLUID|#" + tagId;
	}
}
