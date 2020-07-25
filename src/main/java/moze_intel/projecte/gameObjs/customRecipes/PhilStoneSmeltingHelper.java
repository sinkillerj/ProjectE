package moze_intel.projecte.gameObjs.customRecipes;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

//Note: Has to be IResourceManagerReloadListener, so that it works properly on servers
public class PhilStoneSmeltingHelper implements IResourceManagerReloadListener {

	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
		//TODO - 1.16: Replace philo stone smelting recipe system with just using a single special recipe and a custom serializer for it
		// It will make it less of a hack and the only downside is that then it is slightly harder for users to just remove a single specific
		// recipe type
		//TODO: FIX-ME, we are unable to check if our data pack is loaded as the world is not loaded yet the first time this is called
		// Note: This does not currently matter as mod data packs cannot be properly disabled: https://github.com/MinecraftForge/MinecraftForge/issues/5506
		// Once they can be, we should check if these recipes properly also get disabled due to being in projecte's namespace
		if (ServerLifecycleHooks.getCurrentServer() != null) {//It is null the first time the reload listener runs
			RecipeManager manager = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
			if (!(manager.recipes instanceof HashMap<?, ?>)) {
				//If the recipe map has not been changed to mutable from immutable by someone else (like CraftTweaker), then we need to make it mutable
				// This way we don't have to do extra copies when there is no reason to
				manager.recipes = new HashMap<>(manager.recipes);
			}
			Map<ResourceLocation, IRecipe<CraftingInventory>> craftingRecipes = manager.getRecipes(IRecipeType.CRAFTING);
			if (!(craftingRecipes instanceof HashMap<?, ?>)) {
				//If the crafting recipe map has not been changed to mutable from immutable by someone else (like CraftTweaker), then we need to make it mutable
				// This way we don't have to do extra copies when there is no reason to
				manager.recipes.put(IRecipeType.CRAFTING, new HashMap<>(craftingRecipes));
				craftingRecipes = manager.getRecipes(IRecipeType.CRAFTING);
			}
			for (IRecipe<IInventory> r : manager.getRecipes(IRecipeType.SMELTING).values()) {
				if (r.getIngredients().isEmpty() || r.getIngredients().get(0).hasNoMatchingItems() || r.getRecipeOutput().isEmpty()) {
					continue;
				}

				Ingredient input = r.getIngredients().get(0);
				ItemStack output = r.getRecipeOutput().copy();
				output.setCount(output.getCount() * 7);

				String inputName = r.getId().toString().replace(':', '_');
				ResourceLocation recipeName = new ResourceLocation(PECore.MODID, "philstone_smelt_" + inputName);

				NonNullList<Ingredient> ingrs = NonNullList.from(Ingredient.EMPTY,
						Ingredient.fromItems(ObjHandler.philosStone),
						input, input, input, input, input, input, input,
						Ingredient.fromTag(ItemTags.COALS));
				craftingRecipes.put(recipeName, new RecipeShapelessHidden(recipeName, "projecte:philstone_smelt", output, ingrs));
			}
		}
	}
}