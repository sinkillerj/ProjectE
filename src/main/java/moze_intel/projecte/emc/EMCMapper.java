package moze_intel.projecte.emc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import moze_intel.projecte.config.FileHelper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientSyncPKT;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

public final class EMCMapper 
{
	public static LinkedHashMap<SimpleStack, Integer> emc = new LinkedHashMap();
	public static LinkedHashMap<SimpleStack, Integer> IMCregistrations = new LinkedHashMap();
	public static LinkedList<SimpleStack> blackList = new LinkedList();
	public static LinkedList<SimpleStack> failed = new LinkedList();
	
	public static void map()
	{
		loadEmcFromIMC();
		lazyInit();
		loadEmcFromOD();
		mapFromSmelting();
		
		for (int i = 0; i < 2; i++)
		{
			boolean canMap = false;
			
			do
			{
				canMap = false;
				
				for (Entry<SimpleStack, LinkedList<RecipeInput>> entry : RecipeMapper.getEntrySet())
				{
					SimpleStack key = entry.getKey();
					
					if (emc.containsKey(key) || failed.contains(key) || blackList.contains(key))
					{
						continue;
					}
					
					int totalEmc = 0; 
					boolean toMap = true;
					
					A: for (RecipeInput rInput : entry.getValue())
					{
						toMap = true;
						
						B: for (SimpleStack stack : rInput)
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
							totalEmc =  (int) Math.ceil(totalEmc / (double) key.qnty);
							
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
		}
		
		//Makes sure items from other mods have the lowest EMC possible
		for (Entry<SimpleStack, LinkedList<RecipeInput>> entry : RecipeMapper.getEntrySet())
		{
			if (!emc.containsKey(entry.getKey()) || entry.getKey().toString().startsWith("minecraft:") || entry.getValue().size() <= 1)
			{
				continue;
			}
			
			int currentEmc = emc.get(entry.getKey());
			int minEmc = currentEmc;
			
			for (RecipeInput input : entry.getValue())
			{
				int emc = 0;
				
				for (SimpleStack s : input.getInputs())
				{
					if (!EMCMapper.emc.containsKey(s))
					{
						emc = 0;
						break;
					}
					
					else 
					{
						emc += EMCMapper.emc.get(s);
					}
				}
				
				if (emc > 0 && emc < minEmc)
				{
					minEmc = (int) Math.ceil(emc / (double) entry.getKey().qnty);
				}
			}
			
			if (minEmc < currentEmc)
			{
				emc.put(entry.getKey(), minEmc);
			}
		}
		
		failed.clear();
		
		Transmutation.loadCompleteKnowledge();
		FuelMapper.loadMap();
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
				SimpleStack input = new SimpleStack(key);
				SimpleStack result = new SimpleStack(value);
			
				if (emc.containsKey(input) && !emc.containsKey(result))
				{
					if (failed.contains(result) || blackList.contains(result))
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
					if (failed.contains(input) || blackList.contains(input))
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
	
	public static boolean addCustomEntry(ItemStack stack, int value)
	{
		if (FileHelper.addToFile(stack, value))
		{
			clearMaps();
			FileHelper.readUserData();
			map();
			PacketHandler.sendToAll(new ClientSyncPKT());
			return true;
		}
		
		return false;
	}
	
	public static boolean addCustomEntry(String odName, int value)
	{
		if (FileHelper.addToFile(odName, value))
		{
			clearMaps();
			FileHelper.readUserData();
			map();
			PacketHandler.sendToAll(new ClientSyncPKT());
			return true;
		}
		
		return false;
	}
	
	public static void clearMaps()
	{
		emc.clear();
		blackList.clear();
		
	}
	
	public static void addMapping(ItemStack stack, int value)
	{
		addMapping(new SimpleStack(stack), value);
	}
	
	public static void addMapping(String odName, int value)
	{
		for (ItemStack stack : Utils.getODItems(odName))
		{
			addMapping(stack, value);
		}
	}
	
	public static boolean addIMCRegistration(ItemStack stack, int value)
	{
		SimpleStack simpleStack = new SimpleStack(stack);
		
		if (!IMCregistrations.containsKey(simpleStack))
		{
			IMCregistrations.put(simpleStack, value);
			return true;
		}
		
		return false;
	}
	
	private static void addMapping(SimpleStack stack, int value)
	{
		if (emc.containsKey(stack))
		{
			return;
		}
		
		if (value <= 0)
		{
			blackList.add(stack);
		}
		else
		{
			emc.put(stack, value);
		}
	}
	
	private static void lazyInit()
    {
    	addMapping(new ItemStack(Blocks.cobblestone), 1);
    	addMapping(new ItemStack(Blocks.stone), 1);
    	addMapping(new ItemStack(Blocks.netherrack), 1);
    	addMapping(new ItemStack(Blocks.dirt), 1);
    	addMapping(new ItemStack(Blocks.grass), 1);
    	addMapping(new ItemStack(Blocks.mycelium), 1);
    	addMapping(new ItemStack(Blocks.leaves), 1);
    	addMapping(new ItemStack(Blocks.leaves2), 1);
    	addMapping(new ItemStack(Blocks.sand, 1, 0), 1);
    	addMapping(new ItemStack(Blocks.sand, 1, 1), 1);
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
    	
    	for (int i = 0; i <= 8; i++)
    	{
    		addMapping(new ItemStack(Blocks.red_flower, 1, i), 16);
    	}
    	
    	addMapping(new ItemStack(Blocks.yellow_flower), 16);
    	addMapping(new ItemStack(Items.wheat), 24);
    	addMapping(new ItemStack(Items.nether_wart), 24);
    	addMapping(new ItemStack(Items.stick), 4);
    	addMapping(new ItemStack(Blocks.red_mushroom), 32);
    	addMapping(new ItemStack(Blocks.brown_mushroom), 32);
    	addMapping(new ItemStack(Items.reeds), 32);
    	addMapping(new ItemStack(Blocks.soul_sand), 49);
    	addMapping(new ItemStack(Blocks.obsidian), 64);
    	
    	for (int i = 0; i < 16; i++)
    	{
    		addMapping(new ItemStack(Blocks.stained_hardened_clay, 1, i), 64);
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
    	
    	addMapping(new ItemStack(Items.nether_star), 262144);
    	addMapping(new ItemStack(Items.iron_horse_armor), 1280);       
    	addMapping(new ItemStack(Items.golden_horse_armor), 1024);    
    	addMapping(new ItemStack(Items.diamond_horse_armor), 40960);  
    	addMapping(new ItemStack(Blocks.tallgrass), 1);
    	addMapping(new ItemStack(Blocks.packed_ice), 4);
    	addMapping(new ItemStack(Items.snowball), 1);
    	addMapping(new ItemStack(Items.filled_map), 1472);
    	addMapping(new ItemStack(Items.blaze_powder), 768);
    	addMapping(new ItemStack(Items.dye, 1, 15), 48);
    }
	
	private static void loadEmcFromIMC()
	{
		for (Entry<SimpleStack, Integer> entry : IMCregistrations.entrySet())
		{
			addMapping(entry.getKey(), entry.getValue());
		}
	}
	
	private static void loadEmcFromOD()
	{
		//trees
		addMapping("logWood", 32);
		addMapping("plankWood", 8);
		addMapping("treeSapling", 32);
		addMapping("stickWood", 4);
		addMapping("blockGlass", 1);
		addMapping("blockCloth", 48);
		
		//building stuff
		addMapping("stone", 1);
		addMapping("cobblestone", 1);
		
		//gems and dusts
		addMapping("gemDiamond", 8192);
		addMapping("dustRedstone", 64);
		addMapping("dustGlowstone", 384);
		addMapping("dustCoal", 64);
		addMapping("dustCharcoal", 16);
		addMapping("dustSulfur", 32);
		
		//Ingots (blocks will get auto-mapped)
		/*Vanilla*/
		addMapping("ingotIron", 256);
		addMapping("ingotGold", 2048);
		/*General*/
		addMapping("ingotCopper", 128);
		addMapping("ingotTin", 256);
		addMapping("ingotBronze", 160);
		addMapping("ingotSilver", 512);
		addMapping("ingotLead", 512);
		addMapping("ingotNickel", 1024);
		/*TE*/
		addMapping("ingotSignalum", 256);
		addMapping("ingotLumium", 512);
		addMapping("ingotInvar", 512);
		addMapping("ingotElectrum", 1280);
		addMapping("ingotEnderium", 4096);
		addMapping("ingotPlatinum", 4096);
		/*TiCon*/
		addMapping("ingotAluminum", 128);
		addMapping("ingotAluminumBrass", 512);
		addMapping("ingotArdite", 1024);
		addMapping("ingotCobalt", 1024);
		addMapping("ingotManyullyn", 2048);
		addMapping("ingotAlumite", 1024);
		/*TC*/
		addMapping("ingotThaumium", 2048);
		/*Ender IO*/
		addMapping("itemSilicon", 32);
		addMapping("ingotPhasedIron", 1280);
		addMapping("ingotPhasedGold", 3520);
		addMapping("ingotRedstoneAlloy", 96);
		addMapping("ingotConductiveIron", 320);
		addMapping("ingotEnergeticAlloy", 2496);
		addMapping("ingotElectricalSteel", 352);
		addMapping("ingotDarkSteel", 384);
		addMapping("ingotSoularium", 2097);
		/*Mekanism*/
		addMapping("ingotOsmium", 2496);
			
		//AE2
		addMapping("crystalCertusQuartz", 64);
		addMapping("crystalFluix", 256);
		addMapping("dustCertusQuartz", 32);
		addMapping("dustFluix", 128);
		
		//BOP-ProjectRed
		addMapping("gemRuby", 2048);
		addMapping("gemSapphire", 2048);
		addMapping("gemPeridot", 2048);
		addMapping("blockMarble", 4);
		
		//TE
		addMapping("blockGlassHardened", 192);
		
		//IC2
		addMapping("itemRubber", 32);
		
		//Thaumcraft
		addMapping("shardAir", 64);
		addMapping("shardFire", 64);
		addMapping("shardWater", 64);
		addMapping("shardEarth", 64);
		addMapping("shardOrder", 64);
		addMapping("shardEntropy", 64);
		
		//Vanilla 
		addMapping("treeLeaves", 1);
		
		
		//Black-list all ores/dusts
		for (String s : OreDictionary.getOreNames())
		{
			if (s.startsWith("ore") || s.startsWith("dust") || s.startsWith("crushed"))
			{
				for (ItemStack stack : Utils.getODItems(s))
				{
					if (stack == null)
					{
						continue;
					}
					
					failed.add(new SimpleStack(stack));
				}
			}
		}
	}
}
