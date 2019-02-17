package moze_intel.projecte.gameObjs.customRecipes;

import com.google.gson.JsonObject;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Composes a ShapelessRecipe to hide it from JEI and the book
 */
public class RecipeShapelessHidden implements IRecipe
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
	public boolean matches(@Nonnull IInventory inv, @Nonnull World worldIn) {
		return compose.matches(inv, worldIn);
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull IInventory inv) {
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
	public NonNullList<ItemStack> getRemainingItems(IInventory inv)
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

	public static class Serializer implements IRecipeSerializer<RecipeShapelessHidden>
	{
		private static final ResourceLocation TYPE_ID = new ResourceLocation(PECore.MODID, "shapeless_recipe_hidden");

		@Nonnull
		@Override
		public RecipeShapelessHidden read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json)
		{
			return new RecipeShapelessHidden(RecipeSerializers.CRAFTING_SHAPELESS.read(recipeId, json));
		}

		@Nonnull
		@Override
		public RecipeShapelessHidden read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer)
		{
			return new RecipeShapelessHidden(RecipeSerializers.CRAFTING_SHAPELESS.read(recipeId, buffer));
		}

		@Override
		public void write(@Nonnull PacketBuffer buffer, @Nonnull RecipeShapelessHidden recipe)
		{
			RecipeSerializers.CRAFTING_SHAPELESS.write(buffer, recipe.compose);
		}

		@Nonnull
		@Override
		public ResourceLocation getName()
		{
			return TYPE_ID;
		}
	}
}