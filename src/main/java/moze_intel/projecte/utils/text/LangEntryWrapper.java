package moze_intel.projecte.utils.text;

import net.minecraft.util.IItemProvider;

public class LangEntryWrapper implements ILangEntry {

	private final IItemProvider itemProvider;

	public LangEntryWrapper(IItemProvider itemProvider) {
		this.itemProvider = itemProvider;
	}

	@Override
	public String getTranslationKey() {
		return itemProvider.asItem().getDescriptionId();
	}
}