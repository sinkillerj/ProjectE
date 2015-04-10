package moze_intel.projecte.emc.mappers;

import ic2.api.item.IC2Items;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;
import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.PELogger;

public class ModMapper extends LazyMapper{

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper,Configuration config) {
		PELogger.logInfo("Adding Modded Mappings");
		
		this.mapper = mapper;
		
		if(Loader.isModLoaded("appliedenergistics2")){
			ae2Mappings();
		} else{
			notDetected("Applied Energistics 2");
		}
		if(Loader.isModLoaded("IC2")){
			ic2Mappings();
		} else{
			notDetected("Industrial Craft 2");
		}
	}
	
	@Override
	public String getName() {
		return "Mod Mapper";
	}

	@Override
	public String getDescription() {
		return "Default values for manually mapped mod Items";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
	
	/**
	 * Applied Energistics 2 Mappings
	 */
	private void ae2Mappings(){
		PELogger.logInfo("Loading AE2 Values");
		addMapping("appliedenergistics2:item.ItemMultiMaterial", 1, 256); //all up to 52
	}
	
	/**
	 * IC2 Mappings
	 */
	private void ic2Mappings(){
		PELogger.logInfo("Loading IC2 Values");
		
		//Dusts
		addMapping(IC2Items.getItem("clayDust"), 32);
		addMapping(IC2Items.getItem("hydratedCoalDust"), (int) Math.round(64.125));
		addMapping(IC2Items.getItem("siliconDioxideDust"), 128); //TODO: Work out IC2's name for Silicon Dioxide
		addMapping(IC2Items.getItem("stoneDust"), 1);
		addMapping(IC2Items.getItem("obsidianDust"), 64);
		addMapping(IC2Items.getItem("lapiDust"), 864);
		//TODO: Add Energium Dust || EMC = 3961
		
		
		//Ingots
		addMapping(IC2Items.getItem("advIronIngot"), 1024); //TODO: Should be the same value as Steel
		addMapping(IC2Items.getItem("mixedMetalIngot"), 1008);
		
		//Plates
		addMapping(IC2Items.getItem("platecopper"), 128);
		addMapping(IC2Items.getItem("platetin"), 256);
		addMapping(IC2Items.getItem("platebronze"), 160);
		addMapping(IC2Items.getItem("plategold"), 2048);
		addMapping(IC2Items.getItem("plateiron"), 256);
		addMapping(IC2Items.getItem("plateadviron"), 1024); //TODO: Should be the same value as Steel
		addMapping(IC2Items.getItem("platelead"), 512);
		addMapping(IC2Items.getItem("plateobsidian"), 64);
		addMapping(IC2Items.getItem("platelapi"), 864);
		
		//Dense Plates
		addMapping(IC2Items.getItem("denseplatecopper"), 128 * 9);
		addMapping(IC2Items.getItem("denseplatetin"), 256 * 9);
		addMapping(IC2Items.getItem("denseplatebronze"), 160 * 9);
		addMapping(IC2Items.getItem("denseplategold"), 2048 * 9);
		addMapping(IC2Items.getItem("denseplateiron"), 256 * 9);
		addMapping(IC2Items.getItem("denseplateadviron"), 1024 * 9); //TODO: Should be the same value as Steel
		addMapping(IC2Items.getItem("denseplatelead"), 512 * 9);
		addMapping(IC2Items.getItem("denseplateobsidian"), 64 * 9);
		addMapping(IC2Items.getItem("denseplatelapi"), 864 * 9);
		
		//Casings
		addMapping(IC2Items.getItem("casingcopper"), 128/2);
		addMapping(IC2Items.getItem("casingtin"), 256/2);
		addMapping(IC2Items.getItem("casingbronze"), 160/2);
		addMapping(IC2Items.getItem("casinggold"), 2048/2);
		addMapping(IC2Items.getItem("casingiron"), 256/2);
		addMapping(IC2Items.getItem("casingadviron"), 1024/2); //TODO: should be the same value as Steel
		addMapping(IC2Items.getItem("casinglead"), 512/2);
		
		//Cables

		addMapping(IC2Items.getItem("copperCableItem"), 128);
		addMapping(IC2Items.getItem("insulatedCopperCableItem"), 160);
		
		addMapping(IC2Items.getItem("goldCableItem"), 2048);
		addMapping(IC2Items.getItem("insulatedGoldCableItem"), 2080);
		addMapping(IC2Items.getItem("doubleInsulatedGoldCableItem"), 2112);

		addMapping(IC2Items.getItem("ironCableItem"), 256);
		addMapping(IC2Items.getItem("insulatedIronCableItem"), 288);
		addMapping(IC2Items.getItem("doubleInsulatedIronCableItem"), 320);
		addMapping(IC2Items.getItem("trippleInsulatedIronCableItem"), 352);
		
		addMapping(IC2Items.getItem("tinCableItem"), 256);
		addMapping(IC2Items.getItem("insulatedTinCableItem"), 288);
		
		addMapping(IC2Items.getItem("glassFiberCableItem"), 840);

		addMapping(IC2Items.getItem("detectorCableItem"), 1888);
		addMapping(IC2Items.getItem("splitterCableItem"), 837);
		
		//Misc
		addMapping(IC2Items.getItem("electronicCircuit"), 1344);
		addMapping(IC2Items.getItem("advancedCircuit"), 4096);
		
	}
	
	private void notDetected(String modName){
		PELogger.logInfo(modName + " was not detected, not mapping values.");
	}

}
