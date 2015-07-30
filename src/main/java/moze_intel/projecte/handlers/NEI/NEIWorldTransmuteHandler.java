package moze_intel.projecte.handlers.NEI;

import java.awt.Rectangle;
import java.util.List;
import java.util.Map.Entry;

import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.recipe.TemplateRecipeHandler;
import moze_intel.projecte.utils.MetaBlock;
import moze_intel.projecte.utils.WorldTransmutations;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import org.apache.commons.lang3.tuple.Pair;

public class NEIWorldTransmuteHandler extends TemplateRecipeHandler {

	private static final String name = "World Transmutation";
	private static final String id = "worldTransmutation";
	
	@Override
	public String getRecipeName() {
		return name;
	}

	@Override
	public String getGuiTexture() {
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
		public PositionedStack getIngredient() {
			ItemStack is = ItemHelper.stateToStack(input, 1);
			return new PositionedStack(is,22,23);
			
		}
		
		
		
		@Override
		public PositionedStack getResult() {
			ItemStack result = ItemHelper.stateToStack(output, 1);
			return new PositionedStack(result,128,23);
			
		}
		
	    @Override
	    public PositionedStack getOtherStack(){
	    	return new PositionedStack(new ItemStack(ObjHandler.philosStone), 60, 23);
	    }
		
	}
	
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(id) && getClass() == NEIWorldTransmuteHandler.class) {
            for (Entry<IBlockState, Pair<IBlockState, IBlockState>> entry: WorldTransmutations.getWorldTransmutations().entrySet()) {
            	if (entry!=null){
            		if (entry.getValue().getLeft() != null)
					{
						arecipes.add(new CachedTransmutationRecipe(entry.getKey(), false));
					}
            		if (entry.getValue().getRight() !=null)
					{
						arecipes.add(new CachedTransmutationRecipe(entry.getKey(), true));
					}
            	}
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (Entry<IBlockState, Pair<IBlockState, IBlockState>> entry: WorldTransmutations.getWorldTransmutations().entrySet()) {
           	if (NEIServerUtils.areStacksSameTypeCrafting(ItemHelper.stateToStack(entry.getValue().getLeft(), 1), result))
			{
				arecipes.add(new CachedTransmutationRecipe(entry.getKey(),false));
           	} else if (NEIServerUtils.areStacksSameTypeCrafting(ItemHelper.stateToStack(entry.getValue().getRight(), 1), result))
			{
				arecipes.add(new CachedTransmutationRecipe(entry.getKey(),true));
           	}
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
    	for (Entry<IBlockState, Pair<IBlockState, IBlockState>> entry: WorldTransmutations.getWorldTransmutations().entrySet()) {
    		if(NEIServerUtils.areStacksSameTypeCrafting(ItemHelper.stateToStack(entry.getKey(), 1), ingredient)) {
				if (entry.getValue().getLeft() != null)
				{
					arecipes.add(new CachedTransmutationRecipe(entry.getKey(), false));
				}
				if (entry.getValue().getRight() !=null)
				{
					arecipes.add(new CachedTransmutationRecipe(entry.getKey(), true));
				}
            }
        }
    }
    
    @Override
	public void drawForeground( int recipe )
	{
			String sneak = StatCollector.translateToLocal("key.sneak");
			
			CachedTransmutationRecipe r = (CachedTransmutationRecipe) arecipes.get(recipe);
			
			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			if(r.sneaking)fr.drawString( sneak, 70, 40,0);

	}
    

    @Override
    public void loadTransferRects(){
    	this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(83,23,25,10), id, new Object[0]));
    }
	
}
