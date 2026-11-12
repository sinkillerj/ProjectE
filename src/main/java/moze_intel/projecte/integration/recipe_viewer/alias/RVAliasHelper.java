package moze_intel.projecte.integration.recipe_viewer.alias;

import java.util.Collection;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/**
 * From Mekanism
 */
public interface RVAliasHelper<ITEM> {

	ITEM ingredient(ItemLike itemLike);

	List<ITEM> tagContents(TagKey<Item> tag);

	default void addAliases(ItemLike item, IHasTranslationKey... aliases) {
		addAliases(ingredient(item), aliases);
	}

	default void addAliases(TagKey<Item> tag, IHasTranslationKey... aliases) {
		if (aliases.length == 0) {
			PECore.LOGGER.warn("Expected to have at least one alias for item tag: {}", tag.location());
		} else {
			addAliases(tagContents(tag), aliases);
		}
	}

	default void addAliases(Collection<? extends ItemLike> items, IHasTranslationKey... aliases) {
		addAliases(items.stream().map(this::ingredient).toList(), aliases);
	}

	default void addAliases(ITEM item, IHasTranslationKey... aliases) {
		addAliases(List.of(item), aliases);
	}

	void addAliases(List<ITEM> stacks, IHasTranslationKey... aliases);
}
