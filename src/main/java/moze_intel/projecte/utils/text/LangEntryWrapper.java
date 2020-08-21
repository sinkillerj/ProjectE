package moze_intel.projecte.utils.text;

public class LangEntryWrapper implements ILangEntry {

	private final String translationKey;

	//TODO: Replace this with something that can go based on the items directly
	public LangEntryWrapper(String translationKey) {
		this.translationKey = translationKey;
	}

	@Override
	public String getTranslationKey() {
		return translationKey;
	}
}