package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.Utils;
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

					for (ItemStack stack : Utils.getODItems(s)) {
						if (stack == null) {
							continue;
						}

						mapper.setValue(NormalizedSimpleStack.getNormalizedSimpleStackFor(stack), 0, IMappingCollector.FixedValue.FixAndDoNotInherit);
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


	}

	protected void addMapping(String odName, int value) {
		for (ItemStack stack : Utils.getODItems(odName)) {
			addMapping(stack, value);
		}
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
