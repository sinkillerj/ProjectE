package moze_intel.projecte.emc.mappers;

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
		
		if(Loader.isModLoaded("Applied Energistics 2")) ae2Mappings();
		if(Loader.isModLoaded("IC2")) ic2Mappings();
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
	}

}
