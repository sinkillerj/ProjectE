package moze_intel.projecte.client.integration.emi;

import com.mojang.serialization.Codec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

/**
 * From Mekanism
 */
public abstract class BaseEmiDefaults implements DataProvider {

	private static final Codec<List<Identifier>> CODEC = ExtraCodecs.nonEmptyList(Identifier.CODEC.listOf())
			.fieldOf("added")
			.codec();

	private final CompletableFuture<HolderLookup.Provider> registries;
	private final Set<Identifier> recipes = new HashSet<>();
	private final PathProvider pathProvider;
	private final String modid;

	protected BaseEmiDefaults(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
		this.pathProvider = output.createPathProvider(Target.RESOURCE_PACK, "recipe/defaults");
		this.registries = registries;
		this.modid = modid;
	}

	@NotNull
	@Override
	public String getName() {
		return "EMI Default Recipe Provider: " + modid;
	}

	@NotNull
	@Override
	public final CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
		return this.registries.thenCompose(lookupProvider -> {
			addDefaults(lookupProvider);
			//Sort to make the output more stable
			List<Identifier> sortedRecipes = new ArrayList<>(recipes);
			sortedRecipes.sort(Identifier::compareNamespaced);
			Path path = pathProvider.json(Identifier.fromNamespaceAndPath(IntegrationHelper.EMI_MODID, modid));
			return DataProvider.saveStable(cachedOutput, lookupProvider, CODEC, sortedRecipes, path);
		});
	}

	protected abstract void addDefaults(HolderLookup.Provider lookupProvider);

	protected void addRecipe(ItemLike output) {
		Identifier registryName = BuiltInRegistries.ITEM.getResourceKey(output.asItem())
				.map(ResourceKey::location)
				.orElseThrow(() -> new IllegalStateException("Could not retrieve registry name for output."));
		addRecipe(registryName);
	}

	protected void addRecipe(String recipePath) {
		addRecipe(Identifier.fromNamespaceAndPath(modid, recipePath));
	}

	protected void addRecipe(Identifier recipe) {
		addUncheckedRecipe(recipe);
	}

	protected void addUncheckedRecipe(Identifier recipe) {
		if (!recipes.add(recipe)) {
			throw new IllegalArgumentException("Recipe '" + recipe + "' was added multiple times.");
		}
	}
}
