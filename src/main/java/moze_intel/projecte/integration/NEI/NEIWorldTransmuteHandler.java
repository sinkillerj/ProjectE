package moze_intel.projecte.integration.NEI;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.MetaBlock;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.awt.*;
import java.util.Map.Entry;

public class NEIWorldTransmuteHandler extends TemplateRecipeHandler
{

	private static String name = StatCollector.translateToLocal("pe.nei.worldtransmute");
	private static String id = "worldTransmutation";

	@Override
	public String getRecipeName()
	{
		return name;
	}

	@Override
	public String getGuiTexture()
	{
		return "projecte:textures/gui/nei.png";
	}

	public class CachedTransmutationRecipe extends CachedRecipe
	{

		private MetaBlock input;
		private MetaBlock output;
		public boolean sneaking;

		public CachedTransmutationRecipe(MetaBlock in, boolean sneak)
		{

			input = in;
			sneaking = sneak;
			output = WorldTransmutations.getWorldTransmutation(in, sneaking);

		}

		@Override
		public PositionedStack getIngredient()
		{
			ItemStack is = input.toItemStack();
			return new PositionedStack(is, 22, 23);

		}


		@Override
		public PositionedStack getResult()
		{
			ItemStack result = output.toItemStack();
			return new PositionedStack(result, 128, 23);

		}

		@Override
		public PositionedStack getOtherStack()
		{
			return new PositionedStack(new ItemStack(ObjHandler.philosStone), 60, 23);
		}

	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results)
	{
		if (outputId.equals(id) && getClass() == NEIWorldTransmuteHandler.class)
		{
			for (Entry<MetaBlock, MetaBlock[]> entry : WorldTransmutations.MAP.entrySet())
			{
				if (entry != null)
				{
					if (entry.getValue()[0] != null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(), false));
					if (entry.getValue()[1] != null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(), true));
				}
			}
		} else
		{
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result)
	{
		for (Entry<MetaBlock, MetaBlock[]> entry : WorldTransmutations.MAP.entrySet())
		{
			if (NEIServerUtils.areStacksSameTypeCrafting(entry.getValue()[0].toItemStack(), result))
			{
				if (entry != null && entry.getValue() != null)
					arecipes.add(new CachedTransmutationRecipe(entry.getKey(), false));
			} else if (NEIServerUtils.areStacksSameTypeCrafting(entry.getValue()[1].toItemStack(), result))
			{
				if (entry != null && entry.getValue() != null)
					arecipes.add(new CachedTransmutationRecipe(entry.getKey(), true));
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient)
	{
		for (Entry<MetaBlock, MetaBlock[]> entry : WorldTransmutations.MAP.entrySet())
		{
			if (NEIServerUtils.areStacksSameTypeCrafting(entry.getKey().toItemStack(), ingredient))
			{
				if (entry != null)
				{
					if (entry.getValue()[0] != null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(), false));
					if (entry.getValue()[1] != null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(), true));
				}
			}
		}
	}

	@Override
	public void drawForeground(int recipe)
	{
		String sneak = StatCollector.translateToLocal("key.sneak");

		CachedTransmutationRecipe r = (CachedTransmutationRecipe) arecipes.get(recipe);

		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		if (r.sneaking) fr.drawString(sneak, 70, 40, 0);

	}


	@Override
	public void loadTransferRects()
	{
		this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(83, 23, 25, 10), id));
	}


}