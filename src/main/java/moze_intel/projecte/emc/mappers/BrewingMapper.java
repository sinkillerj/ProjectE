package moze_intel.projecte.emc.mappers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

public class BrewingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private static final String POTION_TYPE_CONVERSIONS = "field_185213_a";
    private static final String POTION_ITEM_CONVERSIONS = "field_185214_b";
	
	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, IResourceManager resourceManager) {
		int ItemCount = 0;
		int TypeCount = 0;
		/*First, let's add a conversion between bottle of water(potion) and a known EMC value (empty bottle)*/
		ItemStack bottleOfWater = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.WATER);
		ArrayList<NormalizedSimpleStack> recipeValues = new ArrayList<>();
		recipeValues.add(new NSSItem(new ItemStack(Items.GLASS_BOTTLE)));
		mapper.addConversion(1, new NSSItem(bottleOfWater), recipeValues);
		/*What follows seems really ineficient, but is the best we can do*/
		 List<Object> itemConversions = ObfuscationReflectionHelper.getPrivateValue(PotionBrewing.class, null, POTION_ITEM_CONVERSIONS);
	     List<Object> typeConversions = ObfuscationReflectionHelper.getPrivateValue(PotionBrewing.class, null, POTION_TYPE_CONVERSIONS);

	     //Collection of potion items (classes of potions) for later adding all classes to all types
	     HashSet<Item> potionItems = new HashSet<>();
	     HashSet<PotionType> potionTypes = new HashSet<>();
	     potionItems.add(bottleOfWater.getItem());
	     List<ItemStack[]> potionItemsConverters = new ArrayList<>();
	     
	     
	     final String inputField = "field_185198_a";
	     final String reagentField = "field_185199_b";
	     final String outputField = "field_185200_c";
	     
	    Class MixPredicate;
		try {
			MixPredicate = Class.forName("net.minecraft.potion.PotionBrewing$MixPredicate");
		} catch (ClassNotFoundException e) {
			PECore.LOGGER.error("Brewing mapper: could not find MixPredicate");
			return;
		}
	     
	     //First, find all potion Class recipes (Potion to Splash, Splash to Lingering)
	     for(Object obj : itemConversions){
	    	 try{
	    	 IRegistryDelegate<Item> in = (net.minecraftforge.registries.IRegistryDelegate<Item>)(ObfuscationReflectionHelper.getPrivateValue(MixPredicate, obj, inputField));
	    	 Ingredient reag = (Ingredient)ObfuscationReflectionHelper.getPrivateValue(MixPredicate, obj, reagentField);
	    	 IRegistryDelegate<Item> out = (net.minecraftforge.registries.IRegistryDelegate<Item>)ObfuscationReflectionHelper.getPrivateValue(MixPredicate, obj, outputField);
	    	 potionItems.add(in.get());
	    	 potionItems.add(out.get());
	    	 for(ItemStack reagItmStack: reag.getMatchingStacks()){
	    		 recipeValues = new ArrayList<>();
	    		 potionItemsConverters.add(new ItemStack[]{new ItemStack(in.get(), 3),reagItmStack, new ItemStack(out.get(),3)});
	    		 recipeValues.add(new NSSItem(reagItmStack));
	    		 recipeValues.add(new NSSItem(new ItemStack(in.get())));
	    		 recipeValues.add(new NSSItem(new ItemStack(in.get())));
	    		 recipeValues.add(new NSSItem(new ItemStack(in.get())));
	    		 mapper.addConversion(3, new NSSItem(new ItemStack(out.get())), recipeValues);
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
	    		 IRegistryDelegate<PotionType> in = (net.minecraftforge.registries.IRegistryDelegate<PotionType>)(ObfuscationReflectionHelper.getPrivateValue(MixPredicate, obj, inputField));
		    	 Ingredient reag = (Ingredient)ObfuscationReflectionHelper.getPrivateValue(MixPredicate, obj, reagentField);
		    	 IRegistryDelegate<PotionType> out = (net.minecraftforge.registries.IRegistryDelegate<PotionType>)ObfuscationReflectionHelper.getPrivateValue(MixPredicate, obj, outputField);
	    	 potionTypes.add(in.get());
	    	 potionTypes.add(out.get());
	    	 for(ItemStack reagItmStack: reag.getMatchingStacks()){
	    		 for(Item potClass : potionItems){
	    			 ItemStack inStack = new ItemStack(potClass);
	    			 inStack = PotionUtils.addPotionToItemStack(inStack, in.get());
	    			 ItemStack outStack = new ItemStack(potClass);
	    			 outStack = PotionUtils.addPotionToItemStack(outStack, out.get());
	    			 recipeValues = new ArrayList<>();
	    			 recipeValues.add(new NSSItem(reagItmStack));
	    			 recipeValues.add(new NSSItem(inStack));
	    			 recipeValues.add(new NSSItem(inStack));
	    			 recipeValues.add(new NSSItem(inStack));
	    			 mapper.addConversion(3, new NSSItem(outStack), recipeValues);
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
    			 recipeValues.add(new NSSItem(potClass[1]));
    			 recipeValues.add(new NSSItem(inStack));
    			 recipeValues.add(new NSSItem(inStack));
    			 recipeValues.add(new NSSItem(inStack));
    			 mapper.addConversion(3, new NSSItem(outStack), recipeValues);
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
			 recipeValues.add(new NSSItem(arrow));
			 recipeValues.add(new NSSItem(inStack));
			 mapper.addConversion(8, new NSSItem(outStack), recipeValues);
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
