package moze_intel.projecte.client.integration.emi;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.TagEmiIngredient;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.registry.EmiInitRegistryImpl;
import dev.emi.emi.registry.EmiPluginContainer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.integration.recipe_viewer.alias.IAliasMapping;
import moze_intel.projecte.integration.recipe_viewer.alias.RVAliasHelper;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.PathProvider;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

/**
 * From Mekanism
 */
public class EmiAliasProvider implements DataProvider, RVAliasHelper<EmiIngredient> {

	private static boolean emiSerializersInitialized;

	//TODO: Remove the need for us bootstrapping it manually: https://github.com/emilyploszaj/emi/issues/537
	private static void bootstrapEmi() {
		if (!emiSerializersInitialized) {
			emiSerializersInitialized = true;
			//Bootstrap the initialization stage of emi plugins so that AliasInfo.INGREDIENT_CODEC has the backing
			// EmiIngredientSerializers present for it to wrap
			EmiInitRegistry initRegistry = new EmiInitRegistryImpl();
			for (EmiPluginContainer container : EmiAgnos.getPlugins().stream().sorted(Comparator.comparingInt(container -> container.id().equals("emi") ? 0 : 1)).toList()) {
				container.plugin().initialize(initRegistry);
			}
		}
	}

	private final CompletableFuture<HolderLookup.Provider> registries;
	private final SequencedSet<AliasInfo> data = new LinkedHashSet<>();
	private final Supplier<IAliasMapping> mappings;
	private final PathProvider pathProvider;
	private final String modid;

	public EmiAliasProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid, Supplier<IAliasMapping> mappings) {
		this.pathProvider = output.createPathProvider(Target.RESOURCE_PACK, "aliases");
		this.registries = registries;
		this.modid = modid;
		this.mappings = mappings;
	}

	@NotNull
	@Override
	public final CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
		bootstrapEmi();
		return this.registries.thenCompose(lookupProvider -> {
			IAliasMapping mapping = mappings.get();
			mapping.addAliases(this);
			Path path = pathProvider.json(Identifier.fromNamespaceAndPath(IntegrationHelper.EMI_MODID, modid));
			return DataProvider.saveStable(cachedOutput, lookupProvider, AliasInfo.SEQUENCED_SET_CODEC, data, path);
		});
	}

	@Override
	public EmiIngredient ingredient(ItemLike itemLike) {
		return EmiStack.of(itemLike);
	}

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public List<EmiIngredient> tagContents(TagKey<Item> tag) {
		//Note: We can't use the method in EmiIngredient as it does checks against things that aren't initialized in datagen
		return List.of(new TagEmiIngredient(tag, 1));
	}

	@Override
	public void addAliases(List<EmiIngredient> stacks, IHasTranslationKey... aliases) {
		if (aliases.length == 0) {
			throw new IllegalArgumentException("Expected to have at least one alias");
		} else if (stacks.isEmpty()) {
			throw new IllegalArgumentException("Expected to have at least ingredient");
		}
		//Sort the translation key aliases so that our datagen output is more stable
		List<String> sortedAliases = Arrays.stream(aliases)
				.map(IHasTranslationKey::getTranslationKey)
				.sorted()
				.toList();
		//TODO: Is there some global sort, or stack based sort we can apply as well?
		if (!data.add(new AliasInfo(stacks, sortedAliases))) {
			//TODO: Can we improve the validation we have relating to duplicate values/make things more compact?
			// This if statement exists mainly as a simple check against copy-paste errors
			throw new IllegalStateException("Duplicate alias pair added");
		}
	}

	@NotNull
	@Override
	public String getName() {
		return "EMI Alias Provider: " + modid;
	}

	private record AliasInfo(List<EmiIngredient> ingredients, List<String> aliases) {

		private static final Codec<EmiIngredient> INGREDIENT_CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
					JsonElement element = dynamic.convert(JsonOps.INSTANCE).getValue();
					EmiIngredient ingredient = EmiIngredientSerializer.getDeserialized(element);
					return ingredient.isEmpty() ? DataResult.error(() -> "Empty or invalid ingredient") : DataResult.success(ingredient);
				}, ingredient -> new Dynamic<>(JsonOps.INSTANCE, EmiIngredientSerializer.getSerialized(ingredient))
		);
		private static final Codec<AliasInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				singleOrListCodec(INGREDIENT_CODEC).fieldOf("stacks").forGetter(AliasInfo::ingredients),
				singleOrListCodec(ExtraCodecs.NON_EMPTY_STRING).fieldOf("text").forGetter(AliasInfo::aliases)
		).apply(instance, AliasInfo::new));
		private static final Codec<SequencedSet<AliasInfo>> SEQUENCED_SET_CODEC = ExtraCodecs.nonEmptyList(CODEC.listOf())
				.<SequencedSet<AliasInfo>>xmap(LinkedHashSet::new, List::copyOf).fieldOf("aliases").codec();

		private static <T> Codec<List<T>> singleOrListCodec(Codec<T> codec) {
			return Codec.either(codec, ExtraCodecs.nonEmptyList(codec.listOf())).xmap(
					either -> either.map(List::of, Function.identity()),
					list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list)
			);
		}
	}
}
