package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.SimpleGraphMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.emc.json.NSSFake;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NSSItemWithNBT;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.arithmetics.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.collector.MappingCollector;
import moze_intel.projecte.emc.collector.WildcardSetValueFixCollector;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessKleinStar;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.datafix.fixes.PotionItems;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BrewingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private static final String[] POTION_TYPE_CONVERSIONS = new String[]{"field_185213_a", "POTION_TYPE_CONVERSIONS"};
    private static final String[] POTION_ITEM_CONVERSIONS = new String[]{"field_185214_b", "POTION_ITEM_CONVERSIONS"};
	
	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final Configuration config) {
		int ItemCount = 0;
		int TypeCount = 0;
		/*First, let's add a conversion between bottle of water(potion) and a known EMC value (empty bottle)*/
		ItemStack bottleOfWater = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
		ArrayList<NormalizedSimpleStack> recipeValues = new ArrayList<>();
		recipeValues.add(NSSItem.create(new ItemStack(Items.GLASS_BOTTLE)));
		mapper.addConversion(1, NSSItemWithNBT.create(bottleOfWater), recipeValues);
		
		/*What follows seems really ineficient, but is the best we can do*/
		 List<Object> itemConversions = ReflectionHelper.getPrivateValue(PotionHelper.class, null, POTION_ITEM_CONVERSIONS);
	     List<Object> typeConversions = ReflectionHelper.getPrivateValue(PotionHelper.class, null, POTION_TYPE_CONVERSIONS);

	     //Collection of potion items (classes of potions) for later adding all classes to all types
	     HashSet<Item> potionItems = new HashSet<>();
	     HashSet<PotionType> potionTypes = new HashSet<>();
	     potionItems.add(bottleOfWater.getItem());
	     List<ItemStack[]> potionItemsConverters = new ArrayList<>();
	     
	     //First, find all potion Class recipes (Potion to Splash, Splash to Lingering)
	     for(Object obj : itemConversions){
	    	 try{
	    	 Field input = ReflectionHelper.findField(obj.getClass(), "input", "field_185198_a");
	    	 Field reagent =ReflectionHelper.findField(obj.getClass(), "reagent", "field_185199_b");
	    	 Field output =ReflectionHelper.findField(obj.getClass(), "output", "field_185198_c");
	    	 
	    	 net.minecraftforge.registries.IRegistryDelegate<Item> in = (net.minecraftforge.registries.IRegistryDelegate<Item>)input.get(obj);
	    	 Ingredient reag = (Ingredient)reagent.get(obj);
	    	 net.minecraftforge.registries.IRegistryDelegate<Item> out = (net.minecraftforge.registries.IRegistryDelegate<Item>)output.get(obj);
	    	 potionItems.add(in.get());
	    	 potionItems.add(out.get());
	    	 for(ItemStack reagItmStack: reag.getMatchingStacks()){
	    		 recipeValues = new ArrayList<>();
	    		 potionItemsConverters.add(new ItemStack[]{new ItemStack(in.get(), 3),reagItmStack, new ItemStack(out.get(),3)});
	    		 recipeValues.add(NSSItemWithNBT.create(reagItmStack));
	    		 recipeValues.add(NSSItemWithNBT.create(new ItemStack(in.get())));
	    		 recipeValues.add(NSSItemWithNBT.create(new ItemStack(in.get())));
	    		 recipeValues.add(NSSItemWithNBT.create(new ItemStack(in.get())));
	    		 mapper.addConversion(3, NSSItemWithNBT.create(new ItemStack(out.get())), recipeValues);
	    		 ItemCount++;
	    	 }
	    	 }catch (Exception ex){
	    		 PECore.LOGGER.error("Brewing mapper: could not find fields "+ ex.getMessage());
	    		 return;
	    	 }
	     }
	     //next, find all brewing recipes that create actual potions of the same class
	     for(Object obj : typeConversions){
	    	 try{
	    	 Field input = ReflectionHelper.findField(obj.getClass(), "input", "field_185198_a");
	    	 Field reagent =ReflectionHelper.findField(obj.getClass(), "reagent", "field_185199_b");
	    	 Field output =ReflectionHelper.findField(obj.getClass(), "output", "field_185200_c");
	    	 
	    	 net.minecraftforge.registries.IRegistryDelegate<PotionType> in = (net.minecraftforge.registries.IRegistryDelegate<PotionType>)input.get(obj);
	    	 Ingredient reag = (Ingredient)reagent.get(obj);
	    	 net.minecraftforge.registries.IRegistryDelegate<PotionType> out = (net.minecraftforge.registries.IRegistryDelegate<PotionType>)output.get(obj);
	    	 potionTypes.add(in.get());
	    	 potionTypes.add(out.get());
	    	 for(ItemStack reagItmStack: reag.getMatchingStacks()){
	    		 for(Item potClass : potionItems){
	    			 ItemStack inStack = new ItemStack(potClass);
	    			 inStack = PotionUtils.addPotionToItemStack(inStack, in.get());
	    			 ItemStack outStack = new ItemStack(potClass);
	    			 outStack = PotionUtils.addPotionToItemStack(outStack, out.get());
	    			 recipeValues = new ArrayList<>();
	    			 recipeValues.add(NSSItemWithNBT.create(reagItmStack));
	    			 recipeValues.add(NSSItemWithNBT.create(inStack));
	    			 recipeValues.add(NSSItemWithNBT.create(inStack));
	    			 recipeValues.add(NSSItemWithNBT.create(inStack));
	    			 mapper.addConversion(3, NSSItemWithNBT.create(outStack), recipeValues);
	    		 }
	    		 TypeCount++;
	    	 }
	    	 }catch (Exception ex){
	    		 PECore.LOGGER.error("Brewing mapper: could not find fields "+ ex.getMessage());
	    		 return;
	    	 }
	     }
	     
	     //finally, add mappings between all potion Types changing classes (ie: Potion of harming to Splash potion of harming) 
	     for(ItemStack[] potClass: potionItemsConverters){
	    	 for(PotionType potType: potionTypes){
	    		 ItemStack inStack = potClass[0].copy();
    			 inStack = PotionUtils.addPotionToItemStack(inStack, potType);
    			 ItemStack outStack = potClass[2].copy();
    			 outStack = PotionUtils.addPotionToItemStack(outStack, potType);
    			 recipeValues = new ArrayList<>();
    			 recipeValues.add(NSSItemWithNBT.create(potClass[1]));
    			 recipeValues.add(NSSItemWithNBT.create(inStack));
    			 recipeValues.add(NSSItemWithNBT.create(inStack));
    			 recipeValues.add(NSSItemWithNBT.create(inStack));
    			 mapper.addConversion(3, NSSItemWithNBT.create(outStack), recipeValues);
	    	 }
	     }
	     //PS: Assume tipped arrows can be crafted with any potionEffect
	     ItemStack arrow = new ItemStack(Items.ARROW, 8);
	     for(PotionType potType: potionTypes){
    		 ItemStack inStack = new ItemStack(Items.LINGERING_POTION);
			 inStack = PotionUtils.addPotionToItemStack(inStack, potType);
			 ItemStack outStack = new ItemStack(Items.TIPPED_ARROW);
			 outStack = PotionUtils.addPotionToItemStack(outStack, potType);
			 recipeValues = new ArrayList<>();
			 recipeValues.add(NSSItemWithNBT.create(arrow));
			 recipeValues.add(NSSItemWithNBT.create(inStack));
			 mapper.addConversion(8, NSSItemWithNBT.create(outStack), recipeValues);
    	 }
	     
		PECore.debugLog("BrewingMapper Statistics:");	
		PECore.debugLog("Found {} Recipes of changing Item Type", ItemCount);
		PECore.debugLog("Found {} Recipes of changing Potion Type", TypeCount);
	}

	@Override
	public String getName() {
		return "BrewingMapper";
	}

	@Override
	public String getDescription() {
		return "Add Conversions for Brewing Recipes gathered from PotionHelper's fields";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}
