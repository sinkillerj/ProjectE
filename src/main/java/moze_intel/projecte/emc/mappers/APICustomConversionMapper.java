package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.json.NSSFake;
import moze_intel.projecte.emc.json.NSSFluid;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.ClassUtils;

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

	public void addConversion(String modId, int amount, @Nonnull Object output, @Nonnull Map<Object, Integer> ingredients)
	{
		NormalizedSimpleStack nssOut = objectToNSS(modId, output);
		IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
		for (Map.Entry<Object, Integer> entry: ingredients.entrySet())
		{
			NormalizedSimpleStack nss = objectToNSS(modId, entry.getKey());
			ingredientMap.addIngredient(nss, entry.getValue());
		}
		storedConversions.computeIfAbsent(modId, s -> new ArrayList<>())
				.add(new APIConversion(amount, nssOut, ImmutableMap.copyOf(ingredientMap.getMap())));
	}

	NormalizedSimpleStack objectToNSS(String modId, Object object)
	{
		if (object instanceof Block) {
			return NSSItem.createItem((Block) object);
		} else if (object instanceof Item) {
			return NSSItem.createItem((Item) object);
		} else if (object instanceof ItemStack) {
			return NSSItem.createItem((ItemStack) object);
		} else if (object instanceof FluidStack) {
			return NSSFluid.createFluid(((FluidStack) object).getFluid());
		} else if (object instanceof ResourceLocation) {
			//TODO: 1.14, Figure out if this should be an item or a fluid
			return NSSItem.createTag((ResourceLocation) object);
		} else if (object != null && object.getClass() == Object.class) {
			return fakes.computeIfAbsent(object, o -> NSSFake.create("" + object + " by " + modId));
		} else {
			throw new IllegalArgumentException("Cannot turn " + object + " (" + ClassUtils.getPackageCanonicalName(object, "") + ") into NormalizedSimpleStack. need ItemStack, FluidStack, ResourceLocation or 'Object'");
		}
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