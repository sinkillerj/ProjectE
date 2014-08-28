package moze_intel.EMC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;
import appeng.api.util.*;

public class EMCMapper
{
	public static LinkedHashMap<IStack, Integer> emc = new LinkedHashMap();
	public static LinkedList<IStack> failed = new LinkedList();
	
	public static void map()
	{
		lazyInit();
		loadEmcFromOD();
		mapFromSmelting();
		
		boolean canMap = false;
		
		do
		{
			canMap = false;
			
			for (Entry<IStack, LinkedList<RecipeInput>> entry : RecipeMapper.getEntrySet())
			{
				IStack key = entry.getKey();
				
				if (emc.containsKey(key) || failed.contains(key))
				{
					continue;
				}
				
				int totalEmc = 0; 
				boolean toMap = true;
				
				A: for (RecipeInput rInput : entry.getValue())
				{
					toMap = true;
					
					B: for (IStack stack : rInput)
					{
						if (emc.containsKey(stack))
						{
							totalEmc += emc.get(stack);
						}
						else
						{
							toMap = false;
							break B;
						}
					}
					
					if (toMap)
					{
						totalEmc /= key.qnty;
						
						if (totalEmc <= 0)
						{
							failed.add(key);
							continue;
						}
						
						addMapping(key, totalEmc);
						canMap = true;
						
						break A;
					}
				}
			}
			
		}
		while (canMap);
		
		mapFromSmelting();
		
		failed.clear();
	}
	
	public static void mapFromSmelting()
	{
		HashMap<ItemStack, ItemStack> smelting = (HashMap<ItemStack, ItemStack>) FurnaceRecipes.smelting().getSmeltingList();
		
		for (Entry<ItemStack, ItemStack> entry : smelting.entrySet())
		{
			ItemStack key = entry.getKey();
			ItemStack value = entry.getValue();
			
			if (key == null || value == null)
			{
				continue;
			}
			
			try
			{
				IStack input = new IStack(key);
				IStack result = new IStack(value);
			
				if (emc.containsKey(input) && !emc.containsKey(result))
				{
					if (failed.contains(result))
					{
						continue;
					}
				
					int totalEmc = emc.get(input) / result.qnty;
				
					if (totalEmc <= 0)
					{
						continue;
					}
				
					addMapping(result, totalEmc);
				}
				else if (!emc.containsKey(input) && emc.containsKey(result))
				{
					if (failed.contains(input))
					{
						continue;
					}
				
					int totalEmc = emc.get(result) * result.qnty;
					
					if (totalEmc <= 0)
					{
						continue;
					}
				
					addMapping(input, totalEmc);
				}
			}
			catch (Exception e) {}
		}
	}
	
	public static void clearMap()
	{
		emc.clear();
	}
	
	public static void addMapping(ItemStack stack, int value)
	{
		addMapping(new IStack(stack), value);
	}
	
	private static void addMapping(IStack stack, int value)
	{
		if (emc.containsKey(stack))
		{
			return;
		}
		
		emc.put(stack, value);
	}
	
	private static void lazyInit()
    {
		//Vanilla
    	addMapping(new ItemStack(Blocks.cobblestone), 1);
    	addMapping(new ItemStack(Blocks.stone), 1);
    	addMapping(new ItemStack(Blocks.netherrack), 1);
    	addMapping(new ItemStack(Blocks.dirt), 1);
    	addMapping(new ItemStack(Blocks.grass), 1);
    	addMapping(new ItemStack(Blocks.mycelium), 1);
    	addMapping(new ItemStack(Blocks.leaves), 1);
    	addMapping(new ItemStack(Blocks.leaves2), 1);
    	addMapping(new ItemStack(Blocks.sand, 1, OreDictionary.WILDCARD_VALUE), 1);
    	addMapping(new ItemStack(Blocks.stained_glass, 1, OreDictionary.WILDCARD_VALUE), 1);
    	addMapping(new ItemStack(Blocks.snow), 1);
    	addMapping(new ItemStack(Blocks.ice), 1);
    	addMapping(new ItemStack(Blocks.deadbush), 1);
    	addMapping(new ItemStack(Blocks.gravel), 4);
    	addMapping(new ItemStack(Blocks.cactus), 8);
    	addMapping(new ItemStack(Blocks.vine), 8);
    	addMapping(new ItemStack(Blocks.torch), 9);
    	addMapping(new ItemStack(Blocks.web), 12);
    	addMapping(new ItemStack(Items.wheat_seeds), 16);
    	addMapping(new ItemStack(Items.melon), 16);
    	addMapping(new ItemStack(Items.clay_ball), 16);
    	addMapping(new ItemStack(Blocks.waterlily), 16);
    	addMapping(new ItemStack(Blocks.red_flower, 1, OreDictionary.WILDCARD_VALUE), 16);
    	addMapping(new ItemStack(Blocks.yellow_flower), 16);
    	addMapping(new ItemStack(Items.wheat), 24);
    	addMapping(new ItemStack(Items.nether_wart), 24);
    	addMapping(new ItemStack(Blocks.log, 1, OreDictionary.WILDCARD_VALUE), 32);
    	addMapping(new ItemStack(Blocks.log2, 1, OreDictionary.WILDCARD_VALUE), 32);
    	addMapping(new ItemStack(Blocks.planks), 8);
    	addMapping(new ItemStack(Items.stick), 4);
    	addMapping(new ItemStack(Blocks.red_mushroom), 32);
    	addMapping(new ItemStack(Blocks.brown_mushroom), 32);
    	addMapping(new ItemStack(Blocks.sapling, 1, OreDictionary.WILDCARD_VALUE), 32);
    	addMapping(new ItemStack(Items.reeds), 32);
    	addMapping(new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE), 48);
    	addMapping(new ItemStack(Blocks.soul_sand), 49);
    	addMapping(new ItemStack(Blocks.obsidian), 64);
    	
