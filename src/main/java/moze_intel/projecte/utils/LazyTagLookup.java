package moze_intel.projecte.utils;

import net.minecraft.tags.TagKey;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;

//From Mekanism
public record LazyTagLookup<TYPE extends IForgeRegistryEntry<TYPE>>(TagKey<TYPE> key, Lazy<ITag<TYPE>> lazyTag) {

	public static <TYPE extends IForgeRegistryEntry<TYPE>> LazyTagLookup<TYPE> create(IForgeRegistry<TYPE> registry, TagKey<TYPE> key) {
		return new LazyTagLookup<>(key, Lazy.of(() -> tagManager(registry).getTag(key)));
	}

	public ITag<TYPE> tag() {
		return lazyTag.get();
	}

	public boolean contains(TYPE element) {
		return tag().contains(element);
	}

	public boolean isEmpty() {
		return tag().isEmpty();
	}

	public static <TYPE extends IForgeRegistryEntry<TYPE>> ITagManager<TYPE> tagManager(IForgeRegistry<TYPE> registry) {
		ITagManager<TYPE> tags = registry.tags();
		if (tags == null) {
			throw new IllegalStateException("Expected " + registry.getRegistryName() + " to have tags.");
		}
		return tags;
	}
}