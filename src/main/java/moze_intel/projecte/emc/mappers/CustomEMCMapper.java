package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.common.config.Configuration;

public class CustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Integer> {
	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		for (CustomEMCParser.CustomEMCEntry entry : CustomEMCParser.currentEntries.entries) {
			PELogger.logInfo("Adding custom EMC value for " + entry.nss + ": " + entry.emc);
			mapper.setValueBefore(entry.nss, entry.emc);
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
