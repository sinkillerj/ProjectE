package moze_intel.projecte.api.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.api.conversion.CustomConversionFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.Identifier;

/**
 * Base Data Generator Provider class for use in creating custom conversion json data files that ProjectE will read from the data pack.
 */
@ParametersAreNonnullByDefault
public abstract class CustomConversionProvider implements DataProvider {

	private final Map<Identifier, CustomConversionBuilder> customConversions = new LinkedHashMap<>();
	private final CompletableFuture<HolderLookup.Provider> lookupProvider;
	private final PathProvider outputProvider;
	private final String modid;

	protected CustomConversionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modid) {
		this.outputProvider = output.createPathProvider(Target.DATA_PACK, "pe_custom_conversions");
		this.lookupProvider = lookupProvider;
		this.modid = modid;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return this.lookupProvider.thenApply(registries -> {
			customConversions.clear();
			addCustomConversions(registries);
			return registries;
		}).thenCompose(registries -> CompletableFuture.allOf(customConversions.entrySet().stream()
				.map(entry -> DataProvider.saveStable(output, registries, CustomConversionFile.CODEC, entry.getValue().build(), outputProvider.json(entry.getKey())))
				.toArray(CompletableFuture[]::new)
		));
	}

	/**
	 * Implement this method to add any custom conversion files.
	 *
	 * @param registries Access to holder lookups.
	 */
	protected abstract void addCustomConversions(HolderLookup.Provider registries);

	/**
	 * Creates and adds a custom conversion builder with the file located by data/modid/pe_custom_conversions/namespace.json
	 *
	 * @param id modid:namespace
	 *
	 * @return Builder
	 */
	protected CustomConversionBuilder createConversionBuilder(Identifier id) {
		Objects.requireNonNull(id, "Custom Conversion Builder ID cannot be null.");
		if (customConversions.containsKey(id)) {
			throw new RuntimeException("Custom conversion '" + id + "' has already been registered.");
		}
		CustomConversionBuilder conversionBuilder = new CustomConversionBuilder();
		customConversions.put(id, conversionBuilder);
		return conversionBuilder;
	}

	@Override
	public final String getName() {
		return "Custom EMC Conversions: " + modid;
	}
}
