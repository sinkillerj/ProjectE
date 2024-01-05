package moze_intel.projecte.client.lang;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.client.lang.FormatSplitter.Component;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.NotNull;

/**
 * @apiNote From Mekanism
 */
public abstract class BaseLanguageProvider extends LanguageProvider {

	private final ConvertibleLanguageProvider[] altProviders;

	public BaseLanguageProvider(PackOutput output, String modid) {
		super(output, modid, "en_us");
		altProviders = new ConvertibleLanguageProvider[]{
				new UpsideDownLanguageProvider(output, modid),
				new NonAmericanLanguageProvider(output, modid, "en_au"),
				new NonAmericanLanguageProvider(output, modid, "en_gb")
		};
	}

	protected void add(IHasTranslationKey key, String value) {
		add(key.getTranslationKey(), value);
	}

	@Override
	public void add(@NotNull String key, @NotNull String value) {
		super.add(key, value);
		if (altProviders.length > 0) {
			List<Component> splitEnglish = FormatSplitter.split(value);
			for (ConvertibleLanguageProvider provider : altProviders) {
				provider.convert(key, splitEnglish);
			}
		}
	}

	@NotNull
	@Override
	public CompletableFuture<?> run(@NotNull CachedOutput cache) {
		CompletableFuture<?> future = super.run(cache);
		if (altProviders.length > 0) {
			CompletableFuture<?>[] futures = new CompletableFuture[altProviders.length + 1];
			futures[0] = future;
			for (int i = 0; i < altProviders.length; i++) {
				futures[i + 1] = altProviders[i].run(cache);
			}
			return CompletableFuture.allOf(futures);
		}
		return future;
	}
}