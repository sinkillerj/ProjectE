package moze_intel.projecte.emc.mappers;

import java.util.List;

import moze_intel.projecte.PECore;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.config.CustomEMCParser.CustomEMCEntry;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NSSOreDictionary;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class CustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {
	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, Configuration config) {
		for(String key: CustomEMCParser.customEMCEntries.keySet()){
			PECore.debugLog("Adding custom EMC value from mod " + key);
			for (CustomEMCParser.CustomEMCEntry entry : CustomEMCParser.customEMCEntries.get(key)) {
				PECore.debugLog("Adding custom EMC value for {}: {}", entry.nss, entry.emc);
				if(entry.nss instanceof NSSOreDictionary){
					 NSSOreDictionary ore = (NSSOreDictionary)entry.nss;
					 List<ItemStack> ores = ItemHelper.getODItems(ore.od);
					 for(ItemStack itm: ores){
						 NSSItem itm2 = (NSSItem) NSSItem.create(itm);
						 if(!CustomEMCParser.containsItem(itm2)){
							 mapper.setValueBefore(itm2, entry.emc);
						 }
					 }
				}else{
					mapper.setValueBefore(entry.nss, entry.emc);
				}
			}
		}
	}

	@Override
	public String getName() {
		return "CustomEMCMapper";
	}

	@Override
	public String getDescription() {
		return "Uses the `custom_emc.json` File to add EMC values.";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}
