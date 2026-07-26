package moze_intel.projecte.emc.mappers.customConversions;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import moze_intel.projecte.api.conversion.CustomConversion;
import moze_intel.projecte.api.conversion.CustomConversionFile;
import moze_intel.projecte.api.mapper.collector.NoOpMappingCollector;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test custom conversion file ordering")
class CustomConversionMapperOrderingTest {

	@AfterEach
	void cleanup() {
		NSSFake.resetNamespace();
	}

	@Test
	@DisplayName("Custom conversion files have stable resource ID precedence")
	void testStableResourceIdPrecedence() {
		NormalizedSimpleStack output = NSSFake.create("output");
		NormalizedSimpleStack alphaIngredient = NSSFake.create("alpha_ingredient");
		NormalizedSimpleStack zetaIngredient = NSSFake.create("zeta_ingredient");
		ResourceLocation alphaId = ResourceLocation.fromNamespaceAndPath("test", "alpha");
		ResourceLocation zetaId = ResourceLocation.fromNamespaceAndPath("test", "zeta");

		CustomConversionFile alpha = createFile(output, 10, alphaIngredient);
		CustomConversionFile zeta = createFile(output, 20, zetaIngredient);

		Map<ResourceLocation, CustomConversionFile> forward = new LinkedHashMap<>();
		forward.put(alphaId, alpha);
		forward.put(zetaId, zeta);
		Map<ResourceLocation, CustomConversionFile> reverse = new LinkedHashMap<>();
		reverse.put(zetaId, zeta);
		reverse.put(alphaId, alpha);

		CollectionResult forwardResult = collect(forward, output);
		CollectionResult reverseResult = collect(reverse, output);

		Assertions.assertEquals(forwardResult, reverseResult, "Backing map iteration order must not change custom conversion precedence");
		Assertions.assertEquals(List.of(10L, 20L), forwardResult.fixedValueOrder());
		Assertions.assertEquals(20L, forwardResult.finalFixedValue());
		Assertions.assertEquals(List.of(alphaIngredient, zetaIngredient), forwardResult.forcedIngredientOrder());
		Assertions.assertEquals(zetaIngredient, forwardResult.finalForcedIngredient());
	}

	private static CustomConversionFile createFile(NormalizedSimpleStack output, long fixedValue, NormalizedSimpleStack ingredient) {
		CustomConversionFile file = new CustomConversionFile();
		file.values().setValueBefore().put(output, fixedValue);
		Object2IntLinkedOpenHashMap<NormalizedSimpleStack> ingredients = new Object2IntLinkedOpenHashMap<>();
		ingredients.put(ingredient, 1);
		file.values().conversions().add(new CustomConversion(1, output, ingredients));
		return file;
	}

	private static CollectionResult collect(Map<ResourceLocation, CustomConversionFile> files, NormalizedSimpleStack output) {
		RecordingCollector collector = new RecordingCollector();
		CustomConversionMapper.addMappingsFromFiles(files, collector);
		return new CollectionResult(
				List.copyOf(collector.fixedValueOrder),
				collector.fixedValues.get(output),
				List.copyOf(collector.forcedIngredientOrder),
				collector.forcedIngredients.get(output)
		);
	}

	private record CollectionResult(List<Long> fixedValueOrder, Long finalFixedValue, List<NormalizedSimpleStack> forcedIngredientOrder,
			NormalizedSimpleStack finalForcedIngredient) {
	}

	private static class RecordingCollector extends NoOpMappingCollector<NormalizedSimpleStack, Long> {

		private final List<Long> fixedValueOrder = new ArrayList<>();
		private final Map<NormalizedSimpleStack, Long> fixedValues = new HashMap<>();
		private final List<NormalizedSimpleStack> forcedIngredientOrder = new ArrayList<>();
		private final Map<NormalizedSimpleStack, NormalizedSimpleStack> forcedIngredients = new HashMap<>();

		@Override
		public void setValueBefore(NormalizedSimpleStack something, Long value) {
			fixedValueOrder.add(value);
			fixedValues.put(something, value);
		}

		@Override
		public void setValueFromConversion(int outnumber, NormalizedSimpleStack something, Object2IntMap<NormalizedSimpleStack> ingredientsWithAmount) {
			NormalizedSimpleStack ingredient = ingredientsWithAmount.keySet().iterator().next();
			forcedIngredientOrder.add(ingredient);
			forcedIngredients.put(something, ingredient);
		}
	}
}
