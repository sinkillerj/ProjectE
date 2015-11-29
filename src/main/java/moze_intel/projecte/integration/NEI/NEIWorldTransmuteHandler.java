/*
 todo 1.8.8 restore when NEI updates
package moze_intel.projecte.integration.NEI;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

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

		private IBlockState input;
		private IBlockState output;
		public boolean sneaking;

		public CachedTransmutationRecipe(IBlockState in, boolean sneak)
		{

			input = in;
			sneaking = sneak;
			output = WorldTransmutations.getWorldTransmutation(in, sneaking);
		}

		@Override
		public PositionedStack getIngredient()
		{
			return new PositionedStack(getStack(input), 22, 23);
		}

		@Override
		public PositionedStack getResult()
		{
			return new PositionedStack(getStack(output), 128, 23);
		}

	    @Override
	    public PositionedStack getOtherStack(){
	    	return new PositionedStack(new ItemStack(ObjHandler.philosStone), 60, 23);
	    }

		private ItemStack getStack(IBlockState state)
		{
			Fluid f = FluidRegistry.lookupFluidForBlock(state.getBlock());
			if (f != null)
			{
				for (FluidContainerRegistry.FluidContainerData fd : FluidContainerRegistry.getRegisteredFluidContainerData())
				{
					if (fd.fluid.getFluid() == f)
					{
						// Cheat, find first container for this fluid
						return fd.filledContainer.copy().setStackDisplayName(f.getLocalizedName(fd.fluid));
					}
				}

				// If no containers are registered
				return new ItemStack(Blocks.barrier).setStackDisplayName(f.getLocalizedName(new FluidStack(f, 1000)));
			} else
			{
				return ItemHelper.stateToDroppedStack(state, 1);
			}
		}
	}

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
	{
		if (outputId.equals(id) && getClass() == NEIWorldTransmuteHandler.class)
		{
			for (Entry<IBlockState, Pair<IBlockState, IBlockState>> entry : WorldTransmutations.getWorldTransmutations().entrySet())
			{
				if (entry != null)
				{
					if (entry.getValue().getLeft() != null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(), false));
					if (entry.getValue().getRight() != null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(), true));
				}
			}
		} else
		{
			super.loadCraftingRecipes(outputId, results);
		}
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (Entry<IBlockState, Pair<IBlockState, IBlockState>> entry: WorldTransmutations.getWorldTransmutations().entrySet()) {
           	IBlockState resultState = entry.getValue().getLeft();
			IBlockState altResultState = entry.getValue().getRight();
			if (resultState != null && NEIServerUtils.areStacksSameTypeCrafting(ItemHelper.stateToStack(resultState, 1), result))
			{
				arecipes.add(new CachedTransmutationRecipe(entry.getKey(), false));
           	} else if (altResultState != null && NEIServerUtils.areStacksSameTypeCrafting(ItemHelper.stateToStack(altResultState, 1), result))
			{
				arecipes.add(new CachedTransmutationRecipe(entry.getKey(), true));
           	}
        }
    }

	@Override
	public void loadUsageRecipes(ItemStack ingredient)
	{
		for (Entry<IBlockState, Pair<IBlockState, IBlockState>> entry : WorldTransmutations.getWorldTransmutations().entrySet())
		{
			if (NEIServerUtils.areStacksSameTypeCrafting(ItemHelper.stateToStack(entry.getKey(), 1), ingredient))
			{
				if (entry != null)
				{
					arecipes.add(new CachedTransmutationRecipe(entry.getKey(), true));
				}
            }
        }
    }

	@Override
	public void drawForeground(int recipe)
	{
		String sneak = StatCollector.translateToLocal("key.sneak");

		CachedTransmutationRecipe r = (CachedTransmutationRecipe) arecipes.get(recipe);

		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		if (r.sneaking) fr.drawString(sneak, 70, 40, 0);

	}

	@Override
	public void loadTransferRects()
	{
		this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(83, 23, 25, 10), id));
	}


}
*/