    	for (int i = 0; i < 16; i++)
    	{
    		addMapping(new ItemStack(Blocks.stained_hardened_clay, 1, OreDictionary.WILDCARD_VALUE), 64);
    	}
    	
    	addMapping(new ItemStack(Items.apple), 128);
    	addMapping(new ItemStack(Items.dye, 1, 3), 128);
    	addMapping(new ItemStack(Blocks.pumpkin), 144);
    	addMapping(new ItemStack(Items.bone), 144);
    	addMapping(new ItemStack(Blocks.mossy_cobblestone), 145);
    	addMapping(new ItemStack(Items.saddle), 192);
    	addMapping(new ItemStack(Items.water_bucket), 769);
    	addMapping(new ItemStack(Items.lava_bucket), 832);
    	addMapping(new ItemStack(Items.milk_bucket), 833);
    	addMapping(new ItemStack(Items.record_11), 2048);
    	addMapping(new ItemStack(Items.record_13), 2048);
    	addMapping(new ItemStack(Items.record_blocks), 2048);
    	addMapping(new ItemStack(Items.record_cat), 2048);
    	addMapping(new ItemStack(Items.record_chirp), 2048);
    	addMapping(new ItemStack(Items.record_far), 2048);
    	addMapping(new ItemStack(Items.record_mall), 2048);
    	addMapping(new ItemStack(Items.record_mellohi), 2048);
    	addMapping(new ItemStack(Items.record_stal), 2048);
    	addMapping(new ItemStack(Items.record_strad), 2048);
    	addMapping(new ItemStack(Items.record_wait), 2048);
    	addMapping(new ItemStack(Items.record_ward), 2048);
    	addMapping(new ItemStack(Items.string), 12);
    	addMapping(new ItemStack(Items.rotten_flesh), 32);
    	addMapping(new ItemStack(Items.slime_ball), 32);
    	addMapping(new ItemStack(Items.egg), 32);
    	addMapping(new ItemStack(Items.feather), 48);
    	addMapping(new ItemStack(Items.leather), 64);
    	addMapping(new ItemStack(Items.spider_eye), 128);
    	addMapping(new ItemStack(Items.gunpowder), 192);
    	addMapping(new ItemStack(Items.ender_pearl), 1024);
    	addMapping(new ItemStack(Items.blaze_rod), 1536);
    	addMapping(new ItemStack(Items.ghast_tear), 4096);
    	addMapping(new ItemStack(Blocks.dragon_egg), 139264);
    	addMapping(new ItemStack(Items.porkchop), 64);
    	addMapping(new ItemStack(Items.beef), 64);
    	addMapping(new ItemStack(Items.chicken), 64);
    	addMapping(new ItemStack(Items.fish), 64);
    	addMapping(new ItemStack(Items.carrot), 64);
    	addMapping(new ItemStack(Items.potato), 64);
    	addMapping(new ItemStack(Items.iron_ingot), 256);
    	addMapping(new ItemStack(Items.gold_ingot), 2048);
    	addMapping(new ItemStack(Items.diamond), 8192);
    	addMapping(new ItemStack(Items.flint), 4);
    	addMapping(new ItemStack(Items.coal), 128);
    	addMapping(new ItemStack(Items.redstone), 64);
    	addMapping(new ItemStack(Items.glowstone_dust), 384);
    	addMapping(new ItemStack(Items.quartz), 256);
    	addMapping(new ItemStack(Items.dye, 1, 4), 864);

