package moze_intel.projecte.emc.json;

import moze_intel.projecte.PECore;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NSSTag implements NormalizedSimpleStack {
	static final Map<String, NSSTag> tagStacks = new HashMap<>();

	private final ResourceLocation tagId;

	private NSSTag(ResourceLocation tagId) {
		this.tagId = tagId;
	}

	public static NormalizedSimpleStack create(String oreDictionaryName) {
		return tagStacks.computeIfAbsent(oreDictionaryName, s -> new NSSTag(new ResourceLocation(s)));
	}

	public Iterable<Item> getAllElements()
	{
		Tag<Item> tag = ItemTags.getCollection().get(tagId);
		if (tag == null)
		{
			PECore.LOGGER.warn("Couldn't find tag {}", tagId);
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
		return o instanceof NSSTag && this.tagId.equals(((NSSTag) o).tagId);
	}

	@Override
	public String json() {
		return "#" + this.tagId;
	}

	@Override
	public String toString() {
		return "#" + tagId;
	}
}
