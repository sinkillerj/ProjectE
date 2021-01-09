package moze_intel.projecte.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

/**
 * Base Data Generator Provider class for use in creating custom conversion json data files that ProjectE will read from the data pack.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CustomConversionProvider implements IDataProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Map<ResourceLocation, CustomConversionBuilder> customConversions = new LinkedHashMap<>();
	private final DataGenerator generator;

	protected CustomConversionProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public final void act(DirectoryCache cache) throws IOException {
		customConversions.clear();
		addCustomConversions();
		for (Map.Entry<ResourceLocation, CustomConversionBuilder> entry : customConversions.entrySet()) {
			ResourceLocation customConversion = entry.getKey();
			Path path = generator.getOutputFolder().resolve("data/" + customConversion.getNamespace() + "/pe_custom_conversions/" + customConversion.getPath() + ".json");
			try {
				IDataProvider.save(GSON, cache, entry.getValue().serialize(), path);
			} catch (IOException e) {
				throw new RuntimeException("Couldn't save custom conversion file for conversion: " + customConversion, e);
			}
		}
	}

	/**
	 * Implement this method to add any custom conversion files.
	 */
	protected abstract void addCustomConversions();

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