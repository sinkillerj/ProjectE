package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryMapper extends LazyMapper {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		this.mapper = mapper;
		if (config.getBoolean("blacklistOresAndDusts", "", true, "Set EMC=0 for everything that has an OD Name that starts with `ore`, `dust` or `crushed` besides `dustPlastic`")) {
			//Black-list all ores/dusts
			for (String s : OreDictionary.getOreNames()) {
				if (s.startsWith("ore") || s.startsWith("dust") || s.startsWith("crushed")) {
					//Some exceptions in the black-listing
					if (s.equals("dustPlastic")) {
						continue;
					}

					for (ItemStack stack : ItemHelper.getODItems(s)) {
						if (stack == null) {
							continue;
						}

						mapper.setValueBefore(NormalizedSimpleStack.getFor(stack), 0);
						mapper.setValueAfter(NormalizedSimpleStack.getFor(stack), 0);
					}
				}
			}
		}
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
		/*TC*/
		addMapping("ingotThaumium", 2048);
		/*Ender IO*/
		addMapping("itemSilicon", 32);
		/*Mekanism*/
		addMapping("ingotOsmium", 512);

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

		//IC2 BigReactors
		addMapping("itemRubber", 32);
		addMapping("ingotUranium", 4096);
		addMapping("ingotCyanite", 1024);

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


	}

	protected void addMapping(String odName, int value) {
		for (ItemStack stack : ItemHelper.getODItems(odName)) {
			addMapping(stack, value);
		}
		this.mapper.setValueBefore(NormalizedSimpleStack.forOreDictionary(odName), value);
	}


	@Override
	public String getName() {
		return "OreDictionaryMapper";
	}

	@Override
	public String getDescription() {
		return "Default values for a lot of Mod - OreDictionary Names.";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}
