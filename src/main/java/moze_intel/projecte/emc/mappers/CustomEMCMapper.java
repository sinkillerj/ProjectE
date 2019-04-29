package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import moze_intel.projecte.PECore;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NSSTag;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.IMappingCollector;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;

public class CustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {
	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, IResourceManager resourceManager) {
		//Tag items get processed first to be overwritten if the item is declared specifically 
		for (CustomEMCParser.CustomEMCEntry entry : CustomEMCParser.customEMCEntries.get("$Tag")) {
			for(Item itm: ((NSSTag)(entry.item)).getAllElements()){
				mapper.setValueBefore(new NSSItem(new ItemStack(itm)), entry.emc);
			}
		}
		for(String s: CustomEMCParser.customEMCEntries.keySet()){
			if(!s.equals("$Tag")){
				for (CustomEMCParser.CustomEMCEntry entry : CustomEMCParser.customEMCEntries.get(s)) {
					PECore.debugLog("Adding custom EMC value for {}: {}", entry.item, entry.emc);
					mapper.setValueBefore(entry.item, entry.emc);	
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
