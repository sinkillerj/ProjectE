package moze_intel.projecte.handlers.NEI;

import java.util.List;
import java.util.Map.Entry;

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

public class NEIWorldTransmuteHandler extends TemplateRecipeHandler {

	private static String name = "World Transmutation";
	private static String id = "worldTransmutation";
	
	@Override
	public String getRecipeName() {
		return name;
	}

	@Override
	public String getGuiTexture() {
		return "nei:textures/gui/recipebg.png";
	}
	
	public class CachedTransmutationRecipe extends CachedRecipe{
		
		private MetaBlock input;
		private MetaBlock output;
		private MetaBlock[] inputs;
		
		public CachedTransmutationRecipe(MetaBlock in,boolean sneaking) {
			
			input = in;
			output = WorldTransmutations.getWorldTransmutation(in,sneaking);
			
		}
		
		@Override
		public PositionedStack getIngredient() {
			ItemStack is = input.toItemStack();
			return new PositionedStack(is,21,29);
			
		}
		
		
		
		@Override
		public PositionedStack getResult() {
			ItemStack result = output.toItemStack();
			return new PositionedStack(result,128,29);
			
		}
		
	}
	
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(id) && getClass() == NEIWorldTransmuteHandler.class) {
            for (Entry<MetaBlock, MetaBlock[]> entry: WorldTransmutations.MAP.entrySet()) {
            	if(entry != null && entry.getValue()!= null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(),false));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (Entry<MetaBlock, MetaBlock[]> entry: WorldTransmutations.MAP.entrySet()) {
           	if(NEIServerUtils.areStacksSameTypeCrafting(entry.getValue()[0].toItemStack(), result)){
           		if(entry != null && entry.getValue()!= null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(),false));
           	}else if (NEIServerUtils.areStacksSameTypeCrafting(entry.getValue()[1].toItemStack(), result)){
           		if(entry != null && entry.getValue()!= null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(),true));
           	}
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
    	for (Entry<MetaBlock, MetaBlock[]> entry: WorldTransmutations.MAP.entrySet()) {
    		if(NEIServerUtils.areStacksSameTypeCrafting(entry.getKey().toItemStack(), ingredient)) {
            	if(entry!=null){
            		if(entry.getValue()[0]!= null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(), false));
            		else if(entry.getValue()[1]!=null) arecipes.add(new CachedTransmutationRecipe(entry.getKey(), true));
            	}
            }
        }
    }

	
	
}
