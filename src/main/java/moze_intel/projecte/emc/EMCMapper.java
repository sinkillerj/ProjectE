package moze_intel.projecte.emc;

import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public final class EMCMapper 
{
	public static LinkedHashMap<SimpleStack, Integer> emc = new LinkedHashMap<SimpleStack, Integer>();
	public static LinkedHashMap<SimpleStack, Integer> IMCregistrations = new LinkedHashMap<SimpleStack, Integer>();
	public static LinkedList<SimpleStack> blackList = new LinkedList<SimpleStack>();

	public static void map()
	{
		loadEmcFromIMC();
		lazyInit();
		loadEmcFromOD();
		mapFromSmelting();

		mapFromRecipes(2);
		lateEmcMapping();
		FluidMapper.map();
		mapFromRecipes(1);
		
		assertMinEmcValues();

		Transmutation.loadCompleteKnowledge();
		FuelMapper.loadMap();
	}

	private static void mapFromRecipes(int numRuns)
	{
		for (int i = 0; i < numRuns; i++)
		{
			boolean canMap = false;

			do
			{
				canMap = false;

				for (Entry<SimpleStack, LinkedList<RecipeInput>> entry : RecipeMapper.getEntrySet())
				{
					SimpleStack key = entry.getKey();

					if (mapContains(key) || blacklistContains(key))
					{
						continue;
					}

					int totalEmc = 0;
					boolean toMap = true;

					A: for (RecipeInput rInput : entry.getValue())
					{
						toMap = true;

						B: for (Object obj : rInput)
						{
							SimpleStack input = null;

							if (obj instanceof SimpleStack)
							{
								if (mapContains((SimpleStack) obj))
								{
									input = (SimpleStack) obj;
								}
							}
							else
							{
								for (SimpleStack s : (ArrayList<SimpleStack>) obj)
								{
									if (mapContains(s))
									{
										input = s;
										break;
									}
								}
							}

							if (input == null)
							{
								toMap = false;
								break B;
							}

							ItemStack stack = input.toItemStack();

							if (stack == null)
							{
								toMap = false;
								break B;
							}

							if (stack.getItem().hasContainerItem(stack))
							{
								SimpleStack container = new SimpleStack(stack.getItem().getContainerItem(stack));

								if (mapContains(container))
								{
									totalEmc -= getEmcValue(container);
								}
							}

							totalEmc += getEmcValue(input);
						}

						if (toMap)
						{
							totalEmc = totalEmc / key.qnty;

							if (totalEmc > 0)
							{
								addMapping(key, totalEmc);
								canMap = true;
								break A;
							}
							else
							{
								toMap = false;
							}
						}
					}
				}

			}
			while (canMap);

			mapFromSmelting();
		}
	}

	private static void assertMinEmcValues()
	{
		for (Entry<SimpleStack, LinkedList<RecipeInput>> entry : RecipeMapper.getEntrySet())
		{
			if (!mapContains(entry.getKey()) || entry.getKey().toString().startsWith("minecraft:") || entry.getValue().size() <= 1)
			{
				continue;
			}

			int currentEmc = getEmcValue(entry.getKey());
			int minEmc = currentEmc;

			for (RecipeInput input : entry.getValue())
			{
				int recipeEmc = 0;

				for (Object obj : input)
				{
					SimpleStack stack = null;

					if (obj instanceof SimpleStack)
					{
						if (mapContains((SimpleStack) obj))
						{
							stack = (SimpleStack) obj;
						}
					}
					else
					{
						int itemEmc = -1;

						for (SimpleStack s : (ArrayList<SimpleStack>) obj)
						{
							if (mapContains(s))
							{
								if (itemEmc == -1 || getEmcValue(s) < itemEmc)
								{
									stack = s;
									itemEmc = getEmcValue(s);
								}
							}
						}
					}

					if (stack == null)
					{
						recipeEmc = 0;
						break;
					}
					else
					{
						recipeEmc += EMCMapper.getEmcValue(stack);
					}
				}

				if (recipeEmc > 0)
				{
					recipeEmc = recipeEmc / entry.getKey().qnty;

					if (recipeEmc < minEmc)
					{
						minEmc = recipeEmc;
					}
				}
			}

			if (minEmc < currentEmc)
			{
				addMappingWithOverwrite(entry.getKey(), minEmc);
			}
		}
	}

	private static void mapFromSmelting()
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

				if (!input.isValid() || !result.isValid())
				{
					continue;
				}
			
				if (mapContains(input) && !mapContains(result))
				{
					if (blacklistContains(result))
					{
						continue;
					}
				
					int totalEmc = getEmcValue(input) / result.qnty;

					addMapping(result, totalEmc);
				}
				else if (!mapContains(input) && mapContains(result))
				{
					if (blacklistContains(input))
					{
						continue;
					}
				
					int totalEmc = getEmcValue(result) * result.qnty;
					addMapping(input, totalEmc);
				}
			}
			catch (Exception e) {}
		}
	}

	public static void addToBlackList(ItemStack stack)
	{
		SimpleStack s = new SimpleStack(stack);

		if (s.isValid())
		{
			addToBlacklist(s);
		}
	}

	public static void addToBlackList(String odName)
	{
		for (ItemStack stack : Utils.getODItems(odName))
		{
			addToBlackList(stack);
		}
	}

	private static boolean blacklistContains(SimpleStack stack)
	{
		SimpleStack copy = stack.copy();
		copy.qnty = 1;

		return blackList.contains(copy);
	}

	private static void addToBlacklist(SimpleStack stack)
	{
		SimpleStack copy = stack.copy();
		copy.qnty = 1;

		if (!blackList.contains(copy))
		{
			blackList.add(copy);
		}
	}

	public static boolean mapContains(SimpleStack key)
	{
		SimpleStack copy = key.copy();
		copy.qnty = 1;

		return emc.containsKey(copy);
	}

	public static int getEmcValue(SimpleStack stack)
	{
		SimpleStack copy = stack.copy();
		copy.qnty = 1;

		return emc.get(copy);
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

	public static void addMapping(String unlocalName, int meta, int value)
	{
		ItemStack stack = Utils.getStackFromString(unlocalName, meta);

		if (stack != null)
		{
			addMapping(stack, value);
		}
	}

	public static void addMapping(String odName, int value)
	{
		for (ItemStack stack : Utils.getODItems(odName))
		{
			addMapping(stack, value);
		}
	}

	public static void addMapping(String modid, String name, int meta, int emc)
	{

		Item item = (Item) Item.itemRegistry.getObject(modid + ":" + name);

		if (item != null)
		{
			addMapping(new SimpleStack(new ItemStack(item, 1, meta)), emc);
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
		SimpleStack copy = stack.copy();
		copy.qnty = 1;

		if (emc.containsKey(copy) || blackList.contains(copy))
		{
			return;
		}
		
		if (value > 0)
		{
			emc.put(copy, value);
		}
	}

	private static void addMappingWithOverwrite(SimpleStack stack, int value)
	{
		SimpleStack copy = stack.copy();
		copy.qnty = 1;

		if (value > 0)
		{
			emc.put(copy, value);
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
		addMapping(new ItemStack(Blocks.dragon_egg), 262144);
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
		
		addMapping(new ItemStack(Items.nether_star), 139264);
		addMapping(new ItemStack(Items.iron_horse_armor), 1280);
		addMapping(new ItemStack(Items.golden_horse_armor), 1024);
		addMapping(new ItemStack(Items.diamond_horse_armor), 40960);
		addMapping(new ItemStack(Blocks.tallgrass), 1);
		addMapping(new ItemStack(Blocks.packed_ice), 4);
		addMapping(new ItemStack(Items.snowball), 1);
		addMapping(new ItemStack(Items.filled_map), 1472);
		addMapping(new ItemStack(Items.blaze_powder), 768);
		addMapping(new ItemStack(Items.dye, 1, 15), 48);

		addMapping("appliedenergistics2:item.ItemMultiMaterial", 1, 256);
	}
	
	private static void loadEmcFromIMC()
	{
		for (Entry<SimpleStack, Integer> entry : IMCregistrations.entrySet())
		{
			if (entry.getValue() <= 0)
			{
				addToBlacklist(entry.getKey());
			}
			else
			{
				addMapping(entry.getKey(), entry.getValue());
			}
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
		addMapping("ingotUranium", 4096);
		
		//Thaumcraft
		addMapping("shardAir", 64);
		addMapping("shardFire", 64);
		addMapping("shardWater", 64);
		addMapping("shardEarth", 64);
		addMapping("shardOrder", 64);
		addMapping("shardEntropy", 64);

		//Forbidden Magic
		addMapping("shardNether", 64);
		
		//Vanilla 
		addMapping("treeLeaves", 1);
		
		
		//Black-list all ores/dusts
		for (String s : OreDictionary.getOreNames())
		{
			if (s.startsWith("ore") || s.startsWith("dust") || s.startsWith("crushed"))
			{
				//Some exceptions in the black-listing
				if (s.equals("dustPlastic"))
				{
					continue;
				}

				for (ItemStack stack : Utils.getODItems(s))
				{
					if (stack == null)
					{
						continue;
					}
					
					addToBlacklist(new SimpleStack(stack));
				}
			}
		}
	}

	private static void addRelativeEmcValue(String toSearch, int meta , Object...args)
	{
		ItemStack stack = Utils.getStackFromString(toSearch, meta);

		if (stack == null)
		{
			if (OreDictionary.getOres(toSearch).isEmpty())
			{
				return;
			}
		}

		int totalEmc = 0;

		for (int i = 0; i < args.length - 2; i += 3)
		{
			try
			{
				String currentName = (String) args[i];
				int currentMeta = (Integer) args[i + 1];
				int multiplier = (Integer) args[i + 2];

				ItemStack current = Utils.getStackFromString(currentName, currentMeta);

				if (current == null || !Utils.doesItemHaveEmc(current))
				{
					return;
				}

				totalEmc += (Utils.getEmcValue(current) * multiplier);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
		}

		if (stack != null)
		{
			addMapping(stack, totalEmc);
		}
		else
		{
			addMapping(toSearch, totalEmc);
		}
	}

	private static void lateEmcMapping()
	{
		addRelativeEmcValue("ThermalExpansion:Frame", 6, "ThermalExpansion:Frame", 5, 1, "minecraft:redstone", 0, 40);
		addRelativeEmcValue("ThermalExpansion:Frame", 8, "ThermalExpansion:Frame", 7, 1, "minecraft:ender_pearl", 0, 4);
		addRelativeEmcValue("ingotSteel", 0, "minecraft:iron_ingot", 0, 1, "minecraft:coal", 1, 4);
	}
}
