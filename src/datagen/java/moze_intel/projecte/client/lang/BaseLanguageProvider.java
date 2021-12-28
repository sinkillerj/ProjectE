package moze_intel.projecte.client.lang;

import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.client.lang.FormatSplitter.Component;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraftforge.common.data.LanguageProvider;

/**
 * @apiNote From Mekanism
 */
public abstract class BaseLanguageProvider extends LanguageProvider {

	private final ConvertibleLanguageProvider[] altProviders;

	public BaseLanguageProvider(DataGenerator gen, String modid) {
		super(gen, modid, "en_us");
		altProviders = new ConvertibleLanguageProvider[]{
				new UpsideDownLanguageProvider(gen, modid)
		};
	}

	protected void add(IHasTranslationKey key, String value) {
		add(key.getTranslationKey(), value);
	}

	@Override
	public void add(@Nonnull String key, @Nonnull String value) {
		super.add(key, value);
		if (altProviders.length > 0) {
			List<Component> splitEnglish = FormatSplitter.split(value);
			for (ConvertibleLanguageProvider provider : altProviders) {
				provider.convert(key, splitEnglish);
			}
		}
	}

	@Override
	public void run(@Nonnull HashCache cache) throws IOException {
		super.run(cache);
		if (altProviders.length > 0) {
			for (ConvertibleLanguageProvider provider : altProviders) {
				provider.run(cache);
			}
		}
	}
}