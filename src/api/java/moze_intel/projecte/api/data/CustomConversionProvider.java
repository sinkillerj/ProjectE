package moze_intel.projecte.api.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;

/**
 * Base Data Generator Provider class for use in creating custom conversion json data files that ProjectE will read from the data pack.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CustomConversionProvider implements DataProvider {

	private final Map<ResourceLocation, CustomConversionBuilder> customConversions = new LinkedHashMap<>();
	private final CompletableFuture<HolderLookup.Provider> lookupProvider;
	private final Path outputFolder;

	protected CustomConversionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		this.outputFolder = output.getOutputFolder(Target.DATA_PACK);
		this.lookupProvider = lookupProvider;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return this.lookupProvider.thenApply(registries -> {
			customConversions.clear();
			addCustomConversions(registries);
			return registries;
		}).thenCompose(registries -> {
			List<CompletableFuture<?>> futures = new ArrayList<>();
			for (Map.Entry<ResourceLocation, CustomConversionBuilder> entry : customConversions.entrySet()) {
				ResourceLocation customConversion = entry.getKey();
				Path path = outputFolder.resolve(customConversion.getNamespace() + "/pe_custom_conversions/" + customConversion.getPath() + ".json");
				futures.add(DataProvider.saveStable(output, entry.getValue().serialize(), path));
			}
			return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
		});
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
	protected CustomConversionBuilder createConversionBuilder(ResourceLocation id) {
		Objects.requireNonNull(id, "Custom Conversion Builder ID cannot be null.");
		if (customConversions.containsKey(id)) {
			throw new RuntimeException("Custom conversion '" + id + "' has already been registered.");
		}
		CustomConversionBuilder conversionBuilder = new CustomConversionBuilder(id);
		customConversions.put(id, conversionBuilder);
		return conversionBuilder;
	}

	@Override
	public String getName() {
		return "Custom EMC Conversions";
	}
}