    	for (int i = 0; i < 15; i++)
    	{
    		if (i == 3 || i == 4) 
    		{
    			continue;
    		}
    		addMapping(new ItemStack(Items.dye, 1, i), 16);
    	}
    	
    	addMapping(new ItemStack(Items.enchanted_book), 2048);
    	addMapping(new ItemStack(Items.emerald), 16384);
    	addMapping(new ItemStack(Items.nether_star), 30720);
    	addMapping(new ItemStack(Items.iron_horse_armor), 1280);      // < 
    	addMapping(new ItemStack(Items.golden_horse_armor), 1024);    // < This is going off the assumption that it takes 5 to make one armor
    	addMapping(new ItemStack(Items.diamond_horse_armor), 40960);  // <
    	addMapping(new ItemStack(Blocks.tallgrass), 1);
    	addMapping(new ItemStack(Blocks.beacon), 104172);
    	addMapping(new ItemStack(Blocks.packed_ice), 4);
    	addMapping(new ItemStack(Items.snowball), 1);
    	addMapping(new ItemStack(Items.filled_map), 1472);
    	addMapping(new ItemStack(Items.chainmail_boots), 512);
    	addMapping(new ItemStack(Items.chainmail_leggings), 896);
    	addMapping(new ItemStack(Items.chainmail_chestplate), 1024);
    	addMapping(new ItemStack(Items.chainmail_helmet), 640);
    }
	
	private static void loadEmcFromOD()
	{
		LinkedHashMap<String, Integer> map = new LinkedHashMap();
		
		//trees
		map.put("logWood", 32);
		map.put("plankWood", 8);
		map.put("treeSapling", 32);
		map.put("stickWood", 4);
		
		//building stuff
		map.put("stone", 1);
		map.put("cobblestone", 1);
		map.put("blockMarble", 1);
		map.put("whiteStone", 64);
		
		//ingots
		map.put("ingotIron", 256);
		map.put("ingotGold", 2048);
		
		//gems and dusts
		map.put("gemDiamond", 8192);
		map.put("dustRedstone", 64);
		map.put("dustGlowstone", 384);
		map.put("dustCoal", 64);
		map.put("dustCharcoal", 16);
		map.put("dustSulfur", 32);
		
		//Ingots
		map.put("ingotCopper", 128);
		map.put("ingotTin", 256);
		map.put("ingotBronze", 160);
		map.put("ingotSilver", 512);
		map.put("ingotLead", 512);
		map.put("ingotNickel", 1024);
		map.put("ingotInvar", 512);
		map.put("ingotElectrum", 1280);
		map.put("ingotSignalum", 160);
		map.put("ingotEnderium", 6144);
		map.put("ingotPlatinum", 2084);
		
		//AE2
		map.put("crystalCertusQuartz", 64);
		map.put("crystalFluix", 256);
		map.put("dustCertusQuartz", 32);
		map.put("dustFluix", 128);
		
		//BOP-ProjectRed
		map.put("gemRuby", 2048);
		map.put("gemSapphire", 2048);
		map.put("gemPeridot", 2048);
		
		//TE
		map.put("blockGlassHardened", 192);
		
		//MISC
		map.put("enderChest", 3184); // This is for ender Chests mod which adds over 74 pages of ender chests in nei
		map.put("itemSkull", 10240); // Recipe for the Skulls & Heads
		map.put("treeLeaves", 1);
		map.put("listAllfishraw", 64);
		
		
		//Black-list all ores/dusts
		for (String s : OreDictionary.getOreNames())
		{
			if (s.startsWith("ore") || s.startsWith("dust") || s.startsWith("crushed"))
			{
				for (ItemStack stack : getODItems(s))
				{
					if (stack == null)
					{
						continue;
					}
					
					failed.add(new IStack(stack));
				}
			}
		}
		
		for (Entry<String, Integer> entry : map.entrySet())
		{
			for (ItemStack stack : getODItems(entry.getKey()))
			{
				if (stack == null)
				{
					continue;
				}
				
				addMapping(stack, entry.getValue());
			}
		}
	}
	
	private static List<ItemStack> getODItems(String oreName)
	{
		List<ItemStack> list = new ArrayList();
		list.addAll(OreDictionary.getOres(oreName));
		return list;
	}

}
