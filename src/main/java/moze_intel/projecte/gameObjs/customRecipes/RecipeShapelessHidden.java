package moze_intel.projecte.gameObjs.customRecipes;

import com.google.gson.JsonObject;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Composes a ShapelessRecipe to hide it from JEI and the book
 */
public class RecipeShapelessHidden implements ICraftingRecipe
{
	private final ShapelessRecipe compose;

	public RecipeShapelessHidden(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients)
	{
		this.compose = new ShapelessRecipe(id, group, result, ingredients);
	}

	private RecipeShapelessHidden(ShapelessRecipe compose)
	{
		this.compose = compose;
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World worldIn) {
		return compose.matches(inv, worldIn);
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
		return compose.getCraftingResult(inv);
	}

	@Override
	public boolean canFit(int width, int height) {
		return compose.canFit(width, height);
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return compose.getRecipeOutput();
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv)
	{
		return compose.getRemainingItems(inv);
	}

	@Nonnull
	@Override
	public NonNullList<Ingredient> getIngredients()
	{
		return compose.getIngredients();
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}

	@Nonnull
	@Override
	public String getGroup()
	{
		return compose.getGroup();
	}

	@Nonnull
	@Override
	public ResourceLocation getId()
	{
		return compose.getId();
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		return ObjHandler.SHAPELESS_HIDDEN_SERIALIZER;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeShapelessHidden>
	{
		@Nonnull
		@Override
		public RecipeShapelessHidden read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json)
		{
			return new RecipeShapelessHidden(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, json));
		}

		@Nonnull
		@Override
		public RecipeShapelessHidden read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer)
		{
			return new RecipeShapelessHidden(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, buffer));
		}

		@Override
		public void write(@Nonnull PacketBuffer buffer, @Nonnull RecipeShapelessHidden recipe)
		{
			IRecipeSerializer.CRAFTING_SHAPELESS.write(buffer, recipe.compose);
		}
	}
}