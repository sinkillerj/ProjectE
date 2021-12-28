package moze_intel.projecte.utils.text;

import net.minecraft.world.level.ItemLike;

public class LangEntryWrapper implements ILangEntry {

	private final ItemLike itemProvider;

	public LangEntryWrapper(ItemLike itemProvider) {
		this.itemProvider = itemProvider;
	}

	@Override
	public String getTranslationKey() {
		return itemProvider.asItem().getDescriptionId();
	}
}