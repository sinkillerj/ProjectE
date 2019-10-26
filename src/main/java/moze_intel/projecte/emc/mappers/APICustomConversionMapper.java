package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.collector.IMappingCollector;
import net.minecraft.resources.IResourceManager;

//TODO: Should this get removed if/when we end up making a way to register custom IEMCMappers
// Given this class is a bit limiting due to the ability to reload recipes
public class APICustomConversionMapper implements IEMCMapper<NormalizedSimpleStack,Long>
{
	public static final APICustomConversionMapper instance = new APICustomConversionMapper();
	private final Map<Object, NormalizedSimpleStack> fakes = new HashMap<>();
	private final Map<String, List<APIConversion>> storedConversions = new HashMap<>();

	@Override
	public String getName()
	{
		return "APICustomConversionMapper";
	}

	@Override
	public String getDescription()
	{
		return "Allows other Mods to add Recipes to the EMC Calculation.";
	}

	@Override
	public boolean isAvailable()
	{
		return true;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, IResourceManager resourceManager)
	{
		for (Map.Entry<String, List<APIConversion>> entry : storedConversions.entrySet())
		{
			String modid = entry.getKey();
			String configKey = getName() + ".allow." + modid;
			if (EMCMapper.getOrSetDefault(config, configKey, "Allow this mod to add conversions to the EMC Calculation", true)) {
				for (APIConversion apiConversion: entry.getValue()) {
					mapper.addConversion(apiConversion.amount, apiConversion.output, apiConversion.ingredients);
				}
			}
		}
	}

	public void addConversion(String modId, int amount, @Nonnull NormalizedSimpleStack output, @Nonnull Map<NormalizedSimpleStack, Integer> ingredients)
	{
		IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
		for (Map.Entry<NormalizedSimpleStack, Integer> entry: ingredients.entrySet())
		{
			ingredientMap.addIngredient(entry.getKey(), entry.getValue());
		}
		storedConversions.computeIfAbsent(modId, s -> new ArrayList<>()).add(new APIConversion(amount, output, ImmutableMap.copyOf(ingredientMap.getMap())));
	}

	public static class APIConversion
	{
		public final int amount;
		public final NormalizedSimpleStack output;
		public final ImmutableMap<NormalizedSimpleStack, Integer> ingredients;

		private APIConversion(int amount, NormalizedSimpleStack output, ImmutableMap<NormalizedSimpleStack, Integer> ingredients)
		{
			this.amount = amount;
			this.output = output;
			this.ingredients = ingredients;
		}
	}
}